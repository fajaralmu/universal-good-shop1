package com.fajar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {
	
	@Autowired
	private RealtimeService2 realtimeService;
	
	private double currentProgress=  0.0;
	
	public void init() {
		currentProgress = 0.0;
		realtimeService.sendProgress(1);
	}
	
	public void sendProgress(double progress, double maxProgress, double percent, boolean newProgress) {
		if(newProgress) {
			currentProgress = 0.0;
		}
		currentProgress+=(progress/maxProgress);
		System.out.println("| | | | |  PROGRESS: "+currentProgress+" adding :"+progress+"/"+maxProgress+", portion: "+percent+" ==> "+ currentProgress*percent);
		realtimeService.sendProgress(currentProgress*percent);
	}

	public void sendComplete() {
		System.out.println("________COMPLETE PROGRESS________");
		realtimeService.sendProgress(100);
		
	}

}
