package com.company.aws.elasticsearch.VehicleIndex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class PutVisualizationTest {

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
	UUID areaDiagramId;

	@Before
	public void setUp() throws IOException {

		Properties log4jProp = new Properties();
		log4jProp.setProperty("log4j.rootLogger", "WARN");
		PropertyConfigurator.configure(log4jProp);

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
	public void put_line_diagram_with_3_params()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		lineDiagramId = UUID.randomUUID();

		Map<String, String> valuesMap = new HashMap<>();
		// title
		valuesMap.put("visualization-titel", "drei-parameter-line-diagram-reference-3"); // ${visualization-titel}
		// fields
		valuesMap.put("first-x-param", "auftrittsdatum"); // ${first-x-param}
		valuesMap.put("first-y-param", "kuehlmitteltemperatur"); // ${first-y-param}
		valuesMap.put("second-y-param", "motordrehzahl"); // ${second-y-param}
		valuesMap.put("third-y-param", "fahrzeuggeschwindigkeit"); // ${third-y-param}
		// x-interval
		valuesMap.put("x-interval", "s"); // ${first-x-param}
		// labels
		valuesMap.put("first-x-param-label", "Zeit (s)"); // ${first-x-param-label}
		valuesMap.put("first-y-param-label", "Kuelmitteltemperatur (°C)"); // ${first-y-param-label}
		valuesMap.put("second-y-param-label", "Motordrehzahl (rpm)"); // ${second-y-param-label}
		valuesMap.put("third-y-param-label", "Geschwindigkeit (km/h)"); // ${third-y-param-label}
		// fontSizes
		valuesMap.put("font-size-x", "20px"); // ${font-size-x}
		valuesMap.put("font-size-y", "15px"); // ${font-size-y}
		// Scale type: linear, log or square root function
		valuesMap.put("scale-type", "square root"); // ${scale-type}

		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID_VEHICLE_INDEX); // ${search-id}

		ClassLoader classLoader = getClass().getClassLoader();
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String payload = sub.replace(IOUtils.toString(
				classLoader.getResourceAsStream("fixtures_vehicleIndex/put-line-diagram-3-params.json"), "UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + lineDiagramId, params, entity);
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
		System.out.println(response.toString());
		System.out.println("line-diagrmm-id: " + lineDiagramId);
	}
}