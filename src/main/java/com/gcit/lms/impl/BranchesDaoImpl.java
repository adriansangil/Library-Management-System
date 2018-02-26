package com.gcit.lms.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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

import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.entity.BookCopy;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.mapper.BookCopyMapper;
import com.gcit.lms.mapper.BookLoanMapper;
import com.gcit.lms.mapper.BorrowerMapper;
import com.gcit.lms.mapper.BranchMapper;
import com.gcit.lms.utils.Utils;

@Component
public class BranchesDaoImpl implements BranchDAO {
	Logger logger = LoggerFactory.getLogger(BranchesDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Branch> listBranches() {
		String sql = "select * from tbl_library_branch";
		List<Branch> branchs = jdbcTemplate.query(sql, new BranchMapper());
		return branchs;
	}

	@Override
	public Branch getBranchById(int branchId) {
		String sql = "SELECT * FROM library.tbl_library_branch a where a.branchId=?";
		Branch branch = jdbcTemplate.queryForObject(sql, new Object[] { branchId }, new BranchMapper());
		logger.debug("Logging a message from Branch DAO here is the branch object {}", branch);
		return branch;
	}

	@Override
	public int insertBranch(final Branch branch) {
		final String sql = "INSERT INTO tbl_library_branch (branchname, branchAddress) VALUES (?,?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, branch.getName());
				ps.setString(2, branch.getAddress());
				return ps;
			}
		}, holder);

		int branchId = holder.getKey().intValue();
		// branch.setBranchId(newUserId);

		logger.debug("returned value from backend is {}", branchId);

		return branchId;
	}

	@Override
	public void updateBranch(Branch branch) {
		String sql = "update tbl_library_branch set branchname = ?, branchAddress = ? where branchId = ?";
		jdbcTemplate.update(sql, new Object[] { branch.getName(), branch.getAddress(), branch.getBranchId() });

	}

	@Override
	public void deleteBranch(Branch branch) {
		String sql = "delete from tbl_library_branch where branchId = ?";
		jdbcTemplate.update(sql, new Object[] { branch.getBranchId() });
	}

	@Override
	public List<BookCopy> getBranchBooks(Branch branch) {
		String sql = "select * from tbl_book a, tbl_book_copies b where a.bookId = b.bookId and b.branchId = ?";
		return jdbcTemplate.query(sql, new Object[] { branch.getBranchId() }, new BookCopyMapper());
	}

	@Override
	public List<BookCopy> checkBookUnderABranch(int bookId, int branchId) {
		String sql = "SELECT * from tbl_book_copies a, tbl_book b where a.bookId = b.bookId and a.branchId = ? and a.bookId = ?";
		List<BookCopy> book = jdbcTemplate.query(sql, new Object[] { branchId, bookId }, new BookCopyMapper());
		return book;
	}

	@Override
	public List<Borrower> getBranchBorrowers(Branch branch) {
		String sql = "select a.* from tbl_borrower a, tbl_book_loans b where a.cardNo = b.cardNo and b.branchId = ? group by a.cardNo;";
		return jdbcTemplate.query(sql, new Object[] { branch.getBranchId() }, new BorrowerMapper());
	}

	@Override
	public List<Borrower> getSpecificBranchBorrower(int branchId, int cardNo) {
		String sql = "select a.* from tbl_borrower a, tbl_book_loans b where a.cardNo = b.cardNo and b.branchId = ? and a.cardNo = ? group by a.cardNo;";
		return jdbcTemplate.query(sql, new Object[] { branchId, cardNo }, new BorrowerMapper());
	}

	@Override
	public List<BookLoan> getBranchBorrowerLoans(int branchId, int cardNo,Timestamp dateOut, Boolean returned,
			Boolean overdue) {
		String sql = "select * from tbl_borrower a, tbl_book_loans b, tbl_book c where a.cardNo = b.cardNo and b.bookId = c.bookId and b.branchId = ? and a.cardNo = ?";
		List<Object> filters = new ArrayList<>();
		filters.add(branchId);
		filters.add(cardNo);
		
		if(!Utils.isEmpty(dateOut)) {
			sql += " and b.dateOut = ?";
			filters.add(dateOut);
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
	}

	@Override
	public List<BookLoan> getBranchBorrowerLoansByBook(BookLoan bookLoan, Timestamp dateOut, Boolean returned,
			Boolean overdue) {
		String sql = "select * from tbl_borrower a, tbl_book_loans b, tbl_book c where a.cardNo = b.cardNo and b.bookId = c.bookId and b.branchId = ? and a.cardNo = ? "
				+ "and c.bookId = ?";
		List<Object> filters = new ArrayList<>();
		filters.add(bookLoan.getBranchId());
		filters.add(bookLoan.getCardNo());
		filters.add(bookLoan.getBookId());
		
		if(!Utils.isEmpty(dateOut)) {
			sql += " and b.dateOut = ?";
			filters.add(dateOut);
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
	}

	@Override
	public void updateSpecificBranchBorrowerLoan(BookLoan bookloan) {
		String sql = "update tbl_book_loans set dateIn = ? where bookId = ? and branchId = ? and cardNo = ? and dateOut = ?";
		jdbcTemplate.update(sql, new Object[] { bookloan.getDateIn(), bookloan.getBookId(), bookloan.getBranchId(), bookloan.getCardNo(), bookloan.getDateout() });
	}

	@Override
	public void loanABook(BookLoan bl) {
		String sql = "insert into tbl_book_loans (bookId, branchId, cardNo, dueDate) values (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { bl.getBookId(), bl.getBranchId(), bl.getCardNo(), bl.getDueDate() });
		
	}

	@Override
	public void updateBookCopies(int branchId, int bookId, Integer noOfCopies) {
		String sql = "update tbl_book_copies set noOfCopies = ? where branchId = ? and bookId = ?";
		jdbcTemplate.update(sql, new Object[] {noOfCopies, branchId, bookId });
		
	}

}
