package com.fajar.shoppingmart.service.report.builder;

public interface OnProgress {
	
	public void onProgress(int taxProportion, int totalProportion, int generalProportion, String message);

}
