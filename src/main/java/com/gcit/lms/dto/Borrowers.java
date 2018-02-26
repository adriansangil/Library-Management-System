package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Borrower;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Borrowers implements Serializable{

	private static final long serialVersionUID = 1746740606997034809L;
	
	@XmlElement(name = "borrower")
	private List<Borrower> list;

	public List<Borrower> getList() {
		return list;
	}

	public void setList(List<Borrower> list) {
		this.list = list;
	}

}
