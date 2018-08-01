package com.company.aws.elasticsearch.AutoIndex;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class AmazonElasticsearchServiceSample {

	private static String payload_snapshot = "{ \"type\": \"" + Constants.S3_SERVICE_NAME
			+ "\", \"settings\": { \"bucket\": \"" + Constants.BUCKET_NAME + "\", \"REGION\": \"" + Constants.REGION
			+ "\", \"role_arn\": \"" + Constants.SERVICE_ROLE + "\" } }";

	private static String snapshotPath = "/_snapshot/my-snapshot-repo";

	static int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
	static int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);
	static int i = 1000;

	private static String sampleDocument = "{\r\n" + "	\"Modell\": \"TestAuto_" + i + "\",\r\n"
			+ "	\"Marke\": \"Test" + i + "\",\r\n" + "	\"Auftrittsdatum\":  " + (int) (2022 + i - 1000) + ",\r\n"
			+ "	\"Letzter-Neupreis\": {\r\n" + "		\"Einheit\": \"EUR\",\r\n" + "		\"von\":  43.580,\r\n"
			+ "		\"bis\":  43.580\r\n" + "	},\r\n" + "	\"Leistung\": {\r\n" + "		\"Einheit\": \"PS\",\r\n"
			+ "		\"von\": " + (int) (i - 1000 + randomNum_2) + " ,\r\n" + "		\"bis\":   "
			+ (int) (i - 1000 + randomNum_2) + "        \r\n" + "	},\r\n" + "	\"CO2-Ausstoss\": {\r\n"
			+ "		\"Einheit\": \"g/km\",\r\n" + "		\"von\": " + (int) (i - 1000 + randomNum) + ",\r\n"
			+ "		\"bis\": " + (int) (i - 1000 + randomNum) + " \r\n" + "	},\r\n"
			+ "	\"Aufbauarten\": \"Roadster\",\r\n" + "	\"Kraftstoff\": \"Super\",\r\n"
			+ "	\"Uebersicht\": \"testing\"\r\n" + "}";

	private static String indexingPath = "/autos/auto";

	// The default credential provider chain looks for credentials in this order:
	// 1. Environment variables–AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY.
	static final AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
	// 2. Java system properties–aws.accessKeyId and aws.secretKey
	// static final AWSCredentialsProvider credentialsProvider = new
	// SystemPropertiesCredentialsProvider();
	// 3. The default credential profiles file– typically located at
	// ~/.aws/credentials
	// static final AWSCredentialsProvider credentialsProvider = new
	// DefaultAWSCredentialsProviderChain();

	public static void main(String[] args) throws IOException, InterruptedException {

		for (int i = 1000; i < 1200; i++) {

			RestClient esClient = esClient(Constants.ES_SERVICE_NAME, Constants.REGION);

			int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);

			String payload = "{\r\n" + "	\"Modell\": \"TestAuto_" + i + "\",\r\n" + "	\"Marke\": \"Test" + i
					+ "\",\r\n" + "	\"Auftrittsdatum\":  " + (int) (2022 + i - 1000) + ",\r\n"
					+ "	\"Letzter-Neupreis\": {\r\n" + "		\"Einheit\": \"EUR\",\r\n"
					+ "		\"von\":  43.580,\r\n" + "		\"bis\":  43.580\r\n" + "	},\r\n" + "	\"Leistung\": {\r\n"
					+ "		\"Einheit\": \"PS\",\r\n" + "		\"von\": " + (int) (i - 1000 + randomNum_2) + " ,\r\n"
					+ "		\"bis\":   " + (int) (i - 1000 + randomNum_2) + "        \r\n" + "	},\r\n"
					+ "	\"CO2-Ausstoss\": {\r\n" + "		\"Einheit\": \"g/km\",\r\n" + "		\"von\": "
					+ (int) (i - 1000 + randomNum) + ",\r\n" + "		\"bis\": " + (int) (i - 1000 + randomNum)
					+ " \r\n" + "	},\r\n" + "	\"Aufbauarten\": \"Roadster\",\r\n" + "	\"Kraftstoff\": \"Super\",\r\n"
					+ "	\"Uebersicht\": \"testing\"\r\n" + "}";

			// Register a snapshot repository
			// HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
			Map<String, String> params = Collections.emptyMap();
			// Response response = esClient.performRequest("PUT", snapshotPath, params,
			// entity);
			// System.out.println(response.toString());

			// Index a document
			HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);

			Response response = esClient.performRequest("PUT", indexingPath + "/" + i, params, entity);
			System.out.println(response.toString());

			Thread.sleep(2000);
		}
	}

	// Adds the interceptor to the ES REST client
	public static RestClient esClient(String serviceName, String region) {
		AWS4Signer signer = new AWS4Signer();
		signer.setServiceName(serviceName);
		signer.setRegionName(region);
		HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer,
				credentialsProvider);
		return RestClient.builder(HttpHost.create(Constants.AES_ENDPOINT))
				.setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
	}
}
