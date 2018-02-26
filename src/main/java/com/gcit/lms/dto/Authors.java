package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Author;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Authors implements Serializable{

	private static final long serialVersionUID = 5591209821852158061L;
	@XmlElement(name = "author")
	private List<Author> list;

	public List<Author> getList() {
		return list;
	}

	public void setList(List<Author> list) {
		this.list = list;
	}

}
