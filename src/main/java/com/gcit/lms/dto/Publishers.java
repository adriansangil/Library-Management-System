package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Publisher;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Publishers implements Serializable {

	private static final long serialVersionUID = -7857615694652069807L;
	
	@XmlElement(name = "author")
	private List<Publisher> list;

	public List<Publisher> getList() {
		return list;
	}

	public void setList(List<Publisher> list) {
		this.list = list;
	}

}
