package com.gcit.lms.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoanDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dto.BookLoans;
import com.gcit.lms.entity.BookCopy;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.utils.Utils;

@RestController
public class LoansController {

	private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
	@Autowired
	BookLoanDAO blDao;
	@Autowired
	BookDAO bookDao;
	@Autowired
	BorrowerDAO borrowerDao;
	@Autowired
	BranchDAO branchDao;

	// list loans by xml
	@RequestMapping(value = "/loans", method = RequestMethod.GET, produces = { "application/XML" })
	public BookLoans getAllBookLoansXml() {
		logger.info("retrieving all loans");
		BookLoans aList = new BookLoans();
		aList.setList(blDao.listBookLoans());
		return aList;

	}

	// list all loans by json
	@RequestMapping(value = "/loans", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<BookLoan> getAllBookLoansJson() {
		logger.info("retrieving all loans");
		return blDao.listBookLoans();

	}

	// add loan
	@RequestMapping(value = "/loan", method = RequestMethod.POST, consumes = { "application/XML", "application/JSON" })
	public ResponseEntity<String> addBookLoan(@RequestBody @Valid BookLoan loan) {
		logger.info("Creating loan...");

		// check if borrower exist
		try {
			borrowerDao.getBorrowerById(loan.getCardNo());
		} catch (Exception e) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		// check if branch exist
		try {
			branchDao.getBranchById(loan.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		// check if branch has copy of the book
		List<BookCopy> copies = new ArrayList<>();
		copies = branchDao.checkBookUnderABranch(loan.getBookId(), loan.getBranchId());
		if (Utils.isEmpty(copies)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		if (loan.getDateout() == null) {
			Date currentDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.add(Calendar.DATE, 7);
			Date currentDatePlusOne = c.getTime();
			Timestamp ts = new Timestamp(currentDatePlusOne.getTime());
			loan.setDateout(new Timestamp(currentDate.getTime()));
			loan.setDueDate(ts);
		}

		blDao.insertBookLoan(loan);

		HttpHeaders responseHeaders = new HttpHeaders();

		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/loan", method = RequestMethod.PUT, consumes = { "application/XML", "application/JSON" })
	public ResponseEntity<BookLoan> updateBookLoan(@RequestBody @Valid BookLoan loan) {
		logger.info("Updating loan with book id:" + loan.getBookId() + "" + " cardNo: " + loan.getCardNo()
				+ " branch Id: " + loan.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		// check if borrower exist
		try {
			borrowerDao.getBorrowerById(loan.getCardNo());
		} catch (Exception e) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		// check if branch exist
		try {
			branchDao.getBranchById(loan.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		// check if branch has copy of the book
		List<BookCopy> copies = new ArrayList<>();
		copies = branchDao.checkBookUnderABranch(loan.getBookId(), loan.getBranchId());
		if (Utils.isEmpty(copies)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		// check if dateOut exist
		blDao.updateBookLoan(loan);

		return new ResponseEntity<BookLoan>(null, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/loans", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<BookLoan> deleteBookLoan(@RequestBody @Valid BookLoan loan) {
		logger.info("Delete loan with book id:" + loan.getBookId() + "" + " cardNo: " + loan.getCardNo()
				+ " branch Id: " + loan.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		// check if borrower exist
		try {
			borrowerDao.getBorrowerById(loan.getCardNo());
		} catch (Exception e) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		// check if branch exist
		try {
			branchDao.getBranchById(loan.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		// check if branch has copy of the book
		List<BookCopy> copies = new ArrayList<>();
		copies = branchDao.checkBookUnderABranch(loan.getBookId(), loan.getBranchId());
		if (Utils.isEmpty(copies)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		blDao.deleteBookLoan(loan);
		
		return new ResponseEntity<BookLoan>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

}