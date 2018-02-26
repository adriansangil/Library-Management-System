package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Borrower;

public class BorrowerMapper implements RowMapper<Borrower> {

	@Override
	public Borrower mapRow(ResultSet rs, int rowNum) throws SQLException {
		Borrower borrower = new Borrower();
		borrower.setCardNo(rs.getInt("cardNo"));
		borrower.setName(rs.getString("name"));
		borrower.setAddress(rs.getString("address"));
		borrower.setPhone(rs.getString("phone"));
		
		return borrower;
	}

}
