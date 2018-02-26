package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.Publisher;
import com.gcit.lms.entity.Book;

public interface PublisherDAO {
	List<Publisher> listPublishers();
	Publisher getPublisherById(int pubId);
	int insertPublisher(Publisher pub);
	void updatePublisher(Publisher pub);
	void deletePublisher(Publisher pub);
	List<Book> getPublisherBooks(Publisher pub);
	List<Book> checkBookUnderAPublisher(int bookId, int pubId);
	
	
	

}
