package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Book;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Books implements Serializable{

	private static final long serialVersionUID = -3253914904405308831L;
	@XmlElement(name = "book")
	private List<Book> list;

	public List<Book> getList() {
		return list;
	}

	public void setList(List<Book> list) {
		this.list = list;
	}

}
