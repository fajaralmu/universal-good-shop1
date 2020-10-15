package com.fajar.shoppingmart.service.report.builder;

import static com.fajar.shoppingmart.util.ExcelReportUtil.addMergedRegion;
import static com.fajar.shoppingmart.util.ExcelReportUtil.createRow;
import static com.fajar.shoppingmart.util.ExcelReportUtil.curr;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import com.fajar.shoppingmart.dto.Filter;
import com.fajar.shoppingmart.dto.ReportCategory;
import com.fajar.shoppingmart.service.report.data.ReportData;
import com.fajar.shoppingmart.service.report.data.ReportRowData;
import com.fajar.shoppingmart.util.ExcelReportUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BalanceReportBuilder extends ReportBuilder {

	private static final String PROFIT_CODE = "4";
	private static final String LOSS_CODE = "5";
	private static final String BALANCE_DEBIT = "2";
	private static final String BALANCE_CREDIT = "4";
	
	private static final int FINAL_BALANCE_INDEX = 6;
	private static final int LOSS_PROFIT_BALANCE_INDEX = 5;
	private static final int ADJUSTED_BALANCE_INDEX = 4;
	private static final int ADJUSTING_BALANCE_INDEX = 3;
	private static final int REMAINING_BALANCE_INDEX = 2;
	private static final int CHANGING_BALANCE_INDEX = 1;
	private static final int FORMER_BALANCE_INDEX = 0;
	
	private Map<ReportCategory, ReportRowData> formerBalance;
	private Map<ReportCategory, ReportRowData> thisYearCashflow;
	private Map<ReportCategory, ReportRowData> remainingBalance = new HashMap<>();
	private Map<ReportCategory, ReportRowData> adjustingBalance = emptyCategories();
	private Map<ReportCategory, ReportRowData> adjustedBalance = new HashMap<>();
	private Map<ReportCategory, ReportRowData> lossProfitBalance = new HashMap<>();
	private Map<ReportCategory, ReportRowData> finalBalance = new HashMap<>();
	
	private long businessProfit = 0L;

	public BalanceReportBuilder(  ReportData reportData) {
		super(  reportData);

	}
	
	private void setFormerBalance(Map<ReportCategory, ReportRowData> formerBalance) {
		if(null == formerBalance) {
			formerBalance = emptyCategories();
		}
		this.formerBalance = formerBalance;
	}
	
	private void buildRemainingBalance() {
		remainingBalance.clear();
		
		for(ReportCategory reportCategory:ReportCategory.values()) {
			ReportRowData former = formerBalance.get(reportCategory);
			ReportRowData current = thisYearCashflow.get(reportCategory);
			
			long debitSum = former.getDebitAmount() + current.getDebitAmount();
			long creditSum = former.getCreditAmount() + current.getCreditAmount();
			
			long debitAmount = 0l;
			long creditAmount = 0l;
			
			if(debitSum > creditSum) {
				debitAmount = debitSum - creditSum;
			}
			
			if(creditSum > debitSum) {
				creditAmount = creditSum - debitSum;
			} 
			
			ReportRowData rowData = new ReportRowData(reportCategory, debitAmount, creditAmount);
			remainingBalance.put(reportCategory, rowData);
		}
	}
	
	/**
	 * build lossProfitBalance using adjustedBalance
	 */
	private void buildLossProfitBalanceFromAdjustedBalance() {
		lossProfitBalance.clear();
		for(ReportCategory reportCategory:ReportCategory.values()) {
			ReportRowData adjustedData = adjustedBalance.get(reportCategory);

			long debitAmount = 0l;
			long creditAmount = 0l;
			
			if(reportCategory.codeStartsWith(LOSS_CODE)) {
				debitAmount = adjustedData.getDebitAmount();
			}
			if(reportCategory.codeStartsWith(PROFIT_CODE)) {
				creditAmount = adjustedData.getCreditAmount();
			}
			
			ReportRowData rowData = new ReportRowData(reportCategory, debitAmount, creditAmount);
			lossProfitBalance.put(reportCategory, rowData);
		}
	}
	
	/**
	 * build finalBalance using adjustedBalance
	 */
	private void buildFinalBalanceFromAdjustedBalance() {
		finalBalance.clear();
		for(ReportCategory reportCategory:ReportCategory.values()) {
			ReportRowData adjustedData = adjustedBalance.get(reportCategory);

			long debitAmount = 0l;
			long creditAmount = 0l;
			
			if(reportCategory.codeLeftCharLessThan(BALANCE_DEBIT)) {
				debitAmount = adjustedData.getDebitAmount();
			}
			if(reportCategory.codeLeftCharLessThan(BALANCE_CREDIT)) {
				creditAmount = adjustedData.getCreditAmount();
			}
			
			ReportRowData rowData = new ReportRowData(reportCategory, debitAmount, creditAmount);
			finalBalance.put(reportCategory, rowData);
		}
	}
	
	private void buildAdjustedBalance() {
		adjustedBalance.clear();
		for(ReportCategory reportCategory:ReportCategory.values()) {
			ReportRowData remaining = remainingBalance.get(reportCategory);
			ReportRowData adjusting = adjustingBalance.get(reportCategory);
			
			long debitSum = remaining.getDebitAmount() + adjusting.getDebitAmount();
			long creditSum = remaining.getCreditAmount() + adjusting.getCreditAmount();
			
			long debitAmount = 0l;
			long creditAmount = 0l;
			
			if(debitSum > creditSum) {
				debitAmount = debitSum - creditSum;
			}
			
			if(creditSum > debitSum) {
				creditAmount = creditSum - debitSum;
			} 
			ReportRowData rowData = new ReportRowData(reportCategory, debitAmount, creditAmount);
			adjustedBalance.put(reportCategory, rowData);
		}
	}
	
	private static Map<ReportCategory, ReportRowData> emptyCategories(){ 
		
		Map<ReportCategory, ReportRowData> map = new HashMap<ReportCategory, ReportRowData>();
		for(ReportCategory reportCategory : ReportCategory.values()) {
			ReportRowData rowData = new ReportRowData();
			rowData.setCategory(reportCategory);
			
			map.put(reportCategory, rowData );
		}
		return map ;
	}
	
	private void setThisYearCashflow(Map<ReportCategory, ReportRowData> thisYearCashflow) {
		this.thisYearCashflow = thisYearCashflow;
	}

	@Override
	public CustomWorkbook buildReport() {

		Filter filter = reportData.getFilter();
		String time = getDateTime();
		String sheetName = "BALANCE-" + filter.getYear();

		setFormerBalance(reportData.getDailyReportSummary());
		setThisYearCashflow(reportData.getMonthyReportContent().get(filter.getYear()));

		String reportName = /* webConfigService.getReportPath() + "/" + */ sheetName + "_" + time + ".xlsx";
		CustomWorkbook xwb = new CustomWorkbook();
		xsheet = xwb.createSheet(sheetName);
		xwb.setFileName(reportName);
		
		writeBalanceReport(reportData);

//		File file = MyFileUtil.getFile(xwb, reportName);
		 
		return xwb;
	}

	private void writeBalanceReport(ReportData reportData) {

		int rowNum = 1;
		int colOffset = 1;
		writeHorizontalColumNames(rowNum, colOffset);
		rowNum += 2;
		
		writeReportCategoriesLabel(rowNum, colOffset); 
		writeContents(rowNum, colOffset);
		autoSizeColumns(rowNum, colOffset);
		

	}

	private void writeContents(int rowNum, int colOffset) {
		 
		writeFormerBalance(rowNum, colOffset);
		wirteBalanceChanging(rowNum, colOffset); 
		writeRemainingBalance(rowNum, colOffset);
		writeAdjustingBalance(rowNum, colOffset);
		writeAdjustedBalance(rowNum, colOffset);
		writeLossProfitBalance(rowNum, colOffset);
		writeFinalBalance(rowNum, colOffset);
	}  
 
	private void writeFinalBalance(int rowNum, int colOffset) {
		buildFinalBalanceFromAdjustedBalance();
		long[] totalDebitAndCredit = writeBalanceColumn(rowNum, colOffset, FINAL_BALANCE_INDEX, finalBalance);
		long totalDebit = totalDebitAndCredit[0];
		long totalCredit = totalDebitAndCredit[1];
		writeBusinessBalanceCell(rowNum, colOffset, totalDebit, totalCredit);
	}
	 
	private void writeLossProfitBalance(int rowNum, int colOffset) {
		buildLossProfitBalanceFromAdjustedBalance();
		long[] totalDebitAndCredit = writeBalanceColumn(rowNum, colOffset, LOSS_PROFIT_BALANCE_INDEX, lossProfitBalance);
		long totalDebit = totalDebitAndCredit[0];
		long totalCredit = totalDebitAndCredit[1];
		writeBusinessProfitCell(rowNum, colOffset,totalDebit, totalCredit);
	}
	
	private void writeBusinessBalanceCell(int rowNum, int colOffset, long totalDebit, long totalCredit) {
		log.info("writeBusinessBalanceCell. totalDebit: {}, totalCredit: {}",totalDebit,totalCredit);
		
		int workingRow = rowNum + ReportCategory.values().length + 1;
		int offsetIndex = colOffset + 2 + 2 * FINAL_BALANCE_INDEX; 
		createRow(xsheet, workingRow, offsetIndex , "", curr(businessProfit));
		workingRow++; 
		createRow(xsheet, workingRow, offsetIndex, curr(totalDebit), curr(totalCredit+businessProfit));
		
	}

	private void writeBusinessProfitCell(int rowNum, int colOffset, long totalDebit, long totalCredit) {
		log.info("writeBusinessProfitCell. totalDebit: {}, totalCredit: {}",totalDebit,totalCredit);
		
		int workingRow = rowNum + ReportCategory.values().length + 1;
		int offsetIndex = colOffset + 2 + 2 * LOSS_PROFIT_BALANCE_INDEX;
		long businessProfit = totalCredit > totalDebit ? totalCredit-totalDebit : 0L;
		createRow(xsheet, workingRow, offsetIndex - 1, "SHU "+reportData.getFilter().getYear(), curr(businessProfit), curr(0));
		workingRow++;
		//TODO: total credit calculation
		createRow(xsheet, workingRow, offsetIndex -1,"", curr(totalDebit+businessProfit), curr(totalCredit));
		
		this.businessProfit = businessProfit;
		
	}

	private void writeAdjustedBalance(int rowNum, int colOffset) {
		buildAdjustedBalance();
		writeBalanceColumn(rowNum, colOffset, ADJUSTED_BALANCE_INDEX, adjustedBalance);
	} 
	
	private void writeAdjustingBalance(int rowNum, int colOffset) {
		// TODO: Calculate adjusted balance content
		writeBalanceColumn(rowNum, colOffset, ADJUSTING_BALANCE_INDEX, adjustingBalance); 
	}

	private void writeRemainingBalance(int rowNum, int colOffset) {
		buildRemainingBalance();
		writeBalanceColumn(rowNum, colOffset, REMAINING_BALANCE_INDEX, remainingBalance); 
		
	}
	private void wirteBalanceChanging(int rowNum, int colOffset) {
		writeBalanceColumn(rowNum, colOffset, CHANGING_BALANCE_INDEX, thisYearCashflow); 
	}
	private void writeFormerBalance(int rowNum, int colOffset) {
		writeBalanceColumn(rowNum, colOffset, FORMER_BALANCE_INDEX, formerBalance);
	}
	
	/**
	 * 
	 * @param rowNum
	 * @param colOffset
	 * @param contentSequence starts at 0
	 * @param rowDatas
	 * @return array containing totalDebit & totalCredit
	 */
	private long[] writeBalanceColumn(int rowNum, int colOffset, int contentSequence, Map<ReportCategory, ReportRowData> rowDatas) {
		 
		long totalCredit = 0l;
		long totalDebit = 0l;
		int extraOffset = 2 * contentSequence;
		int offset = colOffset + 2 + extraOffset;
		for (ReportCategory reportCategory : ReportCategory.values()) {
			
			ReportRowData rowData = rowDatas.get(reportCategory);
			
			long creditAmount = rowData.getCreditAmount();
			long debitAmount = rowData.getDebitAmount();
			
			totalCredit+=creditAmount;
			totalDebit+=debitAmount;
			
			createRow(xsheet, rowNum, offset, curr(debitAmount) , curr(creditAmount));
			rowNum++;
		}
		
		createRow(xsheet, rowNum, offset, curr(totalDebit), curr(totalCredit));
		return new long[]{totalDebit, totalCredit};
	} 

	private void writeReportCategoriesLabel(int rowNum, int colOffset) {
		ReportCategory[] reportCategories = ReportCategory.values();
		for (int i = 0; i < reportCategories.length; i++) {
			createRow(xsheet, rowNum, colOffset, reportCategories[i].code, reportCategories[i].name);
			rowNum++;
		}
		createRow(xsheet, rowNum, colOffset, "", "Jumlah");
	}

	private void autoSizeColumns(int rowNum, int colOffset) {
		
		for(int i = 1; i<3+ReportCategory.values().length; i++) {
			ExcelReportUtil.autosizeColumn(xsheet.getRow(i), 20, BorderStyle.THIN, null);
		}
	}
	
	private void writeHorizontalColumNames(final int rowNum, int colOffset) {

		/**
		 * Column title
		 */
		Object[] colNames = { "No. Akun", "Nama Akun", "Neraca Awal", "", "Neraca Perubahan", "", "Neraca Sisa", "",
				"Penyesuaian", "", "NS Disesuaikan", "", "Rugi / Laba", "", "Neraca", ""

		};
		createRow(xsheet, rowNum, colOffset, colNames);
		int titleCount = 7;
		for (int i = 1; i <= titleCount; i++) {
			int firstCol = colOffset + (i * 2);
			CellRangeAddress cellRange = new CellRangeAddress(rowNum, rowNum, firstCol, firstCol + 1);
			addMergedRegion(xsheet, cellRange);
		}
		
		/**
		 * Column info
		 */
		int infoRowNum = rowNum + 1;

		Object[] colInfos = new Object[16]; 

		for (int i = 2; i < colInfos.length; i++) {
			colInfos[i] = i % 2 == 0 ? "D" : "K";
		}
		createRow(xsheet, infoRowNum, colOffset, colInfos); 

	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
