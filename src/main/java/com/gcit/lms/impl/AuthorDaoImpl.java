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

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.mapper.AuthorMapper;
import com.gcit.lms.mapper.BookMapper;

@Component
public class AuthorDaoImpl implements AuthorDAO {
	Logger logger = LoggerFactory.getLogger(AuthorDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Author> listAuthors() {
		String sql = "select * from tbl_author";
		List<Author> authors = jdbcTemplate.query(sql, new AuthorMapper());
		return authors;
	}

	@Override
	public Author getAuthorById(int authorId) {
		String sql = "SELECT * FROM library.tbl_author a where a.authorId=?";
		Author author = jdbcTemplate.queryForObject(sql, new Object[] { authorId }, new AuthorMapper());
		logger.debug("Logging a message from Author DAO here is the author object {}", author);
		return author;
	}

	@Override
	public int insertAuthor(final Author author) {
		final String sql = "INSERT INTO tbl_author (authorName) VALUES (?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, author.getAuthorName());
				return ps;
			}
		}, holder);

		int authorId = holder.getKey().intValue();
		// author.setAuthorId(newUserId);

		logger.debug("returned value from backend is {}", authorId);

		return authorId;
	}

	@Override
	public void updateAuthor(Author author) {
		String sql = "update tbl_author set authorName = ? where authorId = ?";
		jdbcTemplate.update(sql, new Object[] { author.getAuthorName(), author.getAuthorId() });

	}

	@Override
	public void deleteAuthor(Author author) {
		String sql = "delete from tbl_author where authorId = ?";
		jdbcTemplate.update(sql, new Object[] { author.getAuthorId() });
	}

	@Override
	public List<Book> getAuthorBooks(Author author) {
		String sql = "select * from tbl_book where bookId IN (select bookId from tbl_book_authors where authorId = ?)";
		return jdbcTemplate.query(sql, new Object[] { author.getAuthorId() }, new BookMapper());
	}

	@Override
	public List<Book> checkBookUnderAnAuthor(int bookId, int authorId) {
		String sql = "SELECT b.* from tbl_book_authors a, tbl_book b where a.bookId = b.bookId and a.authorId = ? and a.bookId = ?";
		List<Book> book= jdbcTemplate.query(sql, new Object[] { authorId, bookId }, new BookMapper());
		return book;
	}

}
