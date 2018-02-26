package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopy;

public class BookCopyMapper implements RowMapper<BookCopy> {

	@Override
	public BookCopy mapRow(ResultSet rs, int rowNum) throws SQLException {
		BookCopy bookCopy = new BookCopy();
		Book book = new Book();
		
		book.setBookId(rs.getInt("bookId"));
		book.setTitle(rs.getString("title"));
		
		bookCopy.setBookId(rs.getInt("bookId"));
		bookCopy.setBranchId(rs.getInt("branchId"));
		bookCopy.setNoOfCopies(rs.getInt("noOfCopies"));
		
		bookCopy.setBook(book);
		
		return bookCopy;
	}

}
