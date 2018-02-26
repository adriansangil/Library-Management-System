package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;

public interface BorrowerDAO {
	List<Borrower> listBorrowers();
	Borrower getBorrowerById(int borrowerId);
	int insertBorrower(Borrower borrower);
	void updateBorrower(Borrower borrower);
	void deleteBorrower(Borrower borrower);
	List<BookLoan> getBorrowerBooks(Borrower borrower);
	List<BookLoan> checkBookUnderABorrower(int bookId, int cardNo);
	List<BookLoan> getBorrowerBooksWithFilter(Borrower borrower, Integer branchId, Integer bookId, Boolean overdue, Boolean returned);

}
