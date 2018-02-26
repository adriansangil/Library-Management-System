package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Genre;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Genres implements Serializable{

	private static final long serialVersionUID = 2670856066050110684L;
	
	@XmlElement(name = "genre")
	private List<Genre> list;

	public List<Genre> getList() {
		return list;
	}

	public void setList(List<Genre> list) {
		this.list = list;
	}

}
