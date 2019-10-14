package com.fajar.dto;

import com.fajar.annotation.Dto;

@Dto
public class OutputMessage {

	private String from;
    private String text;
    private String time;
    
	public OutputMessage(String from, String text, String time) {
		super();
		this.from = from;
		this.text = text;
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "{from:\"" + from + "\", text:\"" + text + "\", time:\"" + time + "\"}";
	}
	
	
}
