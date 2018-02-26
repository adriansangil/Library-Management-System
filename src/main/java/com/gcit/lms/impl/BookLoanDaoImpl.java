package com.gcit.lms.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;
import com.gcit.lms.mapper.AuthorMapper;
import com.gcit.lms.mapper.BookMapper;
import com.gcit.lms.mapper.BranchMapper;
import com.gcit.lms.mapper.GenreMapper;
import com.gcit.lms.mapper.PublisherMapper;

@Component
public class BookLoanDaoImpl implements BookDAO {
	Logger logger = LoggerFactory.getLogger(BookLoanDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Book> listBooks() {
		String sql = "select * from tbl_book";
		List<Book> Books = jdbcTemplate.query(sql, new BookMapper());
		return Books;
	}

	@Override
	public Book getBookById(int bookId) {
		String sql = "SELECT * FROM library.tbl_book a where a.bookId=?";
		Book Book = jdbcTemplate.queryForObject(sql, new Object[] { bookId }, new BookMapper());
		logger.debug("Logging a message from Book DAO here is the Book object {}", Book);
		return Book;
	}

	@Override
	public int insertBook(final Book book) {
		final String sql = "INSERT INTO tbl_book (title) VALUES (?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, book.getTitle());
				return ps;
			}
		}, holder);

		int bookId = holder.getKey().intValue();
		// Book.setbookId(newUserId);

		logger.debug("returned value from backend is {}", bookId);

		return bookId;
	}

	@Override
	public void updateBook(Book book) {
		String sql = "update tbl_book set title = ? where bookId = ?";
		jdbcTemplate.update(sql, new Object[] { book.getTitle(), book.getBookId() });

	}

	@Override
	public void deleteBook(Book book) {
		String sql = "delete from tbl_book where bookId = ?";
		jdbcTemplate.update(sql, new Object[] { book.getBookId() });
	}
	
	

	@Override
	public List<Author> getBookAuthors(Book book) {
		String sql = "select * from tbl_author where authorId IN (select authorId from tbl_book_authors where bookId = ?)";
		return jdbcTemplate.query(sql, new Object[] { book.getBookId() }, new AuthorMapper());
	}

	@Override
	public List<Genre> getBookGenres(Book book) {
		String sql = "select * from tbl_genre where genre_id IN (select genre_id from tbl_book_genres where bookId = ?)";
		return jdbcTemplate.query(sql, new Object[] { book.getBookId() }, new GenreMapper());
	}

	@Override
	public List<Publisher> getBookPublisher(Book book) {
		String sql = "select a.* from tbl_publisher a, tbl_book b where a.publisherId = b.pubId and b.bookId = ?";
		return jdbcTemplate.query (sql, new Object[] { book.getBookId() }, new PublisherMapper());
	}

	@Override
	public void addBookAuthor(Book book, Author author) {
		String sql = "insert into tbl_book_authors (bookId, authorId) values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), author.getAuthorId() });
		
	}

	@Override
	public void addBookGenre(Book book, Genre genre) {
		String sql = "insert into tbl_book_genres (bookId, genre_id) values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), genre.getGenreId() });
		
	}

	@Override
	public void updateBookPub(Book book, Publisher pub) {
		String sql = "update tbl_book SET pubId = ? where bookId = ?";
		jdbcTemplate.update(sql, new Object[] { pub.getPubId(),book.getBookId() });
		
	}

	@Override
	public void addBookBranchWithCopies(Book book, Branch branch) {
		String sql = "insert into tbl_book_copies (bookId, branchId, noOfCopies) values (?, ?, ?)";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), branch.getBranchId(), branch.getNoOfCopies() });
		
	}

	@Override
	public List<Branch> getBookBranches(Book book) {
		String sql = "select * from tbl_book_copies a, tbl_library_branch b where a.branchId = b.branchId and a.bookId = ?";
		return jdbcTemplate.query(sql, new Object[] { book.getBookId() }, new BranchMapper());
	}

	@Override
	public void deleteBookGenre(Book book, Genre genre) {
		String sql = "delete from tbl_book_genres where bookId = ? and genre_id = ?";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), genre.getGenreId() });
		
	}

	@Override
	public void deleteBookAuthor(Book book, Author author) {
		String sql = "delete from tbl_book_authors where bookId = ? and authorId = ?";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), author.getAuthorId() });
		
	}

	@Override
	public void deleteBookBranch(Book book, Branch branch) {
		String sql = "delete from tbl_book_copies where bookId = ? and branchId = ?";
		jdbcTemplate.update(sql, new Object[] { book.getBookId(), branch.getBranchId() });
		
	}

	@Override
	public void deleteBookPub(Book book) {
		String sql = "update tbl_book SET pubId = NULL where bookId = ?";
		jdbcTemplate.update(sql, new Object[] { book.getBookId() });
		
	}

	@Override
	public List<Genre> checkGenreForABook(int bookId, int genreId) {
		String sql = "SELECT b.* from tbl_book_genres a, tbl_genre b where a.genre_id = b.genre_Id and a.genre_id = ? and a.bookId = ?";
		List<Genre> genre= jdbcTemplate.query(sql, new Object[] { genreId, bookId }, new GenreMapper());
		return genre;
	}

	@Override
	public List<Author> checkAuthorForABook(int bookId, int authorId) {
		String sql = "SELECT b.* from tbl_book_authors a, tbl_author b where a.authorId = b.authorId and a.authorId = ? and a.bookId = ?";
		List<Author> author= jdbcTemplate.query(sql, new Object[] { authorId, bookId }, new AuthorMapper());
		return author;
		
	}

	@Override
	public List<Branch> checkBranchForABook(int bookId, int branchId) {
		String sql = "SELECT b.* from tbl_book_copies a, tbl_library_branch b where a.branchId = b.branchId and a.branchId = ? and a.bookId = ?";
		List<Branch> branch= jdbcTemplate.query(sql, new Object[] { branchId, bookId }, new BranchMapper());
		return branch;
		
	}
	
	

}
