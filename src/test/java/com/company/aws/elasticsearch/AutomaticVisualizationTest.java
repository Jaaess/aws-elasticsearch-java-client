package com.company.aws.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
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
	public void put_line_diagram()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		String first_X_param = "Auftrittsdatum";
		String first_Y_param = "Letzter-Neupreis.bis";
		String second_Y_param = "Leistung.bis";
		String first_X_param_label = "Auftrittsdatum";
		String first_Y_param_label = "Letzter-Neupreis";
		String second_Y_param_label = "Leistung (PS)";

		String payload = "{\r\n" + "        \"type\": \"visualization\",\r\n"
				+ "        \"updated_at\": \"2018-07-11T20:54:04.182Z\",\r\n" + "        \"visualization\": {\r\n"
				+ "            \"title\": \"_" + "line_diagram_X_" + first_X_param + "_Y_" + first_Y_param + "_Y_"
				+ second_Y_param + "_test" + "_\",\r\n"
				+ "            \"visState\": \"{\\\"title\\\":\\\" to complete\\\",\\\"type\\\":\\\"line\\\",\\\"params\\\":{\\\"type\\\":\\\"line\\\",\\\"grid\\\":{\\\"categoryLines\\\":false,\\\"style\\\":{\\\"color\\\":\\\"#eee\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\"},\\\"categoryAxes\\\":[{\\\"id\\\":\\\"CategoryAxis-1\\\",\\\"type\\\":\\\"category\\\",\\\"position\\\":\\\"bottom\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"truncate\\\":100,\\\"rotate\\\":75,\\\"filter\\\":true},\\\"title\\\":{}}],\\\"valueAxes\\\":[{\\\"id\\\":\\\"ValueAxis-1\\\",\\\"name\\\":\\\"LeftAxis-1\\\",\\\"type\\\":\\\"value\\\",\\\"position\\\":\\\"left\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\",\\\"mode\\\":\\\"normal\\\",\\\"defaultYExtents\\\":false,\\\"setYExtents\\\":false},\\\"labels\\\":{\\\"show\\\":true,\\\"rotate\\\":0,\\\"filter\\\":false,\\\"truncate\\\":100},\\\"title\\\":{\\\"text\\\":\\\""
				+ first_Y_param_label
				+ "\\\"}}],\\\"seriesParams\\\":[{\\\"show\\\":\\\"true\\\",\\\"type\\\":\\\"line\\\",\\\"mode\\\":\\\"normal\\\",\\\"data\\\":{\\\"label\\\":\\\""
				+ first_Y_param_label
				+ "\\\",\\\"id\\\":\\\"1\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\",\\\"drawLinesBetweenPoints\\\":true,\\\"showCircles\\\":true,\\\"lineWidth\\\":3,\\\"interpolate\\\":\\\"linear\\\"},{\\\"show\\\":true,\\\"mode\\\":\\\"normal\\\",\\\"type\\\":\\\"line\\\",\\\"drawLinesBetweenPoints\\\":true,\\\"showCircles\\\":true,\\\"interpolate\\\":\\\"linear\\\",\\\"lineWidth\\\":2,\\\"data\\\":{\\\"id\\\":\\\"4\\\",\\\"label\\\":\\\"Leistung (PS)\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\"}],\\\"addTooltip\\\":true,\\\"addLegend\\\":true,\\\"legendPosition\\\":\\\"top\\\",\\\"times\\\":[],\\\"addTimeMarker\\\":false,\\\"orderBucketsBySum\\\":false},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"avg\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{\\\"field\\\":\\\""
				+ first_Y_param + "\\\",\\\"customLabel\\\":\\\"" + first_Y_param_label
				+ "\\\"}},{\\\"id\\\":\\\"3\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\""
				+ first_X_param
				+ "\\\",\\\"otherBucket\\\":false,\\\"otherBucketLabel\\\":\\\"Other\\\",\\\"missingBucket\\\":false,\\\"missingBucketLabel\\\":\\\"Missing\\\",\\\"size\\\":1000000000,\\\"order\\\":\\\"asc\\\",\\\"orderBy\\\":\\\"_term\\\",\\\"customLabel\\\":\\\""
				+ first_X_param_label
				+ "\\\"}},{\\\"id\\\":\\\"4\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"avg\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{\\\"field\\\":\\\""
				+ second_Y_param + "\\\",\\\"customLabel\\\":\\\"" + second_Y_param_label + "\\\"}}]}\",\r\n"
				+ "            \"uiStateJSON\": \"{\\\"vis\\\":{\\\"colors\\\":{\\\"" + first_Y_param_label
				+ "\\\":\\\"#7CFC00\\\",\\\"" + second_Y_param_label + "\\\":\\\"#E24D42\\\"}}}\",\r\n"
				+ "            \"description\": \"\",\r\n" + "            \"version\": 1,\r\n"
				+ "            \"kibanaSavedObjectMeta\": {\r\n"
				+ "                \"searchSourceJSON\": \"{\\\"index\\\":\\\""
				+ Constants.KibanaSavedObjectMeta_searchSourceJSON_Search_ID
				+ "\\\",\\\"filter\\\":[],\\\"query\\\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"}}\"\r\n"
				+ "            }\r\n" + "        }\r\n" + "    }";

		// Index a document
		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", indexingPath + "/visualization:" + id, params, entity);
		System.out.println(response.toString());
	}

	@Test
	public void put_area_diagram()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		ClassLoader classLoader = getClass().getClassLoader();
		String payload = IOUtils
				.toString(classLoader.getResourceAsStream("fixtures/put_area_diagram_payload.json"), "UTF-8")
				.replace("first_X_param", "Auftrittsdatum").replace("first_Y_param", "Letzter-Neupreis.bis")
				.replace("second_Y_param", "Leistung.bis").replace("first_X_param_label", "Auftrittsdatum")
				.replace("first_Y_param_label", "Letzter-Neupreis").replace("second_Y_param_label", "Leistung (PS)");

		// Index a document
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
