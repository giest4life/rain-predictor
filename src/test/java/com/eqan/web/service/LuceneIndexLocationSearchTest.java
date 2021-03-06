package com.eqan.web.service;

import com.eqan.web.model.Location;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@Slf4j
public class LuceneIndexLocationSearchTest {

    @Autowired
    private LocationSearch locationSearch;

    @Test
    public void testSearch() {
        List<Location> locations = locationSearch.search("Sterling, Virginia");
        assertEquals("Sterling", locations.get(0).getLocationName());
        assertEquals("Virginia", locations.get(0).getAdmin1Name());

        locations = locationSearch.search("Sterling, V");
        assertEquals("Sterling", locations.get(0).getLocationName());
        assertEquals("Virginia", locations.get(0).getAdmin1Name());

        locations = locationSearch.search("New York C");
        assertEquals("New York City", locations.get(0).getLocationName());

        locations = locationSearch.search("San Franci");
        for (Location location : locations) {
        }

    }
}
