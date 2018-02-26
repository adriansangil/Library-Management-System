package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.BookLoan;

@XmlRootElement(name="BookLoans")
@XmlAccessorType (XmlAccessType.FIELD)
public class BookLoans implements Serializable{

	private static final long serialVersionUID = 5690331472908386756L;
	
	@XmlElement(name = "bookLoan")
	private List<BookLoan> list;

	public List<BookLoan> getList() {
		return list;
	}

	public void setList(List<BookLoan> list) {
		this.list = list;
	}

}
