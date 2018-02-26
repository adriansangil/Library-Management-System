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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.dto.Authors;
import com.gcit.lms.dto.Books;
import com.gcit.lms.dto.Branches;
import com.gcit.lms.dto.Genres;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;
import com.gcit.lms.utils.Utils;

@RestController
public class BookController {

	private static final Logger logger = LoggerFactory.getLogger(BookController.class);
	@Autowired
	BookDAO bookDao;
	@Autowired
	GenreDAO genreDao;
	@Autowired
	AuthorDAO authorDao;
	@Autowired
	BranchDAO branchDao;
	@Autowired
	PublisherDAO pubDao;

	// list books by xml
	@RequestMapping(value = "/books", method = RequestMethod.GET, produces = { "application/XML" })
	public Books getAllBooksByXml() {
		logger.info("Retrieving all books xml format");
		Books aList = new Books();
		aList.setList(bookDao.listBooks());
		return aList;

	}

	// list all books by json
	@RequestMapping(value = "/books", method = RequestMethod.GET, produces = { "application/JSON" })
	public List<Book> getAllBooksJson() {
		logger.info("Retrieving all books json format");
		List<Book> books = bookDao.listBooks();
		for (Book b : books) {
			List<Author> authors = bookDao.getBookAuthors(b);
			b.setAuthors(authors);
			List<Genre> genres = bookDao.getBookGenres(b);
			b.setGenres(genres);
		}
		return books;

	}

	// by id
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Book> getBook(@PathVariable int bookId) {
		logger.info("Retrieving book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			List<Author> authors = bookDao.getBookAuthors(book);
			book.setAuthors(authors);
			List<Genre> genres = bookDao.getBookGenres(book);
			book.setGenres(genres);
			List<Publisher> pubs = bookDao.getBookPublisher(book);
			Publisher pub = null;
			if(!Utils.isEmpty(pubs)) {
				pub = pubs.get(0);
			}
			book.setPublisher(pub);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}
		return new ResponseEntity<Book>(book, responseHeaders, HttpStatus.OK);
	}

