package com.company.aws.elasticsearch.VehicleIndex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
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
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.company.aws.elasticsearch.general.AWSRequestSigningApacheInterceptor;
import com.company.aws.elasticsearch.general.Constants;

public class PutDataTest {

	private static String payload_snapshot = "{ \"type\": \"" + Constants.S3_SERVICE_NAME
			+ "\", \"settings\": { \"bucket\": \"" + Constants.BUCKET_NAME + "\", \"REGION\": \"" + Constants.REGION
			+ "\", \"role_arn\": \"" + Constants.SERVICE_ROLE + "\" } }";

	private static String snapshotPath = "/_snapshot/my-snapshot-repo";

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

	public class City {
		private String latitude;
		private String longitude;

		public City(String latitude, String longitude) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public String getLongitude() {
			return longitude;
		}
	}

	@Before
	public void initialize() throws IOException {

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

	private City getRandomCity() {
		// prepare a hash map with coordinates of German cities.
		HashMap<String, String> cities = new HashMap<String, String>();
		cities.put("Grimma, Saxony, Germany", "51.236443;12.720231");
		cities.put("Tübingen, Baden-Württemberg, Germany", "48.521637;9.057645");
		cities.put("Mainz, Rhineland-Palatinate, Germany", "49.992863;8.247253");
		cities.put("Münster, North Rhine-Westphalia, Germany", "51.961563;7.628202");
		cities.put("Cologne, North Rhine-Westphalia, Germany", "50.933594;6.961899");
		cities.put("Dresden", "51.050407;13.737262");
		cities.put("Karlsruhe, Baden-Württemberg, Germany", "49.006889;8.403653");
		cities.put("Wolfsburg, Lower Saxony, Germany", "52.427547;10.780420");
		cities.put("Dortmund, North Rhine-Westphalia, Germany", "51.514244;7.468429");
		cities.put("Weimar, Thuringia, Germany", "50.979492;11.323544");
		cities.put("Frankfurt, Germany", "50.110924;8.682127");
		cities.put("Berlin, Germany", "48.137154;11.576124");
		cities.put("Munich, Bavaria, Germany", "52.520008;13.404954");

		// Get a random entry from the HashMap.
		Object[] crunchifyKeys = cities.keySet().toArray();
		Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];

		return new City(cities.get(key).split(";")[0], cities.get(key).split(";")[1]);
	}

	@Test
	public void perform_100_posts()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		for (int i = 1000; i < 2800; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);
			int randomNum_3 = ThreadLocalRandom.current().nextInt(0, 30000);
			// int random_day = ThreadLocalRandom.current().nextInt(1, 28);
			// int random_month = ThreadLocalRandom.current().nextInt(1, 12);
			// int random_year = ThreadLocalRandom.current().nextInt(2000, 2018);
			// String random_date = random_year + "-" + random_month + "-" + random_day;
			int random_motordrehzahl = ThreadLocalRandom.current().nextInt(4000, 8000);
			int random_kuehlmitteltemperatur = ThreadLocalRandom.current().nextInt(5, 30);
			int random_ansauglufttemperatur = ThreadLocalRandom.current().nextInt(20, 60);
			int random_saugrohrdruck = ThreadLocalRandom.current().nextInt(20, 60);
			int random_fahrzeuggeschwindigkeit = ThreadLocalRandom.current().nextInt(0, 460);

			// SimpleDateFormat isoFormat = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss z"));
			// isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Date date = isoFormat.parse(Calendar.getInstance().getTime());

			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZ")
					.format(Calendar.getInstance().getTime());

			// get datetime in millis
			// String timeStamp = Long.toString(Calendar.getInstance().getTimeInMillis());

			Map<String, String> valuesMap = new HashMap<>();
			valuesMap.put("modell", "modell-" + i);
			valuesMap.put("marke", "marke-" + i);
			valuesMap.put("identifizierungsnummer", UUID.randomUUID().toString());
			valuesMap.put("uebersicht", "testing...");
			valuesMap.put("auftrittsdatum", timeStamp);
			valuesMap.put("letzter-neupreis-von", Integer.toString(i - randomNum_3 + 54580));
			valuesMap.put("letzter-neupreis-bis", Integer.toString(i - randomNum_3 + 54580));
			valuesMap.put("leistung-von", Integer.toString(i - 1000 + randomNum_2));
			valuesMap.put("leistung-bis", Integer.toString(i - 1000 + randomNum_2));
			valuesMap.put("co2-austoss-von", Integer.toString(i - 1000 + randomNum));
			valuesMap.put("co2-austoss-bis", Integer.toString(i - 1000 + randomNum));
			valuesMap.put("aufbauarten", "Roadster");
			valuesMap.put("kraftstoff", "Super");
			valuesMap.put("motordrehzahl", Integer.toString(random_motordrehzahl));
			valuesMap.put("kuehlmitteltemperatur", Integer.toString(random_kuehlmitteltemperatur));
			valuesMap.put("saugrohrdruck", Integer.toString(random_saugrohrdruck));
			valuesMap.put("fahrzeuggeschwindigkeit", Integer.toString(random_fahrzeuggeschwindigkeit));
			valuesMap.put("ansauglufttemperatur", Integer.toString(random_ansauglufttemperatur));
			valuesMap.put("airbag-is-active", "false");
			valuesMap.put("latitude", getRandomCity().getLatitude());
			valuesMap.put("longitude", getRandomCity().getLongitude());

			ClassLoader classLoader = getClass().getClassLoader();
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			String payload = sub.replace(IOUtils.toString(
					classLoader.getResourceAsStream("fixtures_vehicleIndex/put-vehicle-payload.json"), "UTF-8"));

			// Index a document
			entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
			response = esClient.performRequest("PUT", Constants.INDEXING_PATH + "/" + i, params, entity);
			assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);
			System.out.println(response.toString());
			Thread.sleep(500);

		}
	}

	@Test
	public void delete_100_added_posts_AWS() throws ClientProtocolException, IOException, InterruptedException {
		int i = 1000;
		while (i <= 1100) {
			try {
				Response response = esClient.performRequest("DELETE", Constants.INDEXING_PATH + "/" + i);
				System.out.println(response.toString());
				assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
			} catch (Exception e) {

			}
			i++;
		}
	}
}
