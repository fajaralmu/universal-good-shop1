//package com.fajar.test;
//
//import java.util.Random;
//
//public class TestingSQL {
//	static Random rand = new Random();
//	static final String sqlInsertTrx = "INSERT INTO goodshop.`transaction` " + 
//			"(id, created_date, deleted, modified_date, `type`, customer_id, user_id, supplier_id) " + 
//			"VALUES($ID, '2019-01-15 02:27:09.000', 0, NULL, 'IN', NULL, 1, $SUPPLIER_ID); " + 
//			"";
//	
//	static final String sqlInsertProductFlow = "INSERT INTO goodshop.product_flow " + 
//			"( created_date, deleted, modified_date, count, expiry_date, flow_ref_id, price, transaction_id, product_id) " + 
//			"VALUES( '2019-10-15 02:27:49.000', 0, NULL, $COUNT, '$EXP_YEAR-$EXP_MONTH-16 17:00:00.000', NULL, $PRICE, $TRX_ID, $PRODUCT_ID); " + 
//			"";
//	
//	public static void mainXX(String[] ss) {
//		
//		for(Integer i=7;i<=490;i++) {
//			Integer supplierID = rand.nextInt(95)+1;
//			String sqlTrx = sqlInsertTrx.replace("$ID", i.toString()).replace("$SUPPLIER_ID", supplierID.toString());
//			System.out.println(sqlTrx);
//			//productFLow
//			Integer flowCount = rand.nextInt(29)+1;
//			for(Integer j=1;j<=flowCount;j++) {
//				Integer expMonth = rand.nextInt(11)+1;
//				Integer expYear = rand.nextInt(5)+2020;
//				Integer count = rand.nextInt(499)+1;
//				String expMonthStr = expMonth<10?"0"+expMonth:expMonth.toString();
//				Integer price = rand.nextInt(4000000)+1000;
//				Integer productId = rand.nextInt(78)+1;
//				String sqlProductFLow = sqlInsertProductFlow.replace("$TRX_ID", i.toString()).replace("$EXP_MONTH", expMonthStr).replace("$PRICE", price.toString())
//						.replace("$PRODUCT_ID", productId.toString()).replace("$COUNT", count.toString())
//						.replace("$EXP_YEAR", expYear.toString());
//				System.out.println(sqlProductFLow);
//			}
//		}
//		
//	}
//	
//	public static void mainDD(String[] xxx) {
//		for(int i=1;i<=82;i++) {
//			Random rand = new Random();
//			Integer catId = rand.nextInt(13)+1;
//			String sql= "UPDATE goodshop.product " + 
//					"SET category_id="+catId+  
//					" WHERE id="+i+"; " ;
//			System.out.println(sql);
//		}
//	}
//
//}
