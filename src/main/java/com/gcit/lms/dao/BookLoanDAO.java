package com.gcit.lms.dao;

import java.util.List;

import com.gcit.lms.entity.BookLoan;

public interface BookLoanDAO {
	List<BookLoan> listBookLoans();
	void insertBookLoan(BookLoan loan);
	void updateBookLoan(BookLoan loan);
	void deleteBookLoan(BookLoan loan);

}
