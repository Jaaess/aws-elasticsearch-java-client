package com.company.aws.elasticsearch.AutoIndex;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

//import org.elasticsearch.client.http.HttpEntity;
//import org.elasticsearch.client.http.HttpHost;
//import org.elasticsearch.client.http.entity.ContentType;
//import org.elasticsearch.client.http.nio.entity.NStringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.company.aws.elasticsearch.general.Constants;

public class JavaRestClientExample {

	public static void main(String[] args) throws IOException {

		String index = "autos";
		String type = "auto";
		int i = 1000;

		int randomNum = ThreadLocalRandom.current().nextInt(100, 300 + 1);
		int randomNum_2 = ThreadLocalRandom.current().nextInt(100, 300 + 1);

		String payload = "{\r\n" + "	\"Modell\": \"TestAuto_" + i + "\",\r\n" + "	\"Marke\": \"Test" + i
				+ "\",\r\n" + "	\"Auftrittsdatum\":  " + (int) (2022 + i - 1000) + ",\r\n"
				+ "	\"Letzter-Neupreis\": {\r\n" + "		\"Einheit\": \"EUR\",\r\n" + "		\"von\":  43.580,\r\n"
				+ "		\"bis\":  43.580\r\n" + "	},\r\n" + "	\"Leistung\": {\r\n"
				+ "		\"Einheit\": \"PS\",\r\n" + "		\"von\": " + (int) (i - 1000 + randomNum_2) + " ,\r\n"
				+ "		\"bis\":   " + (int) (i - 1000 + randomNum_2) + "        \r\n" + "	},\r\n"
				+ "	\"CO2-Ausstoss\": {\r\n" + "		\"Einheit\": \"g/km\",\r\n" + "		\"von\": "
				+ (int) (i - 1000 + randomNum) + ",\r\n" + "		\"bis\": " + (int) (i - 1000 + randomNum) + " \r\n"
				+ "	},\r\n" + "	\"Aufbauarten\": \"Roadster\",\r\n" + "	\"Kraftstoff\": \"Super\",\r\n"
				+ "	\"Uebersicht\": \"testing\"\r\n" + "}";

		RestClient client = RestClient.builder(new HttpHost(Constants.AES_ENDPOINT, 443, "https")).build();

		HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);

		Response response = client.performRequest("PUT", "/" + index + "/" + type + "/" + i,
				Collections.<String, String>emptyMap(), entity);

		System.out.println(response.toString());
	}

}