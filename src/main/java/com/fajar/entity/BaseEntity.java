package com.fajar.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.util.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Dto
@MappedSuperclass
 
public class BaseEntity {
	
	
	
	@Column(name="created_date")
	//@JsonIgnore
	@FormField
	private Date createdDate;
	@Column(name="modified_date")
	@JsonIgnore
	private Date modifiedDate;
	@Column(name="deleted")
	@JsonIgnore
	private boolean deleted;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@FormField
	@Column
	private Long id;
	
	
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return JSONUtil.objectToJson(this);
	}

	@PrePersist
	private void prePersist() {
		if(this.createdDate == null) {
			this.createdDate = new Date();
		}
	}
	
}
