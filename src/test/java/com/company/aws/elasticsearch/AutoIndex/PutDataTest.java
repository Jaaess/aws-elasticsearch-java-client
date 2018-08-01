package com.company.aws.elasticsearch.AutoIndex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

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
import com.campany.aws.elasticsearch.general.Constants;
import com.company.aws.elasticsearch.AutoIndex.AWSRequestSigningApacheInterceptor;

public class PutDataTest {

	private static String payload_snapshot = "{ \"type\": \"" + Constants.S3_SERVICE_NAME
			+ "\", \"settings\": { \"bucket\": \"" + Constants.BUCKET_NAME + "\", \"REGION\": \"" + Constants.REGION
			+ "\", \"role_arn\": \"" + Constants.SERVICE_ROLE + "\" } }";

	private static String snapshotPath = "/_snapshot/my-snapshot-repo";

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

	RestClient esClient;
	HttpEntity entity;
	Response response;
	Map<String, String> params;

	@Before
	public void initialize() throws IOException {

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

	/*@Test
	public void perform_100_posts_old()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		for (int i = 1000; i < 1100; i++) {

			int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_3 = ThreadLocalRandom.current().nextInt(0, 30000);
			int random_day = ThreadLocalRandom.current().nextInt(1, 28);
			int random_month = ThreadLocalRandom.current().nextInt(1, 12);
			int random_year = ThreadLocalRandom.current().nextInt(2000, 2018);
			String random_date = random_year + "-" + random_month + "-" + random_day;

			String payload = "{\r\n" + "	\"Modell\": \"TestAuto_" + i + "\",\r\n" + "	\"Marke\": \"Test" + i
					+ "\",\r\n" + "	\"Auftrittsdatum\":  " + (int) (2022 + i - 1000) + ",\r\n"
					+ "	\"Letzter-Neupreis\": {\r\n" + "		\"Einheit\": \"EUR\",\r\n" + "		\"von\": "
					+ (int) (i - randomNum_3 + 54580) + " ,\r\n" + "		\"bis\":  "
					+ (int) (i - randomNum_3 + 54580) + "\r\n" + "	},\r\n" + "	\"Leistung\": {\r\n"
					+ "		\"Einheit\": \"PS\",\r\n" + "		\"von\": " + (int) (i - 1000 + randomNum_2) + " ,\r\n"
					+ "		\"bis\":   " + (int) (i - 1000 + randomNum_2) + "        \r\n" + "	},\r\n"
					+ "	\"CO2-Ausstoss\": {\r\n" + "		\"Einheit\": \"g/km\",\r\n" + "		\"von\": "
					+ (int) (i - 1000 + randomNum) + ",\r\n" + "		\"bis\": " + (int) (i - 1000 + randomNum)
					+ " \r\n" + "	},\r\n" + "	\"Aufbauarten\": \"Roadster\",\r\n" + "	\"Kraftstoff\": \"Super\",\r\n"
					+ "	\"Uebersicht\": \"testing\",\r\n" + "	\"registrationDate\": \"" + random_date + "\",\r\n"
					+ "	\"registrationDate_yy-mm-dd\": \"" + random_date + "\"}";

			// Index a document
			entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
			response = esClient.performRequest("PUT", indexingPath + "/" + i, params, entity);
			System.out.println(response.toString());
			Thread.sleep(500);
		}
	}*/

	@Test
	public void perform_100_posts()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		for (int i = 1000; i < 1100; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_3 = ThreadLocalRandom.current().nextInt(0, 30000);
			int random_day = ThreadLocalRandom.current().nextInt(1, 28);
			int random_month = ThreadLocalRandom.current().nextInt(1, 12);
			int random_year = ThreadLocalRandom.current().nextInt(2000, 2018);
			String random_date = random_year + "-" + random_month + "-" + random_day;

			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("modell", "modell-" + i);
			valuesMap.put("marke", "marke-" + i);
			valuesMap.put("auftrittsdatum", Integer.toString(2022 + i - 1000));
			valuesMap.put("letzter-neupreis-von", Integer.toString(i - randomNum_3 + 54580));
			valuesMap.put("letzter-neupreis-bis", Integer.toString(i - randomNum_3 + 54580));
			valuesMap.put("leistung-von", Integer.toString(i - 1000 + randomNum_2));
			valuesMap.put("leistung-bis", Integer.toString(i - 1000 + randomNum_2));
			valuesMap.put("co2-austoss-von", Integer.toString(i - 1000 + randomNum));
			valuesMap.put("co2-austoss-bis", Integer.toString(i - 1000 + randomNum));
			valuesMap.put("aufbauarten", "Roadster");
			valuesMap.put("kraftstoff", "Super");
			valuesMap.put("uebersicht", "testing...");
			valuesMap.put("registration-date", random_date);
			valuesMap.put("registration-date-yy-mm-dd", random_date);

			ClassLoader classLoader = getClass().getClassLoader();
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			String payload = sub.replace(
					IOUtils.toString(classLoader.getResourceAsStream("fixtures/put-auto-payload.json"), "UTF-8"));

			// Index a document
			entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
			response = esClient.performRequest("PUT", indexingPath + "/" + i, params, entity);
			System.out.println(response.toString());
			Thread.sleep(500);
		}
	}

	@Test
	public void delete_100_added_posts_AWS() throws ClientProtocolException, IOException, InterruptedException {
		int i = 1000;
		while (i <= 1100) {
			try {
				Response response = esClient.performRequest("DELETE", indexingPath + "/" + i);
				System.out.println(response.toString());
				assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
			} catch (Exception e) {

			}
			i++;
		}
	}
}
