package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.BookCopy;

@XmlRootElement(name="books")
@XmlAccessorType (XmlAccessType.FIELD)
public class BookCopies implements Serializable{

	private static final long serialVersionUID = 5690331472908386756L;
	
	@XmlElement(name = "book")
	private List<BookCopy> list;

	public List<BookCopy> getList() {
		return list;
	}

	public void setList(List<BookCopy> list) {
		this.list = list;
	}

}
