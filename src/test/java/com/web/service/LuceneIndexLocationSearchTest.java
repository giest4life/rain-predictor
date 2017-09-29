package com.web.service;

import com.eqan.web.model.Location;
import com.eqan.web.service.LocationSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    }
}
