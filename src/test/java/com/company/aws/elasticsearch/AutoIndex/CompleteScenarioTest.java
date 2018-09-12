package com.company.aws.elasticsearch.AutoIndex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class CompleteScenarioTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompleteScenarioTest.class);

	static final AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
	private static String indexingPath = "/.kibana/doc";
	private static String snapshotPath = "/_snapshot/my-snapshot-repo";
	private static String payload_snapshot = "{ \"type\": \"" + Constants.S3_SERVICE_NAME
			+ "\", \"settings\": { \"bucket\": \"" + Constants.BUCKET_NAME + "\", \"REGION\": \"" + Constants.REGION
			+ "\", \"role_arn\": \"" + Constants.SERVICE_ROLE + "\" } }";

	RestClient esClient;
	HttpEntity entity;
	Response response;
	Map<String, String> params;
	UUID lineDiagramId;
	UUID dashboardId;

	@Before
	public void setUp() throws IOException {

		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(Constants.ES_SERVICE_NAME);
		signer.setRegionName(Constants.REGION);
		HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(Constants.ES_SERVICE_NAME, signer,
				credentialsProvider);
		esClient = RestClient.builder(HttpHost.create(Constants.AES_ENDPOINT))
				.setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();

		// register a snapshot repository
		entity = new NStringEntity(payload_snapshot, ContentType.APPLICATION_JSON);
		params = Collections.emptyMap();
		response = esClient.performRequest("PUT", snapshotPath, params, entity);
		System.out.println(response.toString());
		LOGGER.debug("response snapshot", response.toString());
	}

	@Test
	public void create_visualization_create_dashbord_share()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {
		//
		// Step 1: Create Visualization.
		//
		lineDiagramId = UUID.randomUUID();

		Map<String, String> valuesMap = new HashMap<>();
		// title
		valuesMap.put("visualization-titel", "sceanario-complet-test-visualization_preis_logScale_8");
		// fields
		valuesMap.put("first-x-param", "registrationDate_yy-mm-dd");
		// valuesMap.put("first-y-param", "CO2-Ausstoss.bis");
		valuesMap.put("first-y-param", "Letzter-Neupreis.bis");
		valuesMap.put("second-y-param", "Leistung.bis");
		// labels
		valuesMap.put("first-x-param-label", "registration date");
		// valuesMap.put("first-y-param-label", "Co2 Austoﬂ (g/Km)");
		valuesMap.put("first-y-param-label", "Letzter-Neupreis.bis (Euro)");
		valuesMap.put("second-y-param-label", "Leistung (PS)");
		// Scale type: linear, log or square root function
		valuesMap.put("scale-type", "log");
		// fontSizes
		valuesMap.put("font-size-x", "20px");
		valuesMap.put("font-size-y", "15px");

		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID);

		ClassLoader classLoader = getClass().getClassLoader();
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String payload = sub.replace(
				IOUtils.toString(classLoader.getResourceAsStream("fixtures/put-line-diagram-payload.json"), "UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + lineDiagramId, params, entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		LOGGER.debug("response create visualization", response.toString());
		System.out.println("line-diagrmm-id: " + lineDiagramId);
		//
		// Step 2: Create the dash board, embed the visualization inside it, set the
		// background color.
		//
		dashboardId = UUID.randomUUID();

		Map<String, String> dashboardValuesMap = new HashMap<>();
		// title
		dashboardValuesMap.put("dashboard-titel", "sceanario-complet-test-dashboard_logScale_4");
		// visualization id
		dashboardValuesMap.put("visualization-id", lineDiagramId.toString());
		// dark theme boolean
		dashboardValuesMap.put("dark-theme", "true");
		// hidePanelTitles boolean
		dashboardValuesMap.put("hide-panel-titles", "true");
		// useMargins boolean, for many visualizations on the same dash board
		dashboardValuesMap.put("use-margin", "true");
		// grid-data
		dashboardValuesMap.put("grid-x", Integer.toString(0));
		dashboardValuesMap.put("grid-y", Integer.toString(0));
		dashboardValuesMap.put("grid-w", Integer.toString(12));
		dashboardValuesMap.put("grid-h", Integer.toString(8));
		dashboardValuesMap.put("grid-i", Integer.toString(1));

		sub = new StrSubstitutor(dashboardValuesMap);
		String dashboardPayload = sub.replace(IOUtils
				.toString(classLoader.getResourceAsStream("fixtures/put-dashboard-1-vis-payload.json"), "UTF-8"));

		entity = new NStringEntity(dashboardPayload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/dashboard:" + dashboardId, params, entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		LOGGER.info("response create dashboard", response.toString());
		System.out.println("dashboard-id: " + dashboardId);
		//
		// Step 3: Get shareable dash board link.
		//
		// link
		Map<String, String> shareableLinkValues = new HashMap<>();
		shareableLinkValues.put("dashboard-uuid", dashboardId.toString());
		sub = new StrSubstitutor(shareableLinkValues);
		System.out.println("Share saved dashboard Link: \n" + sub.replace(Constants.DASHBOARD_SHAREABLE_LINK));
		// embedded iframe
		Map<String, String> iframeValues = new HashMap<>();
		iframeValues.put("dashboard-uuid", dashboardId.toString());
		iframeValues.put("iframe-height", Integer.toString(800));
		iframeValues.put("iframe-width", Integer.toString(1300));
		sub = new StrSubstitutor(iframeValues);
		System.out.println("Share saved dashboard embedded iframe: \n"
				+ sub.replace(Constants.DASHBOARD_SHAREABLE_EMBEDDED_IFRAME));
		LOGGER.debug("shareable dashboard", sub.replace(Constants.DASHBOARD_SHAREABLE_EMBEDDED_IFRAME));

	}

}
