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

import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Publisher;
import com.gcit.lms.entity.Book;
import com.gcit.lms.mapper.PublisherMapper;
import com.gcit.lms.mapper.BookMapper;

@Component
public class PublisherDaoImpl implements PublisherDAO {
	Logger logger = LoggerFactory.getLogger(PublisherDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Publisher> listPublishers() {
		String sql = "select * from tbl_publisher";
		List<Publisher> publishers = jdbcTemplate.query(sql, new PublisherMapper());
		return publishers;
	}

	@Override
	public Publisher getPublisherById(int publisherId) {
		String sql = "SELECT * FROM library.tbl_publisher a where a.publisherId=?";
		Publisher publisher = jdbcTemplate.queryForObject(sql, new Object[] { publisherId }, new PublisherMapper());
		logger.debug("Logging a message from Publisher DAO here is the publisher object {}", publisher);
		return publisher;
	}

	@Override
	public int insertPublisher(final Publisher publisher) {
		final String sql = "INSERT INTO tbl_publisher (publisherName, publisherAddress, publisherPhone) VALUES (?,?,?)";

		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, publisher.getName());
				ps.setString(2, publisher.getAddress());
				ps.setString(3, publisher.getPhone());
				return ps;
			}
		}, holder);

		int publisherId = holder.getKey().intValue();
		// publisher.setPublisherId(newUserId);

		logger.debug("returned value from backend is {}", publisherId);

		return publisherId;
	}

	@Override
	public void updatePublisher(Publisher publisher) {
		String sql = "update tbl_publisher set publisherName = ?, publisherAddress = ?, publisherPhone = ? where publisherId = ?";
		jdbcTemplate.update(sql, new Object[] { publisher.getName(), publisher.getAddress(), publisher.getPhone(), publisher.getPubId() });

	}

	@Override
	public void deletePublisher(Publisher publisher) {
		String sql = "delete from tbl_publisher where publisherId = ?";
		jdbcTemplate.update(sql, new Object[] { publisher.getPubId() });
	}

	@Override
	public List<Book> getPublisherBooks(Publisher publisher) {
		String sql = "select * from tbl_book where pubId = ?";
		return jdbcTemplate.query(sql, new Object[] { publisher.getPubId() }, new BookMapper());
	}

	@Override
	public List<Book> checkBookUnderAPublisher(int bookId, int publisherId) {
		String sql = "SELECT * from tbl_book b where b.pubId = ? and b.bookId = ?";
		List<Book> book= jdbcTemplate.query(sql, new Object[] { publisherId, bookId }, new BookMapper());
		return book;
	}

}
