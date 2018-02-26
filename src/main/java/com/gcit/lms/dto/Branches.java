package com.gcit.lms.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.gcit.lms.entity.Branch;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Branches implements Serializable{

	private static final long serialVersionUID = -8992980319837759063L;
	@XmlElement(name = "branch")
	private List<Branch> list;

	public List<Branch> getList() {
		return list;
	}

	public void setList(List<Branch> list) {
		this.list = list;
	}

}
