package com.fajar.dto;

import java.util.Date;

import com.fajar.annotation.Dto;
import com.fajar.entity.BaseEntity;
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
public class Message extends BaseEntity{
	 
    public Message(String sender, String content, Date date2, Long valueOf, String reqId2) { 
    	this.sender = sender;
    	this.text = content;
    	this.date = date2;
    	this.setId(valueOf);
    	this.requestId =reqId2;
    	 
	}

    private int admin;
	private String sender;
    private String text; 
    @JsonFormat(pattern = "DD-MM-yyyy' 'hh:mm:ss")
    private Date date;
    
    private String requestId;
    
}
