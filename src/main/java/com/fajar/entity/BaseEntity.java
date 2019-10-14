package com.fajar.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import com.fajar.annotation.Dto;
import com.fajar.annotation.FormField;
import com.fajar.config.EntityElement;
import com.fajar.config.EntityElement.EntityElementBuilder;
import com.fajar.util.JSONUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Dto
@MappedSuperclass
 
public class BaseEntity {
	
	
	
	@Column(name="created_date")
	private Date createdDate;
	@Column(name="modified_date")
	private Date modifiedDate;
	@Column(name="deleted")
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
