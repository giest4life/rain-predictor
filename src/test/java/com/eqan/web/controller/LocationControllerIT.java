package com.eqan.web.controller;

import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eqan.web.model.Location;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@Slf4j
public class LocationControllerIT {
    private static String URL = "http://localhost:8080/rain-predictor/locations";
    
    RestTemplate restTemplate = new RestTemplate();
    @Test
    public void testWordLoationSearch() {
        String queryString = "Sterling";
        UriComponentsBuilder uriBuilder =  UriComponentsBuilder.fromUriString(URL);
        uriBuilder.queryParam("query", queryString);
        if (log.isDebugEnabled())
            log.debug("Sending request to {}", uriBuilder.toUriString());
        HttpEntity<Location[]> result = restTemplate.getForEntity(uriBuilder.build().toUri(), Location[].class);
        Location[] locations = result.getBody();
        assertTrue("Locations must not be empty", locations.length > 0);
        
    }
    
    @Test
    public void testPhraseLocationSearch() {
        String queryString = "Sterling Virginia";
        UriComponentsBuilder uriBuilder =  UriComponentsBuilder.fromUriString(URL);
        uriBuilder.queryParam("query", queryString);
        
        if (log.isDebugEnabled())
            log.debug("Sending request to {}", uriBuilder.toUriString());
        HttpEntity<Location[]> result = restTemplate.getForEntity(uriBuilder.build().toUri(),Location[].class);
        Location[] locations = result.getBody();
        assertTrue("Locations must not be empty", locations.length > 0);
    }
    
    @Test
    public void testMultiWordCity() {
        String queryString = "New York City";
        UriComponentsBuilder uriBuilder =  UriComponentsBuilder.fromUriString(URL);
        uriBuilder.queryParam("query", queryString);
        
        if (log.isDebugEnabled())
            log.debug("Sending request to {}", uriBuilder.toUriString());
        HttpEntity<Location[]> result = restTemplate.getForEntity(uriBuilder.build().toUri(), Location[].class);
        Location[] locations = result.getBody();
        assertTrue("Locations must not be empty", locations.length > 0);
    }
}
