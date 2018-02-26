package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Publisher;

public class PublisherMapper implements RowMapper<Publisher> {

	@Override
	public Publisher mapRow(ResultSet rs, int rowNum) throws SQLException {
		Publisher pub = new Publisher();
		pub.setPubId(rs.getInt("publisherId"));
		pub.setName(rs.getString("publisherName"));
		pub.setAddress(rs.getString("publisherAddress"));
		pub.setPhone(rs.getString("publisherPhone"));
		
		return pub;
	}

}
