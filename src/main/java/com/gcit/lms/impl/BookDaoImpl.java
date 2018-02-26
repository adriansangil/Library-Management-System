package com.gcit.lms.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.gcit.lms.dao.BookLoanDAO;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.mapper.BookLoanMapper;

@Component
public class BookDaoImpl implements BookLoanDAO {
	Logger logger = LoggerFactory.getLogger(BookDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<BookLoan> listBookLoans() {
		String sql = "select * from tbl_borrower a, tbl_book_loans b, tbl_book c where a.cardNo = b.cardNo and b.bookId = c.bookId";
		List<BookLoan> bl = jdbcTemplate.query(sql, new BookLoanMapper());
		return bl;
	}
	@Override
	public void insertBookLoan(BookLoan bl) {
		String sql = "insert into tbl_book_loans (bookId, branchId, cardNo, dueDate) values (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { bl.getBookId(), bl.getBranchId(), bl.getCardNo(), bl.getDueDate() });
	}
	@Override
	public void updateBookLoan(BookLoan bookloan) {
		String sql = "update tbl_book_loans set dateIn = ?, dueDate = ? where bookId = ? and branchId = ? and cardNo = ? and dateOut = ?";
		jdbcTemplate.update(sql, new Object[] { bookloan.getDateIn(), bookloan.getDueDate(), bookloan.getBookId(), bookloan.getBranchId(), bookloan.getCardNo(), bookloan.getDateout() });
	}
	@Override
	public void deleteBookLoan(BookLoan bookloan) {
		String sql = "delete from tbl_book_loans where bookId = ? and branchId = ? and cardNo = ? and dateOut = ?";
		jdbcTemplate.update(sql, new Object[] { bookloan.getBookId(), bookloan.getBranchId(), bookloan.getCardNo(), bookloan.getDateout() });
	}

	
	
	

}
