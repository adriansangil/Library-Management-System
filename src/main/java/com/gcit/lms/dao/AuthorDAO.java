package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;

public interface AuthorDAO {
	List<Author> listAuthors();
	Author getAuthorById(int authorId);
	int insertAuthor(Author author);
	void updateAuthor(Author author);
	void deleteAuthor(Author author);
	List<Book> getAuthorBooks(Author author);
	List<Book> checkBookUnderAnAuthor(int bookId, int authorId);

}
