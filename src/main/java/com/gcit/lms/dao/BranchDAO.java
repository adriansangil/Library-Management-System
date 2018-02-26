package com.gcit.lms.dao;

import java.sql.Timestamp;
import java.util.List;

import com.gcit.lms.entity.BookCopy;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;

public interface BranchDAO {
	List<Branch> listBranches();
	Branch getBranchById(int branchId);
	int insertBranch(Branch branch);
	void updateBranch(Branch branch);
	void deleteBranch(Branch branch);
	List<BookCopy> getBranchBooks(Branch branch);
	List<BookCopy> checkBookUnderABranch(int bookId, int branchId);
	
	List<Borrower> getBranchBorrowers(Branch branch);
	List<Borrower> getSpecificBranchBorrower(int branchId, int cardNo);
	
	List<BookLoan> getBranchBorrowerLoans(int branchId, int cardNo, Timestamp dateOut, Boolean returned,
			Boolean overdue);
	
	List<BookLoan> getBranchBorrowerLoansByBook(BookLoan bookLoan, Timestamp dateOut, Boolean returned, Boolean overDue);
	
	void updateSpecificBranchBorrowerLoan(BookLoan bookloan);
	
	void loanABook(BookLoan bl);
	
	void updateBookCopies(int branchId, int bookId, Integer noOfCopies);
	
	

}
