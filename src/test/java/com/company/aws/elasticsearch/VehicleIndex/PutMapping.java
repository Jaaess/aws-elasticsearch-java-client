package com.company.aws.elasticsearch.VehicleIndex;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class PutMapping {

	static final AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
	private static String snapshotPath = "/_snapshot/my-snapshot-repo";
	private static String payload_snapshot = "{ \"type\": \"" + Constants.S3_SERVICE_NAME
			+ "\", \"settings\": { \"bucket\": \"" + Constants.BUCKET_NAME + "\", \"REGION\": \"" + Constants.REGION
			+ "\", \"role_arn\": \"" + Constants.SERVICE_ROLE + "\" } }";

	RestClient esClient;
	HttpEntity entity;
	Response response;
	Map<String, String> params;

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
	public void create_index_and_put_mappings()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		// create new index
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			String payload_index = IOUtils.toString(classLoader.getResourceAsStream("fixtures_vehicleIndex/put-index.json"),
					"UTF-8");

			entity = new NStringEntity(payload_index, ContentType.APPLICATION_JSON);
			response = esClient.performRequest("PUT", Constants.AES_ENDPOINT + "/" + Constants.INDEX_NAME, params,
					entity);
			System.out.println(response.toString());
		} catch (ResponseException e) {
			// index already created or type already exist, than skip
		}

		// put mapping
		String payload = IOUtils.toString(classLoader.getResourceAsStream("fixtures_vehicleIndex/vehicle-mappings.json"), "UTF-8");

		entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
		response = esClient.performRequest("PUT", Constants.AES_ENDPOINT + Constants.MAPPING_PATH, params, entity);
		System.out.println(response.toString());
	}
}
