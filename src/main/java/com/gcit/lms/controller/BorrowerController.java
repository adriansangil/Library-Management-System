package com.gcit.lms.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dto.BookLoans;
import com.gcit.lms.dto.Borrowers;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.utils.Utils;

@RestController
public class BorrowerController {

	private static final Logger logger = LoggerFactory.getLogger(BorrowerController.class);
	@Autowired
	BorrowerDAO borrowerDao;

	// list pubs by xml
	@RequestMapping(value = "/borrowers", method = RequestMethod.GET, produces = { "application/XML" })
	public Borrowers getAllBorrowersXml() {
		logger.info("Retrieving all borrowers ");
		Borrowers aList = new Borrowers();
		aList.setList(borrowerDao.listBorrowers());
		return aList;

	}

	// list all pubs by json
	@RequestMapping(value = "/borrowers", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Borrower> getAllBorrowersJson() {
		logger.info("Retrieving all borrowers ");
		return borrowerDao.listBorrowers();

	}

	// by id
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Borrower> getBorrower(@PathVariable int cardNo) {
		logger.info("Retrieving borrower with cardNo " + cardNo);
		HttpHeaders responseHeaders = new HttpHeaders();
		Borrower pub = new Borrower();
		try {
			pub = borrowerDao.getBorrowerById(cardNo);
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Borrower>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);

				throw new EmptyResultDataAccessException("Borrower resource not found", 1);
			}
		}
		return new ResponseEntity<Borrower>(pub, responseHeaders, HttpStatus.OK);
	}

	// add borrower
	@RequestMapping(value = "/borrower", method = RequestMethod.POST, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<String> addBorrower(@RequestBody @Valid Borrower borrower) {
		logger.info("Creating pub...");
		int id = borrowerDao.insertBorrower(borrower);

		URI location = URI.create("/borrowers/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created borrower with cardNo: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Borrower> updateBorrower(@RequestBody @Valid Borrower borrower, @PathVariable int cardNo) {
		borrower.setCardNo(cardNo);
		logger.info("Updating borrower with cardNo: " + borrower.getCardNo());
		HttpHeaders responseHeaders = new HttpHeaders();
		Borrower returnedBorrower = new Borrower();
		try {
			returnedBorrower = borrowerDao.getBorrowerById(borrower.getCardNo());
			if (Utils.isEmpty(borrower.getName())) {
				borrower.setName(returnedBorrower.getName());
			}
			if (Utils.isEmpty(borrower.getAddress())) {
				borrower.setAddress(returnedBorrower.getAddress());
			}
			if (Utils.isEmpty(borrower.getPhone())) {
				borrower.setPhone(returnedBorrower.getPhone());
			}
			borrowerDao.updateBorrower(borrower);
			borrower = borrowerDao.getBorrowerById(borrower.getCardNo());

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Borrower>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Borrower resource not found", 1);
			}
		}

		logger.info("updated borrower with cardNo: " + borrower.getCardNo());
		return new ResponseEntity<Borrower>(borrower, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Borrower> deleteBorrower(@PathVariable int cardNo) {
		Borrower pub = new Borrower();
		pub.setCardNo(cardNo);
		logger.info("Deleting borrower with cardNo: " + pub.getCardNo());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			pub = borrowerDao.getBorrowerById(pub.getCardNo());
			if (pub == null) {
				throw new EmptyResultDataAccessException(cardNo);
			}
			borrowerDao.deleteBorrower(pub);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Borrower>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Borrower resource not found", 1);
			}
		}

		logger.info("deleted borrower with cardNo: " + pub.getCardNo());
		return new ResponseEntity<Borrower>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// list all loans for a specific borrower json
	@RequestMapping(value = "/borrowers/{cardNo}/loans", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<BookLoan>> getBorrowerLoansJson(@PathVariable int cardNo,
			@RequestParam(name = "branchId", required = false) Integer branchId,
			@RequestParam(name = "bookId", required = false) Integer bookId,
			@RequestParam(name = "overDue", required = false) Boolean overDue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving loans for borrower with cardNo " + cardNo);
		Borrower borrower = new Borrower();
		borrower.setCardNo(cardNo);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<BookLoan> loans = new ArrayList<>();

		try {
			borrower = borrowerDao.getBorrowerById(cardNo);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		// loans = borrowerDao.getBorrowerBooks(borrower);

		loans = borrowerDao.getBorrowerBooksWithFilter(borrower, branchId, bookId, overDue, returned);
		if (Utils.isEmpty(loans)) {
			throw new EmptyResultDataAccessException("No Loan resource found for borrower with cardNo: " + cardNo, 1);
		}

		return new ResponseEntity<List<BookLoan>>(loans, responseHeaders, HttpStatus.OK);

	}

	// list all loans for a specific borrower xml
	@RequestMapping(value = "/borrowers/{cardNo}/loans", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<BookLoans> getBorrowerLoansXml(@PathVariable int cardNo,
			@RequestParam(name = "branchId", required = false) Integer branchId,
			@RequestParam(name = "bookId", required = false) Integer bookId,
			@RequestParam(name = "overdue", required = false) Boolean overDue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving loans for borrower with cardNo " + cardNo);
		Borrower borrower = new Borrower();
		borrower.setCardNo(cardNo);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<BookLoan> loans = new ArrayList<>();
		BookLoans bl = new BookLoans();

		try {
			borrower = borrowerDao.getBorrowerById(cardNo);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		// loans = borrowerDao.getBorrowerBooks(borrower);

		loans = borrowerDao.getBorrowerBooksWithFilter(borrower, branchId, bookId, overDue, returned);
		if (Utils.isEmpty(loans)) {
			throw new EmptyResultDataAccessException("No Loan resource found for borrower with cardNo: " + cardNo, 1);
		}

		bl.setList(loans);

		return new ResponseEntity<BookLoans>(bl, responseHeaders, HttpStatus.OK);

	}
}
