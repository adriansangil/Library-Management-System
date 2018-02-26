package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Branch;

public class BranchMapper implements RowMapper<Branch> {

	@Override
	public Branch mapRow(ResultSet rs, int rowNum) throws SQLException {
		Branch branch = new Branch();
		branch.setBranchId(rs.getInt("branchId"));
		branch.setName(rs.getString("branchName"));
		branch.setAddress(rs.getString("branchAddress"));
		
		return branch;
	}

}
