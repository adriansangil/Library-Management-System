package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;

public interface BookDAO {
	List<Book> listBooks();
	Book getBookById(int BookId);
	int insertBook(Book Book);
	void updateBook(Book Book);
	void deleteBook(Book Book);
	List<Author> getBookAuthors(Book book);
	List<Genre> getBookGenres(Book book);
	List<Publisher> getBookPublisher(Book book);
	List<Branch> getBookBranches(Book book);
	void addBookAuthor(Book book, Author author);
	void addBookGenre(Book book, Genre genre);
	void updateBookPub(Book book, Publisher pub);
	void addBookBranchWithCopies(Book book, Branch branch);
	void deleteBookGenre(Book book, Genre genre);
	void deleteBookAuthor(Book book, Author author);
	void deleteBookBranch(Book book, Branch branch);
	void deleteBookPub(Book book);
	List<Genre> checkGenreForABook(int bookId, int genreId);
	List<Author> checkAuthorForABook(int bookId, int authorId);
	List<Branch> checkBranchForABook(int bookId, int authorId);
	
}
