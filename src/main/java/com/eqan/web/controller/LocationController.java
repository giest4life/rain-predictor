package com.eqan.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.Location;
import com.eqan.web.service.LocationSearch;

@RestController
public class LocationController {
    private static final Logger LOG = LoggerFactory.getLogger(LocationController.class);

    private LocationSearch locationSearch;
    
    @Autowired
    public LocationController(LocationSearch locationSearch) {
        this.locationSearch = locationSearch;
    }

    @GetMapping("/locations")
    public List<Location> getLocations(@RequestParam String query) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Received request to get locations with query {}", query);
        }
        if (query.trim().isEmpty()) {
            throw new IllegalArgumentException("The query parameter must not be null or empty");
        }
        return locationSearch.search(query);
    }

}
