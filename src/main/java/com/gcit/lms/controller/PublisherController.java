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
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.dto.Books;
import com.gcit.lms.dto.Publishers;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Publisher;
import com.gcit.lms.utils.Utils;

@RestController
public class PublisherController {

	private static final Logger logger = LoggerFactory.getLogger(PublisherController.class);
	@Autowired
	PublisherDAO pubDao;
	@Autowired
	BookDAO bookDao;

	// list pubs by xml
	@RequestMapping(value = "/publishers", method = RequestMethod.GET, produces = { "application/XML" })
	public Publishers getAllPublishers() {
		logger.info("Retrieving all publishers ");
		Publishers aList = new Publishers();
		aList.setList(pubDao.listPublishers());
		return aList;

	}

	// list all pubs by json
	@RequestMapping(value = "/publishers", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Publisher> getAllPublishersJson() {
		logger.info("Welcome home! Message from the POST Method ");
		return pubDao.listPublishers();

	}

	// by id
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Publisher> getPublisher(@PathVariable int pubId) {
		logger.info("Retrieving pub with id" + pubId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Publisher pub = new Publisher();
		try {
			pub = pubDao.getPublisherById(pubId);
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Publisher>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);

				throw new EmptyResultDataAccessException("Publisher resource not found", 1);
			}
		}
		return new ResponseEntity<Publisher>(pub, responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific pub json
	@RequestMapping(value = "/publishers/{pubId}/books", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Book>> getPublisherBooksJson(@PathVariable int pubId) {
		logger.info("Retrieving books under pub id " + pubId);
		Publisher pub = new Publisher();
		pub.setPubId(pubId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();
		
		try {
			pub = pubDao.getPublisherById(pub.getPubId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Publisher resource not found", 1);
		}
		
		books = pubDao.getPublisherBooks(pub);

		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("No book resouce found under this pub", 1);
		}
		return new ResponseEntity<List<Book>>(books, responseHeaders, HttpStatus.OK);
	}

	// get a specific book for a specific pub
	@RequestMapping(value = "/publishers/{pubId}/books/{bookId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Book> getPublisherBooksById(@PathVariable int pubId, @PathVariable int bookId) {
		logger.info("Retrieving a specific book under pub id " + pubId);
		Publisher pub = new Publisher();
		pub.setPubId(pubId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();

		try {
			pub = pubDao.getPublisherById(pub.getPubId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Publisher resource not found", 1);
		}
		
		Book book = new Book();
		book.setBookId(bookId);
		books = pubDao.checkBookUnderAPublisher(bookId, pubId);
		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		return new ResponseEntity<Book>(books.get(0), responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific pub xml
	@RequestMapping(value = "/publishers/{pubId}/books", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Books> getPublisherBooksXml(@PathVariable int pubId) {
		logger.info("Retrieving books under pub id " + pubId);
		Publisher pub = new Publisher();
		pub.setPubId(pubId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Books bookList = new Books();
		try {
			bookList.setList(pubDao.getPublisherBooks(pub));
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				return new ResponseEntity<Books>(null, responseHeaders, HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<Books>(bookList, responseHeaders, HttpStatus.OK);
	}

	// add pub
	@RequestMapping(value = "/publisher", method = RequestMethod.POST, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<String> addPublisher(@RequestBody @Valid Publisher pub) {
		logger.info("Creating pub...");
		int id = pubDao.insertPublisher(pub);

		URI location = URI.create("/publishers/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created pub with id: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Publisher> updatePublisher(@RequestBody @Valid Publisher pub, @PathVariable int pubId) {
		pub.setPubId(pubId);
		logger.info("Updating pub with id:" + pub.getPubId());
		HttpHeaders responseHeaders = new HttpHeaders();
		Publisher returnedPub = new Publisher();
		try {
			returnedPub = pubDao.getPublisherById(pub.getPubId());
			if(Utils.isEmpty(pub.getName())) {
				pub.setName(returnedPub.getName());
			}
			if(Utils.isEmpty(pub.getAddress())) {
				pub.setAddress(returnedPub.getAddress());
			}
			if(Utils.isEmpty(pub.getPhone())) {
				pub.setPhone(returnedPub.getPhone());
			}
			pubDao.updatePublisher(pub);
			pub = pubDao.getPublisherById(pub.getPubId());

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Publisher>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Publisher resource not found", 1);
			}
		}

		logger.info("updated pub with id: " + pub.getPubId());
		return new ResponseEntity<Publisher>(pub, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/publishers/{pubId}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Publisher> deletePublisher(@PathVariable int pubId) {
		Publisher pub = new Publisher();
		pub.setPubId(pubId);
		logger.info("Deleting pub with id:" + pub.getPubId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			pub = pubDao.getPublisherById(pub.getPubId());
			if (pub == null) {
				throw new EmptyResultDataAccessException(pubId);
			}
			pubDao.deletePublisher(pub);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Publisher>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Publisher resource not found", 1);
			}
		}

		logger.info("deleted pub with id: " + pub.getPubId());
		return new ResponseEntity<Publisher>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// delete book pub
	@RequestMapping(value = "/publishers/{pubId}/books/{bookId}", method = RequestMethod.DELETE, consumes = {
			"application/XML", "application/JSON" })
	public ResponseEntity<Publisher> deletePublisherBook(@PathVariable int pubId, @PathVariable int bookId) {
		Publisher pub = new Publisher();
		pub.setPubId(pubId);
		logger.info("Deleting a book under pub id: " + pub.getPubId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			pub = pubDao.getPublisherById(pub.getPubId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Publisher resource not found", 1);
		}
		
		Book book = new Book();
		book.setBookId(bookId);
		if (Utils.isEmpty(pubDao.checkBookUnderAPublisher(bookId, pubId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		bookDao.deleteBookPub(book);

		logger.info("deleted book with id: " + bookId + " under pub id: " + pub.getPubId());
		return new ResponseEntity<Publisher>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

}
