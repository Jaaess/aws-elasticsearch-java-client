package com.company.aws.elasticsearch;

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

public class AutomaticVisualizationTest {

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
	UUID id;

	@Before
	public void initialize() throws IOException {

		id = UUID.randomUUID();

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

		// Register a snapshot repository
		entity = new NStringEntity(payload_snapshot, ContentType.APPLICATION_JSON);
		params = Collections.emptyMap();
		response = esClient.performRequest("PUT", snapshotPath, params, entity);
		System.out.println(response.toString());
	}

	
	@Test
	public void put_line_diagramm()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {
		
		Map<String, String> valuesMap = new HashMap<>();
		//titel
		valuesMap.put("visualization-titel", "line-diagramm-reference-1");
		//fields
		valuesMap.put("first-x-param", "registrationDate_yy-mm-dd");
		valuesMap.put("first-y-param", "CO2-Ausstoss.bis");
		valuesMap.put("second-y-param", "Leistung.bis");
		//labels
		valuesMap.put("first-x-param-label", "registeration date");
		valuesMap.put("first-y-param-label", "Co2 Austoﬂ (g/Km)");
		valuesMap.put("second-y-param-label", "Leistung (PS)");
		
		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID);

		ClassLoader classLoader = getClass().getClassLoader();
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String payload = sub.replace(
				IOUtils.toString(classLoader.getResourceAsStream("fixtures/put-area-diagramm-payload.json"), "UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + id, params, entity);
		System.out.println(response.toString());
	}
	
	@Test
	public void put_area_diagramm()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		Map<String, String> valuesMap = new HashMap<>();
		//titel
		valuesMap.put("visualization-titel", "area-diagramm-reference-1");
		//fields
		valuesMap.put("first-x-param", "registrationDate_yy-mm-dd");
		valuesMap.put("first-y-param", "CO2-Ausstoss.bis");
		valuesMap.put("second-y-param", "Leistung.bis");
		//labels
		valuesMap.put("first-x-param-label", "register date");
		valuesMap.put("first-y-param-label", "Co2 Austoﬂ (g/Km)");
		valuesMap.put("second-y-param-label", "Leistung (PS)");
		
		valuesMap.put("search-id", Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID);

		ClassLoader classLoader = getClass().getClassLoader();
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String payload = sub.replace(
				IOUtils.toString(classLoader.getResourceAsStream("fixtures/put-area-diagramm-payload.json"), "UTF-8"));

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + id, params, entity);
		System.out.println(response.toString());
	}

	@Test
	public void delete_last_put_line_diagram() throws ClientProtocolException, IOException, InterruptedException {

		Response response = esClient.performRequest("DELETE", indexingPath + "/visualization:" + id);
		System.out.println(response.toString());
		assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
	}
}