package com.company.aws.elasticsearch.VehicleIndex;

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

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class Put3LineDiagramsInDashboardTest {

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
	UUID speedLineDiagramId;
	UUID temperatureLineDiagramId;
	UUID rpmLineDiagramId;
	UUID dashboardId;

	Map<String, String> valuesMap;
	StrSubstitutor sub;
	String payload;
	ClassLoader classLoader;

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
	}

	@Test
	public void put_3_different_line_diagrams_in_dashboard()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		//
		// Diagram 1: put_line_diagram_with_speed_as_y_param
		//
		speedLineDiagramId = UUID.randomUUID();

		valuesMap = new HashMap<>();
		// title
		valuesMap.put("visualization-titel", "Kolloqium-demo-Geschwindigkeit-uber-zeit"); // ${visualization-titel}
		// fields
		// ${first-x-param}
		valuesMap.put("first-x-param", "auftrittsdatum");
		// ${first-y-param}
		valuesMap.put("first-y-param", "fahrzeuggeschwindigkeit");
		// valuesMap.put("first-y-param", "kuehlmitteltemperatur");
		// valuesMap.put("first-y-param", "motordrehzahl");
		// x-interval
		valuesMap.put("x-interval", "s"); // ${first-x-param}
		// Line Width
		valuesMap.put("line-width", "2"); // ${line-width}

		// labels
		// ${first-x-param-label}
		valuesMap.put("first-x-param-label", "Zeit (s)");
		// ${second-y-param-label}
		valuesMap.put("first-y-param-label", "Geschwindigkeit (km/h)");
		// valuesMap.put("first-y-param-label", "Kuelmitteltemperatur (°C)");
		// valuesMap.put("first-y-param-label", "Motordrehzahl (rpm)");
		// fontSizes
		valuesMap.put("font-size-x", "20px"); // ${font-size-x}
		valuesMap.put("font-size-y", "15px"); // ${font-size-y}
		// Scale type: linear, log or square root function
		valuesMap.put("scale-type-y", "linear"); // ${scale-type}
		// rotate-x
		valuesMap.put("rotate-x", "0"); // ${rotate-x}

		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID_VEHICLE_INDEX); // ${search-id}

		classLoader = getClass().getClassLoader();
		sub = new StrSubstitutor(valuesMap);
		payload = sub.replace(IOUtils.toString(
				classLoader.getResourceAsStream("fixtures_vehicleIndex/put-line-diagram-1-param-x-time.json"),
				"UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + speedLineDiagramId, params,
				entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		System.out.println("speed-line-diagram-id: " + speedLineDiagramId);

		//
		// Diagram 2: put_line_diagram_with_rpm_as_y_param
		//
		rpmLineDiagramId = UUID.randomUUID();

		valuesMap = new HashMap<>();
		// title
		valuesMap.put("visualization-titel", "Kolloqium-demo-motordrehzahl-uber-zeit"); // ${visualization-titel}
		// fields
		// ${first-x-param}
		valuesMap.put("first-x-param", "auftrittsdatum");
		// ${first-y-param}
		valuesMap.put("first-y-param", "motordrehzahl");
		// x-interval
		valuesMap.put("x-interval", "s"); // ${first-x-param}
		// labels
		// ${first-x-param-label}
		valuesMap.put("first-x-param-label", "Zeit (s)");
		// ${second-y-param-label}
		valuesMap.put("first-y-param-label", "Motordrehzahl (rpm)");
		// fontSizes
		valuesMap.put("font-size-x", "20px"); // ${font-size-x}
		valuesMap.put("font-size-y", "15px"); // ${font-size-y}
		// Scale type: linear, log or square root function
		valuesMap.put("scale-type-y", "linear"); // ${scale-type}
		// rotate-x
		valuesMap.put("rotate-x", "0"); // ${rotate-x}
		// Line Width
		valuesMap.put("line-width", "2");

		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID_VEHICLE_INDEX); // ${search-id}

		classLoader = getClass().getClassLoader();
		sub = new StrSubstitutor(valuesMap);
		payload = sub.replace(IOUtils.toString(
				classLoader.getResourceAsStream("fixtures_vehicleIndex/put-line-diagram-1-param-x-time.json"),
				"UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + rpmLineDiagramId, params, entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		System.out.println("rpm-line-diagram-id: " + rpmLineDiagramId);

		//
		// Diagram 3: put_line_diagram_with_temperature_as_y_param
		//
		temperatureLineDiagramId = UUID.randomUUID();

		valuesMap = new HashMap<>();
		// title
		valuesMap.put("visualization-titel", "Kolloqium-demo-kueltemperatur-uber-zeit"); // ${visualization-titel}
		// fields
		// ${first-x-param}
		valuesMap.put("first-x-param", "auftrittsdatum");
		// ${first-y-param}
		valuesMap.put("first-y-param", "kuehlmitteltemperatur");
		// x-interval
		valuesMap.put("x-interval", "s"); // ${first-x-param}
		// labels
		// ${first-x-param-label}
		valuesMap.put("first-x-param-label", "Zeit (s)");
		// ${second-y-param-label}
		valuesMap.put("first-y-param-label", "Kuelmitteltemperatur (°C)");
		// fontSizes
		valuesMap.put("font-size-x", "20px"); // ${font-size-x}
		valuesMap.put("font-size-y", "15px"); // ${font-size-y}
		// Scale type: linear, log or square root function
		valuesMap.put("scale-type-y", "linear"); // ${scale-type}
		// rotate-x
		valuesMap.put("rotate-x", "0"); // ${rotate-x}
		// Line Width
		valuesMap.put("line-width", "2");

		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID_VEHICLE_INDEX); // ${search-id}

		classLoader = getClass().getClassLoader();
		sub = new StrSubstitutor(valuesMap);
		payload = sub.replace(IOUtils.toString(
				classLoader.getResourceAsStream("fixtures_vehicleIndex/put-line-diagram-1-param-x-time.json"),
				"UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + temperatureLineDiagramId, params,
				entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		System.out.println("temperature-line-diagram-id: " + temperatureLineDiagramId);

		//
		// Step 4: Create the dash board, embed the visualization inside it, set the
		// background color.
		//
		dashboardId = UUID.randomUUID();

		Map<String, String> dashboardValuesMap = new HashMap<>();
		// title
		dashboardValuesMap.put("dashboard-titel", "Kolloqium-dashboard-final");// ${dashboard-titel}
		// visualization id
		dashboardValuesMap.put("visualization-1-id", speedLineDiagramId.toString()); // ${visualization-1-id}
		dashboardValuesMap.put("visualization-2-id", rpmLineDiagramId.toString());// ${visualization-2-id}
		dashboardValuesMap.put("visualization-3-id", temperatureLineDiagramId.toString());// ${visualization-3-id}
		// dark theme boolean
		dashboardValuesMap.put("dark-theme", "false"); // ${dark-theme}
		// hidePanelTitles boolean
		dashboardValuesMap.put("hide-panel-titles", "true"); // ${hide-panel-titles}
		// useMargins boolean, for many visualizations on the same dash board
		dashboardValuesMap.put("use-margin", "true"); // ${use-margin}
		dashboardValuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID_VEHICLE_INDEX); // ${search-id}

		sub = new StrSubstitutor(dashboardValuesMap);
		classLoader = getClass().getClassLoader();
		String dashboardPayload = sub.replace(IOUtils.toString(
				classLoader.getResourceAsStream("fixtures_vehicleIndex/put-dashboard-payload.json"), "UTF-8"));

		entity = new NStringEntity(dashboardPayload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/dashboard:" + dashboardId, params, entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		System.out.println("dashboard-id: " + dashboardId);
		//
		// Step 3: Get sharable dash board link.
		//
		// link
		Map<String, String> shareableLinkValues = new HashMap<>();
		shareableLinkValues.put("dashboard-uuid", dashboardId.toString());
		sub = new StrSubstitutor(shareableLinkValues);
		System.out.println("Share saved dashboard Link: \n" + sub.replace(Constants.DASHBOARD_SHAREABLE_LINK));
		// embedded iframe
		Map<String, String> iframeValues = new HashMap<>();
		iframeValues.put("dashboard-uuid", dashboardId.toString());
		iframeValues.put("iframe-height", Integer.toString(1200));
		iframeValues.put("iframe-width", Integer.toString(1300));
		sub = new StrSubstitutor(iframeValues);
		System.out.println("Share saved dashboard embedded iframe: \n"
				+ sub.replace(Constants.DASHBOARD_SHAREABLE_EMBEDDED_IFRAME));

	}
}