package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Author;

public class AuthorMapper implements RowMapper<Author> {

	@Override
	public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
		Author author = new Author();
		author.setAuthorId(rs.getInt("authorId"));
		author.setAuthorName(rs.getString("authorName"));
		return author;
	}

}
