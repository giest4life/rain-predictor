package com.eqan.web.service;

import java.util.List;

import com.eqan.web.model.Location;


public interface LocationSearch {
    List<Location> search(String queryString);
}
