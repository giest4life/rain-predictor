package com.eqan.utils.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CSVCitiesFactory {
	private static String FILE_PATH = "locations.csv";
	private CSVParser records = null;

	public void loadCSVRecords() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			BufferedReader br = new BufferedReader(new InputStreamReader(classloader.getResourceAsStream(FILE_PATH)));
			records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(br);
	}

	@Bean(name = "cityParser")
	public CSVParser getRecords() throws IOException {
		if (records == null) {
			loadCSVRecords();
		}
		return records;
	}

}
