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

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dto.Authors;
import com.gcit.lms.dto.Books;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.utils.Utils;

@RestController
public class AuthorController {

	private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);
	@Autowired
	AuthorDAO authorDao;
	@Autowired
	BookDAO bookDao;

	// list authors by xml
	@RequestMapping(value = "/authors", method = RequestMethod.GET, produces = { "application/XML" })
	public Authors getAllAuthors() {
		logger.info("Retrieving list of authors with GET as xml");
		Authors aList = new Authors();
		aList.setList(authorDao.listAuthors());
		return aList;

	}

	// list all authors by json
	@RequestMapping(value = "/authors", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Author> getAllAuthorsJson() {
		logger.info("Retrieving list of authors with GET as Json ");
		return authorDao.listAuthors();

	}

	// by id
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Author> getAuthor(@PathVariable int authorId) {
		logger.info("Retrieving author with id" + authorId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Author author = new Author();
		try {
			author = authorDao.getAuthorById(authorId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}
		return new ResponseEntity<Author>(author, responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific author json
	@RequestMapping(value = "/authors/{authorId}/books", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Book>> getAuthorBooksJson(@PathVariable int authorId) {
		logger.info("Retrieving books under author id " + authorId);
		Author author = new Author();
		author.setAuthorId(authorId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();

		try {
			author = authorDao.getAuthorById(authorId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}

		books = authorDao.getAuthorBooks(author);
		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("No Book resource found under this author", 1);
		}

		return new ResponseEntity<List<Book>>(books, responseHeaders, HttpStatus.OK);

	}

	// list all books for a specific author xml
	@RequestMapping(value = "/authors/{authorId}/books", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Books> getAuthorBooksXml(@PathVariable int authorId) {
		logger.info("Retrieving books under author id " + authorId);
		Author author = new Author();
		author.setAuthorId(authorId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Books bookList = new Books();
		try {
			bookList.setList(authorDao.getAuthorBooks(author));
		} catch (Exception e) {
			if (e instanceof EmptyResultDataAccessException) {
				return new ResponseEntity<Books>(null, responseHeaders, HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<Books>(bookList, responseHeaders, HttpStatus.OK);
	}

	//retrieve a book for a specific author
	@RequestMapping(value = "/authors/{authorId}/books/{bookId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Book> getAuthorBooksById(@PathVariable int authorId, @PathVariable int bookId) {
		logger.info("Retrieving a specific book under genre id " + authorId);
		Author author = new Author();
		author.setAuthorId(authorId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();

		try {
			author = authorDao.getAuthorById(authorId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}

		Book book = new Book();
		book.setBookId(bookId);
		books = authorDao.checkBookUnderAnAuthor(bookId, authorId);
		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		return new ResponseEntity<Book>(books.get(0), responseHeaders, HttpStatus.OK);
	}

	// add author
	@RequestMapping(value = "/author", method = RequestMethod.POST, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<String> addAuthor(@RequestBody @Valid Author author) {
		logger.info("Creating author...");
		int id = authorDao.insertAuthor(author);

		URI location = URI.create("/authors/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created author with id: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Author> updateAuthor(@RequestBody @Valid Author author, @PathVariable int authorId) {
		author.setAuthorId(authorId);
		logger.info("Updating author with id:" + author.getAuthorId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			authorDao.updateAuthor(author);
			author = authorDao.getAuthorById(author.getAuthorId());

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Author>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Author resource not found", 1);
			}
		}

		logger.info("updated author with id: " + author.getAuthorId());
		return new ResponseEntity<Author>(author, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/authors/{authorId}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Author> deleteAuthor(@PathVariable int authorId) {
		Author author = new Author();
		author.setAuthorId(authorId);
		logger.info("Deleting author with id:" + author.getAuthorId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			author = authorDao.getAuthorById(author.getAuthorId());
			if (author == null) {
				throw new EmptyResultDataAccessException(authorId);
			}
			authorDao.deleteAuthor(author);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Author>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Author resource not found", 1);
			}
		}

		logger.info("deleted author with id: " + author.getAuthorId());
		return new ResponseEntity<Author>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// delete a book resource under a specific author
	@RequestMapping(value = "/authors/{authorId}/books/{bookId}", method = RequestMethod.DELETE, consumes = {
			"application/XML", "application/JSON" })
	public ResponseEntity<Author> deleteAuthorBook(@PathVariable int authorId, @PathVariable int bookId) {
		Author author = new Author();
		author.setAuthorId(authorId);
		logger.info("Deleting book with id: " + bookId + " under Author with id:" + authorId);
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			author = authorDao.getAuthorById(author.getAuthorId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}
		Book book = new Book();
		book.setBookId(bookId);
		if (Utils.isEmpty(authorDao.checkBookUnderAnAuthor(bookId, authorId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		bookDao.deleteBookAuthor(book, author);

		logger.info("deleted author with id: " + author.getAuthorId());
		return new ResponseEntity<Author>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

}
