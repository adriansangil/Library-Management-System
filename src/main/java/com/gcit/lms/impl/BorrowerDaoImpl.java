package com.gcit.lms.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.mapper.BookLoanMapper;
import com.gcit.lms.mapper.BorrowerMapper;
import com.gcit.lms.utils.Utils;

@Component
public class BorrowerDaoImpl implements BorrowerDAO {
	Logger logger = LoggerFactory.getLogger(BorrowerDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Borrower> listBorrowers() {
		String sql = "select * from tbl_borrower";
		List<Borrower> borrowers = jdbcTemplate.query(sql, new BorrowerMapper());
		return borrowers;
	}

	@Override
	public Borrower getBorrowerById(int cardNo) {
		String sql = "SELECT * FROM library.tbl_borrower a where a.cardNo=?";
		Borrower borrower = jdbcTemplate.queryForObject(sql, new Object[] { cardNo }, new BorrowerMapper());
		logger.debug("Logging a message from Borrower DAO here is the borrower object {}", borrower);
		return borrower;
	}

	@Override
	public int insertBorrower(final Borrower borrower) {
		final String sql = "INSERT INTO tbl_borrower (name, address, phone) VALUES (?,?,?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, borrower.getName());
				ps.setString(2, borrower.getAddress());
				ps.setString(3, borrower.getPhone());
				return ps;
			}
		}, holder);

		int cardNo = holder.getKey().intValue();
		// borrower.setBorrowerId(newUserId);

		logger.debug("returned value from backend is {}", cardNo);

		return cardNo;
	}

	@Override
	public void updateBorrower(Borrower borrower) {
		String sql = "update tbl_borrower set name = ?, address = ?, phone = ? where cardNo = ?";
		jdbcTemplate.update(sql, new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone(), borrower.getCardNo() });

	}

	@Override
	public void deleteBorrower(Borrower borrower) {
		String sql = "delete from tbl_borrower where cardNo = ?";
		jdbcTemplate.update(sql, new Object[] { borrower.getCardNo() });
	}

	@Override
	public List<BookLoan> getBorrowerBooks(Borrower borrower) {
		String sql = "select * from tbl_book a, tbl_book_loans b, tbl_borrower c  where a.bookId = b.bookId and b.cardNo = c.cardNo and b.cardNo = ?";
		return jdbcTemplate.query(sql, new Object[] { borrower.getCardNo() }, new BookLoanMapper());
	}

	@Override
	public List<BookLoan> checkBookUnderABorrower(int bookId, int cardNo) {
		String sql = "SELECT * from tbl_book b, tbl_book_loans a, tbl_borrower c where a.bookId = b.bookId and a.cardNo = c.cardNo and  a.cardNo = ? and b.bookId = ?";
		List<BookLoan> bl= jdbcTemplate.query(sql, new Object[] { cardNo, bookId }, new BookLoanMapper());
		return bl;
	}

	@Override
	public List<BookLoan> getBorrowerBooksWithFilter(Borrower borrower, Integer branchId, Integer bookId, Boolean overdue, Boolean returned) {
		String sql = "select * from tbl_book a, tbl_book_loans b, tbl_borrower c  where a.bookId = b.bookId and b.cardNo = c.cardNo and b.cardNo = ?";
		List<Object> filters = new ArrayList<>();
		filters.add(borrower.getCardNo());
		
		if(!Utils.isEmpty(branchId)) {
			sql += " and b.branchId = ?";
			filters.add(branchId);
		}
		if(!Utils.isEmpty(bookId)) {
			sql += " and b.bookId = ?";
			filters.add(bookId);
		}
		
		if(!Utils.isEmpty(returned)) {
			if(returned) {
				sql+=" and b.dateIn is NOT NULL";
			} else {
				sql+=" and b.dateIn is NULL";
			}
		}
		if(!Utils.isEmpty(overdue)) {
			if(overdue) {
				sql += " and b.dueDate < NOW()";
			}
		}
		
		Object[] filterObj = new Object[filters.size()];
		filterObj = filters.toArray(filterObj);
		
		return jdbcTemplate.query(sql, filterObj, new BookLoanMapper());
		//return null;
	}

}
