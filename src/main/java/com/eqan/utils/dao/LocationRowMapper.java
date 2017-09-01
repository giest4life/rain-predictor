package com.eqan.utils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.eqan.web.model.Location;

public class LocationRowMapper implements RowMapper<Location>{

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        Location location = new Location();
        location.setGeonameId(rs.getLong(1));
        location.setLocationName(rs.getString(2));
        location.setLatitude(rs.getDouble(3));
        location.setLongitude(rs.getDouble(4));
        location.setCountryCode(rs.getString(5));
        location.setCountryName(rs.getString(6));
        location.setAdmin1Code(rs.getString(7));
        location.setAdmin2Code(rs.getString(8));
        location.setAdmin1Name(rs.getString(9));
        return location;
    }

}
