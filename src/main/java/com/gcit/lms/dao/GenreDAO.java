package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Book;

public interface GenreDAO {
	List<Genre> listGenres();
	Genre getGenreById(int genreId);
	int insertGenre(Genre genre);
	void updateGenre(Genre genre);
	void deleteGenre(Genre genre);
	List<Book> getGenreBooks(Genre genre);
	List<Book> checkBookUnderAGenre(int bookId, int genreId);
	
	
	

}
