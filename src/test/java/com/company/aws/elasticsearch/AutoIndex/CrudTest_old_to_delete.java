package com.company.aws.elasticsearch.AutoIndex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.PropertyConfigurator;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.junit.Test;

import com.campany.aws.elasticsearch.general.Constants;

public class CrudTest_old_to_delete {

	TransportClient transportClient;

	@Before
	public void initialize() throws UnknownHostException {

		Properties log4jProp = new Properties();
		log4jProp.setProperty("log4j.rootLogger", "WARN");
		PropertyConfigurator.configure(log4jProp);

		// transportClient = TransportClient.builder().build()
		// .addTransportAddress(new
		// InetSocketTransportAddress(InetAddress.getByName(Constants.AES_ENDPOINT),
		// 9300));
	}

	@Test
	public void perform_100_posts()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		for (int i = 1000; i < 1200; i++) {
			// Given
			HttpPost request = new HttpPost(Constants.AES_ENDPOINT + "/autos/auto/" + i);

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

			request.addHeader("Content-Type", "application/json");
			StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
			((HttpPost) request).setEntity(entity);
			// When
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			// Then
			assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, HttpStatus.SC_CREATED);

			Thread.sleep(2000);
		}
	}

	@Test
	public void perform_100_posts_AWS()
			throws ClientProtocolException, IOException, InterruptedException, UnknownHostException {

		for (int i = 1000; i < 1200; i++) {

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

			BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
			bulkRequest.add(transportClient.prepareIndex("autos", "auto").setSource(payload));
			bulkRequest.get();
			Thread.sleep(2000);
		}
	}

	@Test
	public void delete_100_added_posts() throws ClientProtocolException, IOException {

		for (int i = 1000; i < 1200; i++) {
			// Given
			HttpUriRequest request = new HttpDelete(Constants.BASE_URL_LOCAL + "/autos/auto/" + i);
			// When
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			// Then
			assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
		}
	}

	@Test
	public void delete_100_added_posts_AWS() throws ClientProtocolException, IOException {

		for (int i = 1000; i < 1200; i++) {
			// Given
			HttpUriRequest request = new HttpDelete(Constants.AES_ENDPOINT + "/autos/auto/" + i);
			// When
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			// Then
			assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
		}
	}
}