	// book genres json
	@RequestMapping(value = "/books/{bookId}/genres", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Genre>> getBookGenresJson(@PathVariable int bookId) {
		logger.info("Retrieving genres under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Genre> genres = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			genres = bookDao.getBookGenres(book);

		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book genre resource not found", 1);
		}
		return new ResponseEntity<List<Genre>>(genres, responseHeaders, HttpStatus.OK);
	}

	// book genres xml
	@RequestMapping(value = "/books/{bookId}/genres", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Genres> getBookGenresXml(@PathVariable int bookId) {
		logger.info("Retrieving genres under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Genres genreVO = new Genres();
		List<Genre> genres = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			genres = bookDao.getBookGenres(book);
			genreVO.setList(genres);
			// if(genres.isEmpty()) throw new EmptyResultDataAccessException(bookId);

		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book genre resource not found", 1);
		}
		return new ResponseEntity<Genres>(genreVO, responseHeaders, HttpStatus.OK);
	}

	// retrieve a specific genre for a book
	@RequestMapping(value = "/books/{bookId}/genres/{genreId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Genre> getSpecificBookGenre(@PathVariable int bookId, @PathVariable int genreId) {
		logger.info("Retrieving genre with id " + genreId + " under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Genre> genres = null;

		try {
			bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		genres = bookDao.checkGenreForABook(bookId, genreId);

		if (Utils.isEmpty(genres)) {
			throw new EmptyResultDataAccessException("Genre resource not found", 1);
		}

		return new ResponseEntity<Genre>(genres.get(0), responseHeaders, HttpStatus.OK);
	}

	// delete a specific genre from a book
	@RequestMapping(value = "/books/{bookId}/genres/{genreId}", method = RequestMethod.DELETE, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Genre> deleteSpecificBookGenre(@PathVariable int bookId, @PathVariable int genreId) {
		logger.info("Removing genre with id " + genreId + " from book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Genre> genre = null;
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		genre = bookDao.checkGenreForABook(bookId, genreId);

		if (Utils.isEmpty(genre)) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		bookDao.deleteBookGenre(book, genre.get(0));

		logger.info("removed genre with id: " + genreId + "from book with id: " + bookId);
		return new ResponseEntity<Genre>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// get book authors json
	@RequestMapping(value = "/books/{bookId}/authors", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Author>> getBookAuthorsJson(@PathVariable int bookId) {
		logger.info("Retrieving authors under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Author> authors = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			authors = bookDao.getBookAuthors(book);

			// if(authors.isEmpty()) throw new EmptyResultDataAccessException(bookId);

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Book author resource not found", 1);
			}
			throw e;
		}
		return new ResponseEntity<List<Author>>(authors, responseHeaders, HttpStatus.OK);
	}

	// get book authors xml
	@RequestMapping(value = "/books/{bookId}/authors", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Authors> getBookAuthorsXml(@PathVariable int bookId) {
		logger.info("Retrieving authors under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Authors authorVO = new Authors();
		List<Author> authors = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			authors = bookDao.getBookAuthors(book);
			authorVO.setList(authors);

			// if(authors.isEmpty()) throw new EmptyResultDataAccessException(bookId);

		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Book author resource not found", 1);
			}
			throw e;
		}
		return new ResponseEntity<Authors>(authorVO, responseHeaders, HttpStatus.OK);
	}

	// retrieve a specific author for a book
	@RequestMapping(value = "/books/{bookId}/authors/{authorId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Author> getSpecificBookAuthor(@PathVariable int bookId, @PathVariable int authorId) {
		logger.info("Retrieving author with id " + authorId + " under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Author> authors = null;

		try {
			bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		authors = bookDao.checkAuthorForABook(bookId, authorId);

		if (Utils.isEmpty(authors)) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}

		return new ResponseEntity<Author>(authors.get(0), responseHeaders, HttpStatus.OK);
	}

	// delete a specific author from a book
	@RequestMapping(value = "/books/{bookId}/authors/{authorId}", method = RequestMethod.DELETE, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Author> deleteSpecificBookAuthor(@PathVariable int bookId, @PathVariable int authorId) {
		logger.info("Removing author with id " + authorId + " from book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Author> author = null;
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		author = bookDao.checkAuthorForABook(bookId, authorId);

		if (Utils.isEmpty(author)) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		bookDao.deleteBookAuthor(book, author.get(0));

		logger.info("removed author with id: " + authorId + "from book with id: " + bookId);
		return new ResponseEntity<Author>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// get book branches json
	@RequestMapping(value = "/books/{bookId}/branches", method = RequestMethod.GET, produces = { "application/JSON" })
	public ResponseEntity<List<Branch>> getBookBranchesJson(@PathVariable int bookId) {
		logger.info("Retrieving branches under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		// Branches branchVO = new Branches();
		List<Branch> branches = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			branches = bookDao.getBookBranches(book);
			// branchVO.setList(branches);

			// if(authors.isEmpty()) throw new EmptyResultDataAccessException(bookId);

		} catch (Exception e) {
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Book branches resource not found", 1);
			}
			throw e;
		}
		return new ResponseEntity<List<Branch>>(branches, responseHeaders, HttpStatus.OK);
	}

	// get book branches xml
	@RequestMapping(value = "/books/{bookId}/branches", method = RequestMethod.GET, produces = { "application/XML" })
	public ResponseEntity<Branches> getBookBranchesXml(@PathVariable int bookId) {
		logger.info("Retrieving branches under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Branches branchVO = new Branches();
		List<Branch> branches = new ArrayList<>();
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
			branches = bookDao.getBookBranches(book);
			branchVO.setList(branches);

			// if(authors.isEmpty()) throw new EmptyResultDataAccessException(bookId);

		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book branches resource not found", 1);
		}
		return new ResponseEntity<Branches>(branchVO, responseHeaders, HttpStatus.OK);
	}

	// retrieve a specific branch for a book
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}", method = RequestMethod.GET, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Branch> getSpecificBookBranch(@PathVariable int bookId, @PathVariable int branchId) {
		logger.info("Retrieving branch with id " + branchId + " under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Branch> branch = null;

		try {
			bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		branch = bookDao.checkBranchForABook(bookId, branchId);

		if (Utils.isEmpty(branch)) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		return new ResponseEntity<Branch>(branch.get(0), responseHeaders, HttpStatus.OK);
	}

	// delete a specific branch for a book
	@RequestMapping(value = "/books/{bookId}/branches/{branchId}", method = RequestMethod.DELETE, produces = {
			"application/JSON", "application/XML" })
	public ResponseEntity<Branch> deleteSpecificBookBranch(@PathVariable int bookId, @PathVariable int branchId) {
		logger.info("Retrieving branch with id " + branchId + " under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		List<Branch> branch = null;
		Book book = new Book();
		try {
			book = bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		branch = bookDao.checkBranchForABook(bookId, branchId);

		if (Utils.isEmpty(branch)) {
			throw new EmptyResultDataAccessException("Branch resource not found", 1);
		}

		bookDao.deleteBookBranch(book, branch.get(0));

		logger.info("removed branch with id: " + branchId + "from book with id: " + bookId);
		return new ResponseEntity<Branch>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

	// get book publisher xml json
	@RequestMapping(value = "/books/{bookId}/publishers", method = RequestMethod.GET, produces = { "application/JSON",
			"application/XML" })
	public ResponseEntity<Publisher> getBookPublisher(@PathVariable int bookId) {
		logger.info("Retrieving publisher under book with id " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();
		Book book = new Book();
		Publisher pub = null;

		book = bookDao.getBookById(bookId);
		if (!Utils.isEmpty(bookDao.getBookPublisher(book))) {
			pub = bookDao.getBookPublisher(book).get(0);
		}

		if (Utils.isEmpty(pub))
			throw new EmptyResultDataAccessException("Book publisher not found", 1);

		return new ResponseEntity<Publisher>(pub, responseHeaders, HttpStatus.OK);
	}

	// add book
	@RequestMapping(value = "/book", method = RequestMethod.POST, consumes = { "application/XML", "application/JSON" })
	@Transactional(propagation = Propagation.REQUIRED)
	public ResponseEntity<String> addBook(@RequestBody @Valid Book book) {
		logger.info("Creating book...");
		int id = bookDao.insertBook(book);
		book.setBookId(id);

		// check if book has authors
		if (!Utils.isEmpty(book.getAuthors())) {
			for (Author a : book.getAuthors()) {
				logger.info("Adding book authors...");
				
				try {
					authorDao.getAuthorById(a.getAuthorId());
				} catch (EmptyResultDataAccessException e) {
					throw new EmptyResultDataAccessException("Author resource not found", 1);
				}

				bookDao.addBookAuthor(book, a);
			}
		}

		// check if book has genres
		if (!Utils.isEmpty(book.getGenres())) {
			for (Genre g : book.getGenres()) {
				try {
					genreDao.getGenreById(g.getGenreId());
				} catch (EmptyResultDataAccessException e) {
					throw new EmptyResultDataAccessException("Genre resource not found", 1);
				}
				logger.info("Adding book genres...");
				bookDao.addBookGenre(book, g);
			}
		}

		// check if book has publisher
		if (book.getPublisher() != null) {

			try {
				pubDao.getPublisherById(book.getPublisher().getPubId());
			} catch (EmptyResultDataAccessException e) {
				if (e instanceof EmptyResultDataAccessException) {
					throw new EmptyResultDataAccessException("Publisher resource not found", 1);
				}
			}
			logger.info("Adding book publisher...");
			bookDao.updateBookPub(book, book.getPublisher());
		}

		// check if book has branch
		if (!Utils.isEmpty(book.getBranches())) {
			for (Branch b : book.getBranches()) {
				
				try {
					branchDao.getBranchById(b.getBranchId());
				} catch (EmptyResultDataAccessException e) {
					if (e instanceof EmptyResultDataAccessException) {
						throw new EmptyResultDataAccessException("Branch resource not found", 1);
					}
				}
				// insert into book_branch table
				logger.info("Adding book branches...");
				bookDao.addBookBranchWithCopies(book, b);
			}
		}

		URI location = URI.create("/books/" + id);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(location);

		logger.info("Created book with id: " + id);
		return new ResponseEntity<String>("Created", responseHeaders, HttpStatus.CREATED);

	}

	// update
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Book> updateBook(@RequestBody @Valid Book book, @PathVariable int bookId) {
		book.setBookId(bookId);
		logger.info("Updating book with id:" + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			bookDao.updateBook(book);
			book = bookDao.getBookById(book.getBookId());

		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		logger.info("updated book with id: " + book.getBookId());
		return new ResponseEntity<Book>(book, responseHeaders, HttpStatus.OK);
	}

	@RequestMapping(value = "/books/{bookId}/author", method = RequestMethod.POST, consumes = { "application/JSON" })
	// add book authors
	public ResponseEntity<List<Author>> addBookAuthorJson(@RequestBody @Valid Author author, @PathVariable int bookId)
			throws Exception {

		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Adding authors under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		// check if author exist
		try {
			author = authorDao.getAuthorById(author.getAuthorId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Author resource not found", 1);
		}

		// check if author is already under this book
		if (!Utils.isEmpty(bookDao.checkAuthorForABook(bookId, author.getAuthorId()))) {
			throw new Exception("Author resource already exist for this book resource");
		}

		logger.info("Adding book author...");
		bookDao.addBookAuthor(book, author);

		List<Author> list = new ArrayList<>();
		try {
			list = bookDao.getBookAuthors(book);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book author resource not found", 1);
		}
		return new ResponseEntity<List<Author>>(list, responseHeaders, HttpStatus.OK);
	}

	// add book authors xml
	@RequestMapping(value = "/books/{bookId}/author", method = RequestMethod.POST, consumes = { "application/XML" })
	public ResponseEntity<Authors> addBookAuthorXML(@RequestBody @Valid Authors authors, @PathVariable int bookId) {

		List<Author> authorList = authors.getList();
		Book book = new Book();
		book.setBookId(bookId);
		book.setAuthors(authorList);
		logger.info("Adding authors under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		if (!Utils.isEmpty(book.getAuthors())) {
			for (Author a : book.getAuthors()) {
				// check if its a new author and insert into author table, otherwise skip
				/*
				 * if (a.getAuthorId() == null) { a.setAuthorId(adao.addAuthorWithID(a)); }
				 */
				// insert into book_authors table
				logger.info("Adding book authors...");
				bookDao.addBookAuthor(book, a);
			}
		}

		authorList = new ArrayList<>();
		try {
			authorList = bookDao.getBookAuthors(book);
			authors.setList(authorList);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<List<Author>>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Book author resource not found", 1);
			}
		}
		return new ResponseEntity<Authors>(authors, responseHeaders, HttpStatus.OK);
	}

	// add book genre json
	@RequestMapping(value = "/books/{bookId}/genre", method = RequestMethod.POST, consumes = { "application/JSON" })
	public ResponseEntity<List<Genre>> addBookGenre(@RequestBody @Valid Genre genre, @PathVariable int bookId)
			throws Exception {
		Book book = new Book();
		book.setBookId(bookId);
		// book.setGenres(genre);
		logger.info("Adding genre under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		// check if genre exist
		try {
			genre = genreDao.getGenreById(genre.getGenreId());
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Genre resource not found", 1);
		}

		// check if genre is already under this book
		if (!Utils.isEmpty(bookDao.checkGenreForABook(bookId, genre.getGenreId()))) {
			throw new Exception("Genre resource already exist for this book resource");
		}

		logger.info("Adding book genres...");
		bookDao.addBookGenre(book, genre);

		List<Genre> genres = new ArrayList<>();
		try {
			genres = bookDao.getBookGenres(book);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof EmptyResultDataAccessException) {
				// return new ResponseEntity<List<Author>>(null, responseHeaders,
				// HttpStatus.NOT_FOUND);
				throw new EmptyResultDataAccessException("Book genre resource not found", 1);
			}
		}
		return new ResponseEntity<List<Genre>>(genres, responseHeaders, HttpStatus.OK);
	}

	// add book branch json
	@RequestMapping(value = "/books/{bookId}/branch", method = RequestMethod.POST, consumes = { "application/JSON" })
	public ResponseEntity<List<Branch>> addBookBranch(@RequestBody @Valid Branch branch, @PathVariable int bookId) throws Exception {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Adding branch under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		// check if branch id exist
		try {
			branchDao.getBranchById(branch.getBranchId());
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Branch resource not found", 1);
			}
		}

		// check if branch is already under this book
		if (!Utils.isEmpty(bookDao.checkBranchForABook(bookId, branch.getBranchId()))) {
			throw new Exception("Branch resource already exist for this book resource");
		}

		logger.info("Adding book branches...");
		bookDao.addBookBranchWithCopies(book, branch);

		List<Branch> branches = new ArrayList<>();
		try {
			branches = bookDao.getBookBranches(book);
		} catch (EmptyResultDataAccessException e) {
			throw new EmptyResultDataAccessException("Book branch resource not found", 1);
		}
		return new ResponseEntity<List<Branch>>(branches, responseHeaders, HttpStatus.OK);
	}

	// update book publisher
	@RequestMapping(value = "/books/{bookId}/publishers", method = RequestMethod.PUT, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Publisher> updateBookPublisher(@RequestBody @Valid Publisher pub, @PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Updating book publisher with book id: " + bookId);
		HttpHeaders responseHeaders = new HttpHeaders();

		//check if book exist
		try {
			bookDao.getBookById(bookId);
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Book resource not found", 1);
			}
		}
		//check if publisher exist
		try {
			pubDao.getPublisherById(pub.getPubId());
		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Publisher resource not found", 1);
			}
		}
		
		bookDao.updateBookPub(book, pub);
		book.setPublisher(bookDao.getBookPublisher(book).get(0));

		logger.info("updated book publisher with id: " + book.getBookId());
		return new ResponseEntity<Publisher>(book.getPublisher(), responseHeaders, HttpStatus.OK);
	}

	// delete book genre
	/*@RequestMapping(value = "/books/{bookId}/genres", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Book> deleteBookGenreJson(@RequestBody @Valid List<Genre> genres, @PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Deleting genre under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		book = bookDao.getBookById(book.getBookId());
		if (book == null) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		if (!Utils.isEmpty(genres)) {
			for (Genre g : genres) {
				// delete from book_genres table
				bookDao.deleteBookGenre(book, g);
			}
		}

		logger.info("deleted genres under book id: " + book.getBookId());
		return new ResponseEntity<Book>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}*/

	// delete book author
	/*@RequestMapping(value = "/books/{bookId}/authors", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<List<Author>> deleteBookAuthorJson(@RequestBody @Valid List<Author> authors,
			@PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Deleting authors under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		book = bookDao.getBookById(book.getBookId());
		if (book == null) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		if (!Utils.isEmpty(authors)) {
			for (Author a : authors) {
				// delete from book_authors table
				bookDao.deleteBookAuthor(book, a);
			}
		}

		logger.info("deleted authors under book id: " + book.getBookId());
		return new ResponseEntity<List<Author>>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}*/

	// delete book branches
	/*@RequestMapping(value = "/books/{bookId}/branches", method = RequestMethod.DELETE, consumes = {
			"application/JSON" })
	public ResponseEntity<List<Branch>> deleteBookBranchesJson(@RequestBody @Valid List<Branch> branches,
			@PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Deleting branches under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		book = bookDao.getBookById(book.getBookId());
		if (book == null) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		if (!Utils.isEmpty(branches)) {
			for (Branch a : branches) {
				// delete from book_copies table
				bookDao.deleteBookBranch(book, a);
			}
		}

		logger.info("deleted branches under book id: " + book.getBookId());
		return new ResponseEntity<List<Branch>>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}*/

	// delete book branches
	/*@RequestMapping(value = "/books/{bookId}/publishers", method = RequestMethod.DELETE, consumes = {
			"application/JSON" })
	public ResponseEntity<Publisher> deleteBookPublishersJson(@PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Deleting Publisher under book id: " + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();
		book = bookDao.getBookById(book.getBookId());
		if (book == null) {
			throw new EmptyResultDataAccessException("Book resource not found", 1);
		}

		bookDao.deleteBookPub(book);

		logger.info("deleted Publisher under book id: " + book.getBookId());
		return new ResponseEntity<Publisher>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}*/

	// delete
	@RequestMapping(value = "/books/{bookId}", method = RequestMethod.DELETE, consumes = { "application/XML",
			"application/JSON" })
	public ResponseEntity<Book> deleteBook(@PathVariable int bookId) {
		Book book = new Book();
		book.setBookId(bookId);
		logger.info("Deleting book with id:" + book.getBookId());
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			book = bookDao.getBookById(book.getBookId());
			if (book == null) {
				throw new EmptyResultDataAccessException(bookId);
			}
			bookDao.deleteBook(book);

		} catch (EmptyResultDataAccessException e) {
			if (e instanceof EmptyResultDataAccessException) {
				throw new EmptyResultDataAccessException("Book resource not found", 1);
				// return new ResponseEntity<Book>(null, responseHeaders, HttpStatus.NOT_FOUND);
			}
		}

		logger.info("deleted book with id: " + book.getBookId());
		return new ResponseEntity<Book>(null, responseHeaders, HttpStatus.NO_CONTENT);
	}

}
