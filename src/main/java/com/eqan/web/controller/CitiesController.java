package com.eqan.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.City;
import com.eqan.web.service.CitySearch;

@RestController
@RequestMapping("/api")
public class CitiesController {
	private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

	@Autowired
	private CitySearch citySearch;

	@GetMapping("/cities")
	public List<City> getCities(@RequestParam String query) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Received request for /cities with query {}", query);
		}
		if (query.trim().isEmpty()) {
			throw new IllegalArgumentException("The query parameter must not be null or empty");
		}
		return citySearch.searchByCanonicalName(query);
	}

	@PostConstruct
	public void sayHello() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Constructed cities controller");
		}

	}

}
