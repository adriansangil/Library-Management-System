package com.gcit.lms.controller;

import java.net.URI;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dto.BookCopies;
import com.gcit.lms.dto.BookLoans;
import com.gcit.lms.dto.Borrowers;
import com.gcit.lms.dto.Branches;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopy;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.utils.Utils;

@RestController
public class BranchesController {

	private static final Logger logger = LoggerFactory.getLogger(BranchesController.class);
	@Autowired
	BranchDAO branchDao;
	@Autowired
	BookDAO bookDao;

	// list branchs by xml
	@RequestMapping(value = "/branches", method = RequestMethod.GET, produces = { "application/XML" })
	public Branches getAllBranches() {
		logger.info("Welcome home! Message from the POST Method ");
		Branches aList = new Branches();
		aList.setList(branchDao.listBranches());
		return aList;

	}

	// list all branchs by json
	@RequestMapping(value = "/branches", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Branch> getAllBranchesJson() {
		logger.info("Welcome home! Message from the POST Method ");
		return branchDao.listBranches();

	}

	// by id
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Branch> getBranch(@PathVariable int branchId) {
		logger.info("Retrieving branch with id" + branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Branch branch = new Branch();
		try {
			branch = branchDao.getBranchById(branchId);
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Branch>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);

				throw new EmptyResultDataAccessException("Branch resource not found", 1);
			}
		}
		return new ResponseEntity<Branch>(branch, responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific branch json
	@RequestMapping(value = "/branches/{branchId}/books", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<BookCopy>> getBranchBooksJson(@PathVariable int branchId) {
		logger.info("Retrieving books under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<BookCopy> books = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		books = branchDao.getBranchBooks(branch);

		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("No book resouce found under this branch", 1);
		}
		return new ResponseEntity<List<BookCopy>>(books, responseHeaders, HttpStatus.OK);
	}

	// get a specific book for a specific branch
	@RequestMapping(value = "/branches/{branchId}/books/{bookId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<BookCopy> getBranchBooksById(@PathVariable int branchId, @PathVariable int bookId) {
		logger.info("Retrieving a specific book under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<BookCopy> copies = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		BookCopy book = new BookCopy();
		book.setBookId(bookId);
		copies = branchDao.checkBookUnderABranch(bookId, branchId);
		if (Utils.isEmpty(copies)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		return new ResponseEntity<BookCopy>(copies.get(0), responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific branch xml
	@RequestMapping(value = "/branches/{branchId}/books", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<BookCopies> getBranchBooksXml(@PathVariable int branchId) {
		logger.info("Retrieving books under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		BookCopies bookList = new BookCopies();
		try {
			bookList.setList(branchDao.getBranchBooks(branch));
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				return new ResponseEntity<BookCopies>(null, responseHeaders, HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<BookCopies>(bookList, responseHeaders, HttpStatus.OK);
	}

	// add branch
	@RequestMapping(value = "/branch", method = RequestMethod.POST, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<String> addBranch(@RequestBody @Valid Branch branch) {
		logger.info("Creating branch...");
		int id = branchDao.insertBranch(branch);

		URI location = URI.create("/branches/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created branch with id: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Branch> updateBranch(@RequestBody @Valid Branch branch, @PathVariable int branchId) {
		branch.setBranchId(branchId);
		logger.info("Updating branch with id:" + branch.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			branchDao.updateBranch(branch);
			branch = branchDao.getBranchById(branch.getBranchId());

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Branch>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Branch resource not found", 1);
			}
		}

		logger.info("updated branch with id: " + branch.getBranchId());
		return new ResponseEntity<Branch>(branch, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/branches/{branchId}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Branch> deleteBranch(@PathVariable int branchId) {
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		logger.info("Deleting branch with id:" + branch.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
			if (branch == null) {
				throw new EmptyResultDataAccessException(branchId);
			}
			branchDao.deleteBranch(branch);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Branch>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Branch resource not found", 1);
			}
		}

		logger.info("deleted branch with id: " + branch.getBranchId());
		return new ResponseEntity<Branch>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// delete book branch
	@RequestMapping(value = "/branches/{branchId}/books/{bookId}", method = RequestMethod.DELETE, consumes = {
			"application/XML", "application/JSON" })
	public ResponseEntity<Branch> deleteBranchBook(@PathVariable int branchId, @PathVariable int bookId) {
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		logger.info("Removing copies of book with id: " + bookId + " under branch id: " + branch.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		Book book = new Book();
		book.setBookId(bookId);
		if (Utils.isEmpty(branchDao.checkBookUnderABranch(bookId, branchId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		bookDao.deleteBookBranch(book, branch);

		logger.info("deleted book with id: " + bookId + " under branch id: " + branch.getBranchId());
		return new ResponseEntity<Branch>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// update book branch
	@RequestMapping(value = "/branches/{branchId}/books/{bookId}", method = RequestMethod.PUT, consumes = {
			"application/XML", "application/JSON" })
	public ResponseEntity<BookCopy> updateBranchBookCopies(@PathVariable int branchId, @PathVariable int bookId,
			@RequestBody BookCopy copy) {
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		logger.info("Adding copies of book with id: " + bookId + " under branch id: " + branch.getBranchId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		Book book = new Book();
		book.setBookId(bookId);
		if (Utils.isEmpty(branchDao.checkBookUnderABranch(bookId, branchId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		
		int noOfCopies = 0;
		if(!Utils.isEmpty(copy.getNoOfCopies())) {
			noOfCopies = copy.getNoOfCopies();
		}
		
		branchDao.updateBookCopies(branchId, bookId, noOfCopies);
		
		List<BookCopy> returnedList = branchDao.checkBookUnderABranch(bookId, branchId);

		logger.info("update book copies for book with id: " + bookId + " under branch id: " + branch.getBranchId());
		return new ResponseEntity<BookCopy>(returnedList.get(0), responseHeaders, HttpStatus.OK);
	}

	// list all book borrowers for a specific branch json
	@RequestMapping(value = "/branches/{branchId}/borrowers", method = RequestMethod.GET, produces = {
			"application/JSON" })
	public ResponseEntity<List<Borrower>> getBranchBorrowersJson(@PathVariable int branchId) {
		logger.info("Retrieving book borrowers under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrower = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrower = branchDao.getBranchBorrowers(branch);

		if (Utils.isEmpty(borrower)) {
			throw new EmptyResultDataAccessException("No borrower resouce found under this branch", 1);
		}
		return new ResponseEntity<List<Borrower>>(borrower, responseHeaders, HttpStatus.OK);
	}

	// list all book borrowers for a specific branch xml
	@RequestMapping(value = "/branches/{branchId}/borrowers", method = RequestMethod.GET, produces = {
			"application/XML" })
	public ResponseEntity<Borrowers> getBranchBorrowersXml(@PathVariable int branchId) {
		logger.info("Retrieving book borrowers under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrower = new ArrayList<>();
		Borrowers borrowerVo = new Borrowers();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrower = branchDao.getBranchBorrowers(branch);

		if (Utils.isEmpty(borrower)) {
			throw new EmptyResultDataAccessException("No borrower resouce found under this branch", 1);
		}
		borrowerVo.setList(borrower);

		return new ResponseEntity<Borrowers>(borrowerVo, responseHeaders, HttpStatus.OK);
	}

	// get a specific book borrower for a specific branch
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Borrower> getBranchBorrowerById(@PathVariable int branchId, @PathVariable int cardNo) {
		logger.info("Retrieving a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}

		return new ResponseEntity<Borrower>(borrowers.get(0), responseHeaders, HttpStatus.OK);
	}

	// get the loans of a borrower under a branch id
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans", method = RequestMethod.GET, produces = {
			"application/JSON" })
	public ResponseEntity<List<BookLoan>> getBranchBorrowerBookLoansJson(@PathVariable int branchId,
			@PathVariable int cardNo,
			@RequestParam(name = "dateout", required = false) String dateOut,
			@RequestParam(name = "overdue", required = false) Boolean overdue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving book loans for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		
		Timestamp dateOutstamp = null;
		if (!Utils.isEmpty(dateOut)) {

			dateOutstamp = new Timestamp(Long.parseLong(dateOut));
			logger.info("Date is:" + dateOutstamp);

		}

		List<BookLoan> bl = new ArrayList<>();
		bl = branchDao.getBranchBorrowerLoans(branchId, cardNo,dateOutstamp, returned, overdue);
		if (Utils.isEmpty(bl)) {
			throw new EmptyResultDataAccessException("No Loan resource found", 1);
		}

		return new ResponseEntity<List<BookLoan>>(bl, responseHeaders, HttpStatus.OK);
	}

	// get the loans of a borrower under a branch id xml
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans", method = RequestMethod.GET, produces = {
			"application/XML" })
	public ResponseEntity<BookLoans> getBranchBorrowerBookLoansXml(@PathVariable int branchId,
			@PathVariable int cardNo,
			@RequestParam(name = "dateout", required = false) String dateOut,
			@RequestParam(name = "overdue", required = false) Boolean overdue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving book loans for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		
		Timestamp dateOutstamp = null;
		if (!Utils.isEmpty(dateOut)) {

			dateOutstamp = new Timestamp(Long.parseLong(dateOut));
			logger.info("Date is:" + dateOutstamp);

		}

		List<BookLoan> bl = new ArrayList<>();
		bl = branchDao.getBranchBorrowerLoans(branchId, cardNo,dateOutstamp, returned, overdue);
		if (Utils.isEmpty(bl)) {
			throw new EmptyResultDataAccessException("No Loan resource found", 1);
		}

		BookLoans blVO = new BookLoans();
		blVO.setList(bl);

		return new ResponseEntity<BookLoans>(blVO, responseHeaders, HttpStatus.OK);
	}

	// get the loans of a borrower under a branch id json
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans/{bookId}", method = RequestMethod.GET, produces = {
			"application/JSON" })
	public ResponseEntity<List<BookLoan>> getBranchBorrowerBookLoansByBookJson(@PathVariable int branchId,
			@PathVariable int cardNo, @PathVariable int bookId,
			@RequestParam(name = "dateout", required = false) String dateOut,
			@RequestParam(name = "overdue", required = false) Boolean overdue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving book loans by book id for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		BookLoan item = new BookLoan();
		item.setBookId(bookId);
		item.setBranchId(branchId);
		item.setCardNo(cardNo);

		Timestamp dateOutstamp = null;
		if (!Utils.isEmpty(dateOut)) {

			dateOutstamp = new Timestamp(Long.parseLong(dateOut));
			logger.info("Date is:" + dateOutstamp);

		}

		List<BookLoan> bl = new ArrayList<>();
		bl = branchDao.getBranchBorrowerLoansByBook(item, dateOutstamp, returned, overdue);
		if (Utils.isEmpty(bl)) {
			throw new EmptyResultDataAccessException("No Loan resource found", 1);
		}

		BookLoans blVO = new BookLoans();
		blVO.setList(bl);

		return new ResponseEntity<List<BookLoan>>(bl, responseHeaders, HttpStatus.OK);
	}

	// get the loans of a borrower under a branch id xml
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans/{bookId}", method = RequestMethod.GET, produces = {
			"application/XML" })
	public ResponseEntity<BookLoans> getBranchBorrowerBookLoansByBookXml(@PathVariable int branchId,
			@PathVariable int cardNo, @PathVariable int bookId,
			@RequestParam(name = "dateout", required = false) String dateOut,
			@RequestParam(name = "overdue", required = false) Boolean overdue,
			@RequestParam(name = "returned", required = false) Boolean returned) {
		logger.info("Retrieving book loans by book id for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		BookLoan item = new BookLoan();
		item.setBookId(bookId);
		item.setBranchId(branchId);
		item.setCardNo(cardNo);

		Timestamp dateOutstamp = null;
		if (!Utils.isEmpty(dateOut)) {

			dateOutstamp = new Timestamp(Long.parseLong(dateOut));
			logger.info("Date is:" + dateOutstamp);

		}

		List<BookLoan> bl = new ArrayList<>();
		bl = branchDao.getBranchBorrowerLoansByBook(item, dateOutstamp, returned, overdue);
		if (Utils.isEmpty(bl)) {
			throw new EmptyResultDataAccessException("No Loan resource found", 1);
		}

		BookLoans blVO = new BookLoans();
		blVO.setList(bl);

		return new ResponseEntity<BookLoans>(blVO, responseHeaders, HttpStatus.OK);
	}

	// update specific loan of a borrower under a branch id -- return
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans/{bookId}", method = RequestMethod.PUT, produces = {
			"application/XML", "application/JSON" })
	public ResponseEntity<BookLoan> returnAbook(@PathVariable int branchId, @PathVariable int cardNo,
			@PathVariable int bookId, @RequestParam(name = "dateout", required = true) String dateOut) {
		logger.info("Retrieving book loans by book id for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		BookLoan item = new BookLoan();
		item.setBookId(bookId);
		item.setBranchId(branchId);
		item.setCardNo(cardNo);

		Timestamp dateOutstamp = null;
		if (!Utils.isEmpty(dateOut)) {

			dateOutstamp = new Timestamp(Long.parseLong(dateOut));
			logger.info("Date is:" + dateOutstamp);

		}

		// check if loan exist and still not yet returned
		List<BookLoan> bl = new ArrayList<>();
		bl = branchDao.getBranchBorrowerLoansByBook(item, dateOutstamp, false, null);
		if (Utils.isEmpty(bl)) {
			throw new EmptyResultDataAccessException("No Loan resource found, it must have been already returned", 1);
		}

		item.setDateout(dateOutstamp);
		//
		Date currentDate = new Date();
		Timestamp ts = new Timestamp(currentDate.getTime());
		item.setDateIn(ts);

		branchDao.updateSpecificBranchBorrowerLoan(item);

		bl = branchDao.getBranchBorrowerLoansByBook(item, dateOutstamp, true, null);

		return new ResponseEntity<BookLoan>(bl.get(0), responseHeaders, HttpStatus.OK);
	}

	// create a loan for a borrower under this branch
	@RequestMapping(value = "/branches/{branchId}/borrowers/{cardNo}/loans/{bookId}", method = RequestMethod.POST, produces = {
			"application/XML", "application/JSON" })
	public ResponseEntity<BookLoan> borrowAbook(@PathVariable int branchId, @PathVariable int cardNo,
			@PathVariable int bookId) {
		logger.info("creating book loan by book id for a specific book borrower under branch id " + branchId);
		Branch branch = new Branch();
		branch.setBranchId(branchId);
		List<Borrower> borrowers = new ArrayList<>();

		try {
			branch = branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		borrowers = branchDao.getSpecificBranchBorrower(branchId, cardNo);
		if (Utils.isEmpty(borrowers)) {
			throw new EmptyResultDataAccessException("Borrower resource not found", 1);
		}
		BookLoan item = new BookLoan();
		item.setBookId(bookId);
		item.setBranchId(branchId);
		item.setCardNo(cardNo);

		if (Utils.isEmpty(branchDao.checkBookUnderABranch(bookId, branchId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		//
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, 7);
		Date currentDatePlusOne = c.getTime();
		Timestamp ts = new Timestamp(currentDatePlusOne.getTime());
		item.setDateout(new Timestamp(currentDate.getTime()));
		item.setDueDate(ts);

		branchDao.loanABook(item);

		URI location = URI.create("/branches/" + branchId + "/borrowers/" + cardNo + "/loans/" + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		return new ResponseEntity<BookLoan>(null, responseHeaders, HttpStatus.OK);
	}

}
