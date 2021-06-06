package com.adobe.training.core.core;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class ParseJson {

	Logger logger = LoggerFactory.getLogger(ParseJson.class);

	public String loadJson(String SOURCE_URL) {

		// TODO Auto-generated method stub
		String value = new String();
		try {
			URL url = new URL(SOURCE_URL);
			Scanner scan = new Scanner(url.openStream());
			String str = new String();
			while (scan.hasNext()) {
				str += scan.nextLine();
			}
			scan.close();
			String[] csv = str.split(",");
			String key;
			for (int i = 0; i < csv.length; i++) {
				key = new String(csv[i]);
				key = key.replace("\"", "");

				if (key.startsWith("regularMarketPreviousClose")) {
					value = key.split(":")[1];
					break;
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;

	}

}
