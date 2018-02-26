package com.gcit.lms.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;

public class BookLoanMapper implements RowMapper<BookLoan> {

	@Override
	public BookLoan mapRow(ResultSet rs, int rowNum) throws SQLException {
		BookLoan bookLoan = new BookLoan();
		Book book = new Book();
		Borrower borrower = new Borrower();
		
		book.setBookId(rs.getInt("bookId"));
		book.setTitle(rs.getString("title"));
		
		borrower.setCardNo(rs.getInt("cardNo"));
		borrower.setName(rs.getString("name"));
		
		bookLoan.setCardNo(rs.getInt("cardNo"));
		bookLoan.setBookId(rs.getInt("bookId"));
		bookLoan.setBranchId(rs.getInt("branchId"));
		bookLoan.setDateIn(rs.getTimestamp("dateIn"));
		bookLoan.setDateout(rs.getTimestamp("dateOut"));
		bookLoan.setDueDate(rs.getTimestamp("dueDate"));
		
		bookLoan.setBook(book);
		bookLoan.setBorrower(borrower);
		
		return bookLoan;
	}

}
