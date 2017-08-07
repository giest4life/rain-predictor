package com.eqan.web.service;

import java.util.List;

import com.eqan.web.model.City;

public interface CitySearch {
	List<City> searchByName(String name);
	City searchOneByName(String name);
	List<City> searchByCanonicalName(String canonicalName);
}
