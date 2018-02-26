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

import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Book;
import com.gcit.lms.mapper.GenreMapper;
import com.gcit.lms.mapper.BookMapper;

@Component
public class GenreDaoImpl implements GenreDAO {
	Logger logger = LoggerFactory.getLogger(GenreDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Genre> listGenres() {
		String sql = "select * from tbl_genre";
		List<Genre> genres = jdbcTemplate.query(sql, new GenreMapper());
		return genres;
	}

	@Override
	public Genre getGenreById(int genreId) {
		String sql = "SELECT * FROM library.tbl_genre a where a.genre_id=?";
		Genre genre = jdbcTemplate.queryForObject(sql, new Object[] { genreId }, new GenreMapper());
		logger.debug("Logging a message from Genre DAO here is the genre object {}", genre);
		return genre;
	}

	@Override
	public int insertGenre(final Genre genre) {
		final String sql = "INSERT INTO tbl_genre (genre_name) VALUES (?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, genre.getGenreName());
				return ps;
			}
		}, holder);

		int genre_id = holder.getKey().intValue();
		// genre.setGenreId(newUserId);

		logger.debug("returned value from backend is {}", genre_id);

		return genre_id;
	}

	@Override
	public void updateGenre(Genre genre) {
		String sql = "update tbl_genre set genre_name = ? where genre_id = ?";
		jdbcTemplate.update(sql, new Object[] { genre.getGenreName(), genre.getGenreId() });

	}

	@Override
	public void deleteGenre(Genre genre) {
		String sql = "delete from tbl_genre where genre_id = ?";
		jdbcTemplate.update(sql, new Object[] { genre.getGenreId() });
	}

	@Override
	public List<Book> getGenreBooks(Genre genre) {
		String sql = "select * from tbl_book where bookId IN (select bookId from tbl_book_genres where genre_id = ?)";
		return jdbcTemplate.query(sql, new Object[] { genre.getGenreId() }, new BookMapper());
	}

	@Override
	public List<Book> checkBookUnderAGenre(int bookId, int genreId) {
		String sql = "SELECT b.* from tbl_book_genres a, tbl_book b where a.bookId = b.bookId and a.genre_id = ? and a.bookId = ?";
		List<Book> book= jdbcTemplate.query(sql, new Object[] { genreId, bookId }, new BookMapper());
		return book;
	}

}
