package com.fajar.shoppingmart.test;
//package com.fajar.stat;
//
//import java.util.Random;
//
//public class TestSQL {
//	
//static final String list = "radian~~" + 
//		"steradian~~" + 
//		"hertz~~" + 
//		"Newton~~" + 
//		"pascal~~" + 
//		"joule~~" + 
//		"watt~~" + 
//		"coulomb~~" + 
//		"volt~~" + 
//		"farad~~" + 
//		"ohm~~" + 
//		"siemens~~" + 
//		"weber~~" + 
//		"tesla~~" + 
//		"Henry~~" + 
//		"degree Celcius~~" + 
//		"lumen~~" + 
//		"lux~~" + 
//		"bequerel~~" + 
//		"gray~~" + 
//		"sievert~~" + 
//		"katal~~";
//
//		public static void mainUnit (String[] xx) {
//			final String sql ="INSERT INTO goodshop.unit" + 
//					"(  created_date, deleted, modified_date, name, description) " + 
//					"VALUES(  '2019-10-13 08:22:33.000', 0, NULL, '$NAME', NULL); " + 
//					"";
//			String[] liStrings = list.split("~~");
//			for (String string : liStrings) {
//				System.out.println(sql.replace("$NAME", string));
//			}
//		}
//		
//		static final String productList = "Chai~~" + 
//				"Chang~~" + 
//				"Aniseed Syrup~~" + 
//				"Chef Anton s Cajun Seasoning~~" + 
//				"Chef Anton s Gumbo Mix~~" + 
//				"Grandma s Boysenberry Spread~~" + 
//				"Uncle Bob s Organic Dried Pears~~" + 
//				"Northwoods Cranberry Sauce~~" + 
//				"Mishi Kobe Niku~~" + 
//				"Ikura~~" + 
//				"Queso Cabrales~~" + 
//				"Queso Manchego La Pastora~~" + 
//				"Konbu~~" + 
//				"Tofu~~" + 
//				"Genen Shouyu~~" + 
//				"Pavlova~~" + 
//				"Alice Mutton~~" + 
//				"Carnarvon Tigers~~" + 
//				"Teatime Chocolate Biscuits~~" + 
//				"Sir Rodney s Marmalade~~" + 
//				"Sir Rodney s Scones~~" + 
//				"Gustaf s Knäckebröd~~" + 
//				"Tunnbröd~~" + 
//				"Guaraná Fantástica~~" + 
//				"NuNuCa Nuß-Nougat-Creme~~" + 
//				"Gumbär Gummibärchen~~" + 
//				"Schoggi Schokolade~~" + 
//				"Rössle Sauerkraut~~" + 
//				"Thüringer Rostbratwurst~~" + 
//				"Nord-Ost Matjeshering~~" + 
//				"Gorgonzola Telino~~" + 
//				"Mascarpone Fabioli~~" + 
//				"Geitost~~" + 
//				"Sasquatch Ale~~" + 
//				"Steeleye Stout~~" + 
//				"Inlagd Sill~~" + 
//				"Gravad lax~~" + 
//				"Côte de Blaye~~" + 
//				"Chartreuse verte~~" + 
//				"Boston Crab Meat~~" + 
//				"Jack s New England Clam Chowder~~" + 
//				"Singaporean Hokkien Fried Mee~~" + 
//				"Ipoh Coffee~~" + 
//				"Gula Malacca~~" + 
//				"Rogede sild~~" + 
//				"Spegesild~~" + 
//				"Zaanse koeken~~" + 
//				"Chocolade~~" + 
//				"Maxilaku~~" + 
//				"Valkoinen suklaa~~" + 
//				"Manjimup Dried Apples~~" + 
//				"Filo Mix~~" + 
//				"Perth Pasties~~" + 
//				"Tourtière~~" + 
//				"Pâté chinois~~" + 
//				"Gnocchi di nonna Alice~~" + 
//				"Ravioli Angelo~~" + 
//				"Escargots de Bourgogne~~" + 
//				"Raclette Courdavault~~" + 
//				"Camembert Pierrot~~" + 
//				"Sirop d érable~~" + 
//				"Tarte au sucre~~" + 
//				"Vegie-spread~~" + 
//				"Wimmers gute Semmelknödel~~" + 
//				"Louisiana Fiery Hot Pepper Sauce~~" + 
//				"Louisiana Hot Spiced Okra~~" + 
//				"Laughing Lumberjack Lager~~" + 
//				"Scottish Longbreads~~" + 
//				"Gudbrandsdalsost~~" + 
//				"Outback Lager~~" + 
//				"Flotemysost~~" + 
//				"Mozzarella di Giovanni~~" + 
//				"Röd Kaviar~~" + 
//				"Longlife Tofu~~" + 
//				"Rhönbräu Klosterbier~~" + 
//				"Lakkalikööri~~" + 
//				"Original Frankfurter grüne Soße";
//		
//		public static void mainProduct(String[] xxx) {
//			String[] products = productList.split("~~");
//			final String sql ="INSERT INTO goodshop.product " + 
//					"( created_date, deleted, modified_date, description, name, `type`, unit_id, price) " + 
//					"VALUES( '2019-10-13 10:18:08.000', 0, NULL, '$NAME', 'PRODUCT', 'sdsdsdsds', $UNIT, $PRICE); " + 
//					"";
//			Random rand = new Random();
//			for (String string : products) {
//				Integer unitId = rand.nextInt(28)+1;
//				Integer price = rand.nextInt(200000)+3000;
//				System.out.println(sql.replace("$NAME", string).replace("$UNIT", unitId.toString()).replace("$PRICE", price.toString()));
//			}
//		}
//		
//		static final String companies = "Chevron~~" + 
//				"AFLAC~~" + 
//				"Applied Industrial Technologies~~" + 
//				"Charter Communications~~" + 
//				"Coca-Cola~~" + 
//				"Dow Chemical~~" + 
//				"Foot Locker~~" + 
//				"General Dynamics~~" + 
//				"Humana~~" + 
//				"Imation~~" + 
//				"Kimberly-Clark~~" + 
//				"3M~~" + 
//				"Public Storage~~" + 
//				"Qualcomm~~" + 
//				"Sealy~~" + 
//				"Steelcase~~" + 
//				"Textron~~" + 
//				"Men's Wearhouse~~" + 
//				"Toll Brothers~~" + 
//				"Tractor Supply~~" + 
//				"USG~~" + 
//				"Valspar~~" + 
//				"Waste Management~~" + 
//				"Williams-Sonoma~~" + 
//				"Yahoo~~" + 
//				"Union Pacific~~" + 
//				"Abbott Laboratories~~" + 
//				"Advance Auto Parts~~" + 
//				"American Express~~" + 
//				"Alaska Air Group~~" + 
//				"Allstate~~" + 
//				"Anadarko Petroleum~~" + 
//				"Anheuser-Busch~~" + 
//				"Assurant~~" + 
//				"AT&T~~" + 
//				"Boeing~~" + 
//				"Capital One Financial~~" + 
//				"CBS~~" + 
//				"Citigroup~~" + 
//				"E*Trade Financial~~" + 
//				"El Paso~~" + 
//				"Goodrich~~" + 
//				"Hanesbrands~~" + 
//				"International Paper~~" + 
//				"Eastman Kodak~~" + 
//				"Kraft Foods~~" + 
//				"LSI~~" + 
//				"Macy's~~" + 
//				"Brunswick~~" + 
//				"Meredith~~" + 
//				"Manpower~~" + 
//				"NCR~~" + 
//				"Oracle~~" + 
//				"Pepsi Bottling~~" + 
//				"Pfizer~~" + 
//				"Penn National Gaming~~" + 
//				"Prudential Financial~~" + 
//				"RadioShack~~" + 
//				"Regal Entertainment Group~~" + 
//				"SanDisk~~" + 
//				"Charles Schwab~~" + 
//				"Scotts Miracle-Gro~~" + 
//				"Sherwin-Williams~~" + 
//				"SPX~~" + 
//				"Starwood Hotels & Resorts~~" + 
//				"Target~~" + 
//				"Texas Instruments~~" + 
//				"Tiffany~~" + 
//				"United Technologies~~" + 
//				"Verizon Communications~~" + 
//				"Viacom~~" + 
//				"A.G. Edwards~~" + 
//				"Windstream~~" + 
//				"Southwest Airlines~~" + 
//				"Xerox~~" + 
//				"Ameriprise Financial~~" + 
//				"Delphi~~" + 
//				"Supervalu~~" + 
//				"DuPont~~" + 
//				"Google~~" + 
//				"Belk~~" + 
//				"CSX~~" + 
//				"Dean Foods~~" + 
//				"EMC~~" + 
//				"Intuit~~" + 
//				"Merck~~" + 
//				"New York Life Insurance~~" + 
//				"Saks~~" + 
//				"TJX~~" + 
//				"Raytheon~~" + 
//				"Whirlpool~~" + 
//				"Microsoft~~" + 
//				"Big Lots~~" + 
//				"GameStop~~" + 
//				"Live Nation~~" + 
//				"OfficeMax~~" + 
//				"Smithfield Foods~~" + 
//				"Procter & Gamble~~" + 
//				"Principal Financial~~" + 
//				"Graphic Packaging~~" + 
//				"Abercrombie & Fitch" + 
//				"";
//		
//		static final String companiesPhone = "925-842-1000~~" + 
//				"706-323-3431~~" + 
//				"216-426-4000~~" + 
//				"314-965-0555~~" + 
//				"404-676-2121~~" + 
//				"989-636-1000~~" + 
//				"212-720-3700~~" + 
//				"703-876-3000~~" + 
//				"502-580-1000~~" + 
//				"651-704-4000~~" + 
//				"972-281-1200~~" + 
//				"651-733-1110~~" + 
//				"818-244-8080~~" + 
//				"858-587-1121~~" + 
//				"336-861-3500~~" + 
//				"616-247-2710~~" + 
//				"401-421-2800~~" + 
//				"281-776-7200~~" + 
//				"215-938-8000~~" + 
//				"615-440-4000~~" + 
//				"312-436-4000~~" + 
//				"612-332-7371~~" + 
//				"713-512-6200~~" + 
//				"415-421-7900~~" + 
//				"408-349-3300~~" + 
//				"402-544-5000~~" + 
//				"847-937-6100~~" + 
//				"540-362-4911~~" + 
//				"212-640-2000~~" + 
//				"206-392-5040~~" + 
//				"847-402-5000~~" + 
//				"832-636-1000~~" + 
//				"314-577-2000~~" + 
//				"212-859-7000~~" + 
//				"210-821-4105~~" + 
//				"312-544-2000~~" + 
//				"703-720-1000~~" + 
//				"212-975-4321~~" + 
//				"212-559-1000~~" + 
//				"646-521-4300~~" + 
//				"713-420-2600~~" + 
//				"704-423-7000~~" + 
//				"336-519-4400~~" + 
//				"901-419-7000~~" + 
//				"585-724-4000~~" + 
//				"847-646-2000~~" + 
//				"408-433-8000~~" + 
//				"513-579-7000~~" + 
//				"847-735-4700~~" + 
//				"515-284-3000~~" + 
//				"414-961-1000~~" + 
//				"937-445-5000~~" + 
//				"650-506-7000~~" + 
//				"914-767-6000~~" + 
//				"212-573-2323~~" + 
//				"610-373-2400~~" + 
//				"973-802-6000~~" + 
//				"817-415-3011~~" + 
//				"865-922-1123~~" + 
//				"408-801-1000~~" + 
//				"415-636-7000~~" + 
//				"937-644-0011~~" + 
//				"216-566-2000~~" + 
//				"704-752-4400~~" + 
//				"914-640-8100~~" + 
//				"612-304-6073~~" + 
//				"972-995-3773~~" + 
//				"212-755-8000~~" + 
//				"860-728-7000~~" + 
//				"212-395-1000~~" + 
//				"212-258-6000~~" + 
//				"314-955-3000~~" + 
//				"501-748-7000~~" + 
//				"214-792-4000~~" + 
//				"203-968-3000~~" + 
//				"612-671-3131~~" + 
//				"248-813-2000~~" + 
//				"952-828-4000~~" + 
//				"302-774-1000~~" + 
//				"650-253-0000~~" + 
//				"704-357-1000~~" + 
//				"904-359-3200~~" + 
//				"214-303-3400~~" + 
//				"508-435-1000~~" + 
//				"650-944-6000~~" + 
//				"908-423-1000~~" + 
//				"212-576-7000~~" + 
//				"212-940-5305~~" + 
//				"508-390-1000~~" + 
//				"781-522-3000~~" + 
//				"269-923-5000~~" + 
//				"425-882-8080~~" + 
//				"614-278-6800~~" + 
//				"817-424-2000~~" + 
//				"310-867-7000~~" + 
//				"630-438-7800~~" + 
//				"757-365-3000~~" + 
//				"513-983-1100~~" + 
//				"515-247-5111~~" + 
//				"770-644-3000~~" + 
//				"614-283-6500 " + 
//				"";
//		
//		static final  String companiesAddress = "6001 Bollinger Canyon Rd.	San Ramon	94583~~" + 
//				"1932 Wynnton Rd.	Columbus	31999~~" + 
//				"1 Applied Plaza	Cleveland	44115~~" + 
//				"12405 Powerscourt Dr.	Des Peres	63131~~" + 
//				"1 Coca-Cola Plaza	Atlanta	30313~~" + 
//				"2030 Dow Center	Midland	48674~~" + 
//				"112 W. 34th St.	New York	10120~~" + 
//				"2941 Fairview Park Dr.	Mosby	22042~~" + 
//				"500 W. Main St.	Louisville	40202~~" + 
//				"1 Imation Way	Oakdale	55128~~" + 
//				"351 Phelps Dr.	Irving	75038~~" + 
//				"3M Center	St. Paul	55144~~" + 
//				"701 Western Ave.	Glendale	91201~~" + 
//				 "5775 Morehouse Dr.	San Diego	92121~~" + 
//				"1 Office Pkwy.	Trinity	27370~~" + 
//				"901 44th St. S.E.	Kentwood	49508~~" + 
//				"40 Westminster St.	Providence	2903~~" + 
//				"6380 Rogerdale Rd.	Houston	77072~~" + 
//				"250 Gibraltar Rd.	Horsham	19044~~" + 
//				"200 Powell Place	Brentwood	37027~~" + 
//				"550 W. Adams St.	Chicago	60661~~" + 
//				"1101 Third St. S.	Minneapolis	55415~~" + 
//				"1001 Fannin St.	Houston	77002~~" + 
//				"3250 Van Ness Ave.	San Francisco	94109~~" + 
//				"701 First Ave.	Sunnyvale	94089~~" + 
//				"1400 Douglas St.	Omaha	68179~~" + 
//				"100 Abbott Park Rd.	Abbott Park	60064~~" + 
//				"5008 Airport Rd.	Roanoke	24012~~" + 
//				"200 Vesey St.	New York	10285~~" + 
//				"19300 International Blvd.	Tukwila	98188~~" + 
//				"2775 Sanders Rd.	Northbrook	60062~~" + 
//				"1201 Lake Robbins Dr.	The Woodlands	77380~~" + 
//				"1 Busch Place	Saint Louis	63118~~" + 
//				"1 Chase Manhattan Plaza	New York	10005~~" + 
//				"175 E. Houston St.	San Antonio	78205~~" + 
//				"100 N. Riverside Plaza	Chicago	60606~~" + 
//				"1680 Capital One Dr.	West Mclean	22102~~" + 
//				"51 W. 52nd St.	New York	10019~~" + 
//				"399 Park Ave.	New York	10043~~" + 
//				"135 East 57th St.	New York	10022~~" + 
//				"1001 Louisiana St.	Houston	77002~~" + 
//				"2730 W. Tyvola Rd.	Charlotte	28217~~" + 
//				"1000 E. Hanes Mill Rd.	Winston Salem	27105~~" + 
//				"6400 Poplar Ave.	Memphis	38197~~" + 
//				"343 State St.	Rochester	14650~~" + 
//				"3 Lakes Dr.	Northfield	60093~~" + 
//				"1621 Barber Ln.	Milpitas	95035~~" + 
//				"7 W. Seventh St.	Cincinnati	45202~~" + 
//				"1 N. Field Court	Lake Forest	60045~~" + 
//				"1716 Locust St.	Des Moines	50309~~" + 
//				"100 Manpower Place	Milwaukee	53212~~" + 
//				"1700 S. Patterson Blvd.	Dayton	45479~~" + 
//				"500 Oracle Pkwy.	Redwood City	94065~~" + 
//				"1 Pepsi Way	Somers	10589~~" + 
//				"235 E. 42nd St.	New York	10017~~" + 
//				"825 Berkshire Blvd.	Wyomissing	19610~~" + 
//				"751 Broad St.	Newark	7102~~" + 
//				"300 RadioShack Circle	Fort Worth	76102~~" + 
//				"7132 Regal Ln.	Knoxville	37918~~" + 
//				"601 McCarthy Blvd.	Milpitas	95035~~" + 
//				"120 Kearny St.	San Francisco	94108~~" + 
//				"14111 Scottslawn Road	Marysville	43041~~" + 
//				"101 Prospect Ave. N.W.	Cleveland	44115~~" + 
//				"13515 Ballantyne Corporate Place	Charlotte	28277~~" + 
//				"1111 Westchester Ave.	East White Plain	10604~~" + 
//				"1000 Nicollet Mall	Minneapolis	55403~~" + 
//				"12500 TI Blvd.	Dallas	75243~~" + 
//				"727 Fifth Ave.	New York	10022~~" + 
//				"1 Financial Plaza	Hartford	6103~~" + 
//				"140 West St.	New York	10007~~" + 
//				"1515 Broadway	New York	10036~~" + 
//				"1 N. Jefferson Ave.	Saint Louis	63103~~" + 
//				"4001 Rodney Parham Rd.	Little Rock	72212~~" + 
//				"2702 Love Field Dr.	Dallas	75235~~" + 
//				"45 Glover Ave.	Norwalk	6850~~" + 
//				"55 Ameriprise Financial Center	Minneapolis	55474~~" + 
//				"5725 Delphi Dr.	Troy	48098~~" + 
//				"11840 Valley View Rd.	Eden Prairie	55344~~" + 
//				"1007 Market St.	Wilmington	19898~~" + 
//				"1600 Amphitheatre Pkwy.	Mountain View	94043~~" + 
//				"2801 W. Tyvola Rd.	Charlotte	28217~~" + 
//				"500 Water St.	Jacksonville	32202~~" + 
//				"2515 McKinney Ave.	Dallas	75201~~" + 
//				"176 South St.	Hopkinton	1748~~" + 
//				"2700 Coast Avenue	Mountain View	94043~~" + 
//				"1 Merck Dr.	Whitehouse Stati	8889~~" + 
//				"51 Madison Ave.	New York	10010~~" + 
//				"12 E. 49 St.	New York	10017~~" + 
//				"770 Cochituate Rd.	Framingham	1701~~" + 
//				"870 Winter St.	Waltham	2451~~" + 
//				"2000 North M-63	Benton Harbor	49022~~" + 
//				"1 Microsoft Way	Redmond	98052~~" + 
//				"300 Phillipi Rd.	Lincoln Village	43228~~" + 
//				"625 Westport Pkwy.	Grapevine	76051~~" + 
//				"9348 Civic Center Dr.	Beverly Hills	90210~~" + 
//				"263 Shuman Blvd.	Naperville	60563~~" + 
//				"200 Commerce St.	Smithfield	23430~~" + 
//				"1 Procter &amp; Gamble Plaza	Cincinnati	45202~~" + 
//				"711 High St.	Des Moines	50392~~" + 
//				"814 Livingston Court	Marietta	30067~~" + 
//				"6301 Fitch Path	New Albany	43054 " + 
//				"";
//		
//		public static void mainNewSupplier(String[] xxx) {
//			final String sql  ="INSERT INTO goodshop.supplier " + 
//					"(  created_date, deleted, modified_date, address, contact, name) " + 
//					"VALUES(  '2019-10-13 08:22:33.000', 0, NULL, '$ADDRESS', '$CONTACT', '$NAME'); " + 
//					"";
//			String[] companyList = companies.split("~~");
//			String[] companyPhones = companiesPhone.split("~~");
//			String[] companyAddresses = companiesAddress.split("~~");
//			 for (int i = 0; i < companyList.length; i++) {
//				String name = companyList[i];
//				String phone = companyPhones[i];
//				String address = companyAddresses[i];
//				String insert = sql.replace("$ADDRESS", address).replace("$CONTACT", phone).replace("$NAME", name);
//				System.out.println(insert);
//			}
//		}
//		
//		static final String companiesWeb = "http://www.chevron.com~~" + 
//				"http://www.aflac.com~~" + 
//				"http://www.applied.com~~" + 
//				"http://www.charter.com~~" + 
//				"http://www.thecoca-colacompany.com~~" + 
//				"http://www.dow.com~~" + 
//				"http://www.footlocker-inc.com~~" + 
//				"http://www.generaldynamics.com~~" + 
//				"http://www.humana.com~~" + 
//				"http://www.imation.com~~" + 
//				"http://www.kimberly-clark.com~~" + 
//				"http://www.3m.com~~" + 
//				"http://www.publicstorage.com~~" + 
//				"http://www.qualcomm.com~~" + 
//				"http://www.sealy.com~~" + 
//				"http://www.steelcase.com~~" + 
//				"http://www.textron.com~~" + 
//				"http://www.tmw.com~~" + 
//				"http://www.tollbrothers.com~~" + 
//				"http://www.tractorsupply.com~~" + 
//				"http://www.usg.com~~" + 
//				"http://www.valspar.com~~" + 
//				"http://www.wm.com~~" + 
//				"http://www.williams-sonomainc.com~~" + 
//				"http://www.yahoo.com~~" + 
//				"http://www.up.com~~" + 
//				"http://www.abbott.com~~" + 
//				"http://www.advanceautoparts.com~~" + 
//				"http://www.americanexpress.com~~" + 
//				"http://www.alaskaair.com~~" + 
//				"http://www.allstate.com~~" + 
//				"http://www.anadarko.com~~" + 
//				"http://www.anheuser-busch.com~~" + 
//				"http://www.assurant.com~~" + 
//				"http://www.att.com~~" + 
//				"http://www.boeing.com~~" + 
//				"http://www.capitalone.com~~" + 
//				"http://www.cbscorporation.com~~" + 
//				"http://www.citigroup.com~~" + 
//				"http://www.etrade.com~~" + 
//				"http://www.elpaso.com~~" + 
//				"http://www.goodrich.com~~" + 
//				"http://www.hanesbrands.com~~" + 
//				"http://www.internationalpaper.com~~" + 
//				"http://www.kodak.com~~" + 
//				"http://www.kraft.com~~" + 
//				"http://www.lsi.com~~" + 
//				"http://www.macysinc.com~~" + 
//				"http://www.brunswick.com~~" + 
//				"http://www.meredith.com~~" + 
//				"http://www.manpower.com~~" + 
//				"http://www.ncr.com~~" + 
//				"http://www.oracle.com~~" + 
//				"http://www.pbg.com~~" + 
//				"http://www.pfizer.com~~" + 
//				"http://www.pngaming.com~~" + 
//				"http://www.prudential.com~~" + 
//				"http://www.radioshackcorporation.com~~" + 
//				"http://www.regalentertainmentgroup.com~~" + 
//				"http://www.sandisk.com~~" + 
//				"http://www.aboutschwab.com~~" + 
//				"http://www.scotts.com~~" + 
//				"http://www.sherwin.com~~" + 
//				"http://www.spx.com~~" + 
//				"http://www.starwoodhotels.com~~" + 
//				"http://www.target.com~~" + 
//				"http://www.ti.com~~" + 
//				"http://www.tiffany.com~~" + 
//				"http://www.utc.com~~" + 
//				"http://www.verizon.com~~" + 
//				"http://www.viacom.com~~" + 
//				"http://www.agedwards.com~~" + 
//				"http://www.windstream.com~~" + 
//				"http://www.southwest.com~~" + 
//				"http://www.xerox.com~~" + 
//				"http://www.ameriprise.com~~" + 
//				"http://www.delphi.com~~" + 
//				"http://www.supervalu.com~~" + 
//				"http://www.dupont.com~~" + 
//				"http://www.google.com~~" + 
//				"http://www.belk.com~~" + 
//				"http://www.csx.com~~" + 
//				"http://www.deanfoods.com~~" + 
//				"http://www.emc.com~~" + 
//				"http://www.intuit.com~~" + 
//				"http://www.merck.com~~" + 
//				"http://www.newyorklife.com~~" + 
//				"http://www.saksincorporated.com~~" + 
//				"http://www.tjx.com~~" + 
//				"http://www.raytheon.com~~" + 
//				"http://www.whirlpoolcorp.com~~" + 
//				"http://www.microsoft.com~~" + 
//				"http://www.biglots.com~~" + 
//				"http://www.gamestop.com~~" + 
//				"http://www.livenation.com~~" + 
//				"http://www.officemax.com~~" + 
//				"http://www.smithfieldfoods.com~~" + 
//				"http://www.pg.com~~" + 
//				"http://www.principal.com~~" + 
//				"http://www.graphicpkg.com~~" + 
//				"http://www.abercrombie.com" + 
//				"";
//		public static void main(String[] dddd) {
//			String[] websites = companiesWeb.split("~~");
//			String sql = "UPDATE goodshop.supplier " + 
//					"SET  website='$WEBSITE' " + 
//					"WHERE id=$ID;";
//			for (int i = 0; i < websites.length; i++) {
//				String string = websites[i];
//				System.out.println(sql.replace("$WEBSITE", string).replace("$ID", i+2+""));
//			}
//		}
//
//}
