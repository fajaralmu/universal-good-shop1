package com.fajar.test;

import java.rmi.Remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RMIObj implements Remote{
 
	private String code;
	private String name;
	 
	
	

}
