package com.fajar.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fajar.annotation.Dto;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto 
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message extends BaseEntity{
	 
    public Message(String sender, String content, Date date2, Long valueOf, String reqId2) { 
    	this.sender = sender;
    	this.text = content;
    	this.date = date2;
    	this.setId(valueOf);
    	this.requestId =reqId2;
    	 
	}

    @Column
    private int admin;
    @Column
	private String sender;
    @Column
    private String text; 
    @JsonFormat(pattern = "DD-MM-yyyy' 'hh:mm:ss")
    @Column
    private Date date;
    @Column
    private String alias;
    @Column(name="request_id")
    private String requestId;
    
}
