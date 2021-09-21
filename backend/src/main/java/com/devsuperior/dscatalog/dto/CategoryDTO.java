package com.devsuperior.dscatalog.dto;

import java.io.Serializable;

import com.devsuperior.dscatalog.entities.Category;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String nameString;
	
	public CategoryDTO() {
		
	}

	public CategoryDTO(Long id, String nameString) {
		super();
		this.id = id;
		this.nameString = nameString;
	}
	
	public CategoryDTO(Category entity) {
		this.id = entity.getId();
		this.nameString = entity.getName();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
}
