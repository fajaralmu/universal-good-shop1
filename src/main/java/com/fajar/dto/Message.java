package com.fajar.dto;

import java.util.Date;

import com.fajar.annotation.Dto;

import lombok.Builder;
import lombok.Data;

@Dto
@Data
@Builder
public class Message {
	 
    private String sender;
    private String text; 
    private Date date;
    private long id;
    private String requestId;
    
}
