package com.eqan.web.model;

public class City {
	private Long id;
	private String name;
	private String canonicalName;
	private String countryCode;

	public City(Long id, String name, String canonicalName, String countryCode) {
		this.id = id;
		this.name = name;
		this.canonicalName = canonicalName;
		this.countryCode = countryCode;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public String getCountryCode() {
		return countryCode;
	}

}
