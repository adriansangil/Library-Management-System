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
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dto.Books;
import com.gcit.lms.dto.Genres;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.utils.Utils;

@RestController
public class GenreController {

	private static final Logger logger = LoggerFactory.getLogger(GenreController.class);
	@Autowired
	GenreDAO genreDao;
	@Autowired
	BookDAO bookDao;

	// list genres by xml
	@RequestMapping(value = "/genres", method = RequestMethod.GET, produces = { "application/XML" })
	public Genres getAllGenres() {
		logger.info("Welcome home! Message from the POST Method ");
		Genres aList = new Genres();
		aList.setList(genreDao.listGenres());
		return aList;

	}

	// list all genres by json
	@RequestMapping(value = "/genres", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Genre> getAllGenresJson() {
		logger.info("Welcome home! Message from the POST Method ");
		return genreDao.listGenres();

	}

	// by id
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Genre> getGenre(@PathVariable int genreId) {
		logger.info("Retrieving genre with id" + genreId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Genre genre = new Genre();
		try {
			genre = genreDao.getGenreById(genreId);
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Genre>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);

				throw new EmptyResultDataAccessException("Genre resource not found", 1);
			}
		}
		return new ResponseEntity<Genre>(genre, responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific genre json
	@RequestMapping(value = "/genres/{genreId}/books", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Book>> getGenreBooksJson(@PathVariable int genreId) {
		logger.info("Retrieving books under genre id " + genreId);
		Genre genre = new Genre();
		genre.setGenreId(genreId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();
		
		try {
			genre = genreDao.getGenreById(genre.getGenreId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Genre resource not found", 1);
		}
		
		books = genreDao.getGenreBooks(genre);

		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("No book resouce found under this genre", 1);
		}
		return new ResponseEntity<List<Book>>(books, responseHeaders, HttpStatus.OK);
	}

	// get a specific book for a specific genre
	@RequestMapping(value = "/genres/{genreId}/books/{bookId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Book> getGenreBooksById(@PathVariable int genreId, @PathVariable int bookId) {
		logger.info("Retrieving a specific book under genre id " + genreId);
		Genre genre = new Genre();
		genre.setGenreId(genreId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Book> books = new ArrayList<>();

		try {
			genre = genreDao.getGenreById(genre.getGenreId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Genre resource not found", 1);
		}
		
		Book book = new Book();
		book.setBookId(bookId);
		books = genreDao.checkBookUnderAGenre(bookId, genreId);
		if (Utils.isEmpty(books)) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		return new ResponseEntity<Book>(books.get(0), responseHeaders, HttpStatus.OK);
	}

	// list all books for a specific genre xml
	@RequestMapping(value = "/genres/{genreId}/books", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Books> getGenreBooksXml(@PathVariable int genreId) {
		logger.info("Retrieving books under genre id " + genreId);
		Genre genre = new Genre();
		genre.setGenreId(genreId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Books bookList = new Books();
		try {
			bookList.setList(genreDao.getGenreBooks(genre));
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				return new ResponseEntity<Books>(null, responseHeaders, HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<Books>(bookList, responseHeaders, HttpStatus.OK);
	}

	// add genre
	@RequestMapping(value = "/genre", method = RequestMethod.POST, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<String> addGenre(@RequestBody @Valid Genre genre) {
		logger.info("Creating genre...");
		int id = genreDao.insertGenre(genre);

		URI location = URI.create("/genres/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created genre with id: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Genre> updateGenre(@RequestBody @Valid Genre genre, @PathVariable int genreId) {
		genre.setGenreId(genreId);
		logger.info("Updating genre with id:" + genre.getGenreId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			genreDao.updateGenre(genre);
			genre = genreDao.getGenreById(genre.getGenreId());

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Genre>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Genre resource not found", 1);
			}
		}

		logger.info("updated genre with id: " + genre.getGenreId());
		return new ResponseEntity<Genre>(genre, responseHeaders, HttpStatus.OK);
	}

	// delete
	@RequestMapping(value = "/genres/{genreId}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Genre> deleteGenre(@PathVariable int genreId) {
		Genre genre = new Genre();
		genre.setGenreId(genreId);
		logger.info("Deleting genre with id:" + genre.getGenreId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			genre = genreDao.getGenreById(genre.getGenreId());
			if (genre == null) {
				throw new EmptyResultDataAccessException(genreId);
			}
			genreDao.deleteGenre(genre);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<Genre>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Genre resource not found", 1);
			}
		}

		logger.info("deleted genre with id: " + genre.getGenreId());
		return new ResponseEntity<Genre>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// delete book genre
	@RequestMapping(value = "/genres/{genreId}/books/{bookId}", method = RequestMethod.DELETE, consumes = {
			"application/XML", "application/JSON" })
	public ResponseEntity<Genre> deleteGenreBook(@PathVariable int genreId, @PathVariable int bookId) {
		Genre genre = new Genre();
		genre.setGenreId(genreId);
		logger.info("Deleting a book under genre id: " + genre.getGenreId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			genre = genreDao.getGenreById(genre.getGenreId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Genre resource not found", 1);
		}
		
		Book book = new Book();
		book.setBookId(bookId);
		if (Utils.isEmpty(genreDao.checkBookUnderAGenre(bookId, genreId))) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		bookDao.deleteBookGenre(book, genre);

		logger.info("deleted book with id: " + bookId + " under genre id: " + genre.getGenreId());
		return new ResponseEntity<Genre>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

}
