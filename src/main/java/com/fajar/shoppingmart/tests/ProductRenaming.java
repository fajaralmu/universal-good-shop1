package com.fajar.shoppingmart.tests;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.Unit;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.repository.RepositoryCustom;
import com.fajar.shoppingmart.repository.UnitRepository;
import com.fajar.shoppingmart.util.StringUtil;
import com.fajar.shoppingmart.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
public class ProductRenaming {
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RepositoryCustom customRepository;
	@Autowired
	private UnitRepository unitRepository;
	
	@PostConstruct
	public void init() {
		ThreadUtil.run(()->{   });
	}
	
	@Deprecated
	private void swapUnits() {
		List<Product> products = productRepository.findAll();
		List<Unit> units = unitRepository.findAll();
		Random r = new Random();
		for (int i = 0; i < products.size(); i++) {
			try {
				Product p = products.get(i);
				int randomUnit = r.nextInt(units.size()-1);
				p.setUnit(units.get(randomUnit));
				productRepository.save(p);
				customRepository.refresh();
				log.info("Modified unit: {} => {}", i, units.get(randomUnit).getName());
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@Deprecated
	private void renameUnits() {
		String[] names = unitNames();
		List<Unit> units = unitRepository.findAll();
		 
		for (int i = 0; i < units.size(); i++) {
			log.info("Modify Unit: {}", names[i]);
			Unit p = units.get(i);
			p.setName(names[i]); 
			p.setDescription(names[i]);
			unitRepository.save(p);
			customRepository.refresh();
		}
		
		if(names.length > units.size()) {
			 
			for (int i = units.size()-1; i < names.length; i++) {
				log.info("Add new Unit: {}", names[i]);
				Unit p = new Unit();
				p.setName(names[i]); 
				p.setDescription(names[i]);  
				unitRepository.save(p);
				customRepository.refresh();
			}
		}
		
	}
	
	@Deprecated
	private void rename() {
		String[] names = names();
		List<Product> products = productRepository.findAll();
		Product sample = products.get(0);
		for (int i = 0; i < products.size(); i++) {
			log.info("Modify Product: {}", names[i]);
			Product p = products.get(i);
			p.setName(names[i]);
			p.setCode(randomProductCode());
			productRepository.save(p);
		}
		
		if(names.length > products.size()) {
			 
			for (int i = products.size()-1; i < names.length; i++) {
				log.info("Add new product: {}", names[i]);
				Product p = new Product();
				p.setName(names[i]);
				p.setCode(randomProductCode());
				p.setDescription(names[i]);
				p.setPrice(Long.valueOf(StringUtil.generateRandomNumber(2)+100));
				p.setCategory(sample.getCategory() );
				p.setUnit(sample.getUnit());
				productRepository.save(p);
			}
		}
		customRepository.refresh();
	}
	
	private String randomProductCode() {
		 
		return StringUtil.generateRandomNumber(7);
	}

	static String[] names() {
		return  productList.split("\r\n");
	}
	static String[] unitNames() {
		return  unitList.split("\r\n");
	}
	
	public static void main(String[] args) {
		String[] names = names();
		for (String string : names) {
			System.out.println(">>"+string);
		}
	}

	static final String unitList = "\r\n" + 
			"1 gal\r\n" + 
			"1 gal jar\r\n" + 
			"1 Gallon\r\n" + 
			"1 LB\r\n" + 
			"1 pc / 2-2.5 lb\r\n" + 
			"1 pc/2-2.5 lb\r\n" + 
			"1.75-2.5 lb\r\n" + 
			"1/ 33 lb\r\n" + 
			"1/0.56 Ounce\r\n" + 
			"1/1 Gallon\r\n" + 
			"1/1 LB\r\n" + 
			"1/1.75 lb\r\n" + 
			"1/10 lb\r\n" + 
			"1/10 Ounce\r\n" + 
			"1/100 ct\r\n" + 
			"1/11 lb\r\n" + 
			"1/12 lb\r\n" + 
			"1/15 lb\r\n" + 
			"1/15.5 lb\r\n" + 
			"1/16 Ounce\r\n" + 
			"1/20 lb\r\n" + 
			"1/22 lb\r\n" + 
			"1/25 lb\r\n" + 
			"1/288 ct\r\n" + 
			"1/3 lb 3 oz\r\n" + 
			"1/3.5 lb\r\n" + 
			"1/32 Ounce\r\n" + 
			"1/35 lb\r\n" + 
			"1/4 lb\r\n" + 
			"1/40 lb\r\n" + 
			"1/48 ct\r\n" + 
			"1/5 lb\r\n" + 
			"1/5 Ltr\r\n" + 
			"1/50 ct\r\n" + 
			"1/55 lb\r\n" + 
			"1/55.1 lb\r\n" + 
			"1/60 ct\r\n" + 
			"1/64 Ounce\r\n" + 
			"1/8 lb Pail\r\n" + 
			"1/8 oz\r\n" + 
			"1/90 ct\r\n" + 
			"10 lb\r\n" + 
			"10 lb avg\r\n" + 
			"10/12 Count\r\n" + 
			"10/21 oz\r\n" + 
			"10/4 LB\r\n" + 
			"10/8 Oz.\r\n" + 
			"100 ct\r\n" + 
			"10LB\r\n" + 
			"11 LB\r\n" + 
			"1-12/8 Ounce\r\n" + 
			"12/ 1 lb\r\n" + 
			"12/1 lb\r\n" + 
			"12/1 Liters\r\n" + 
			"12/1 Quart\r\n" + 
			"12/1.4\r\n" + 
			"12/10 Ounce\r\n" + 
			"12/10 oz\r\n" + 
			"12/10oz\r\n" + 
			"12/11.2 Ounce\r\n" + 
			"12/12 Ounce\r\n" + 
			"12/12 oz\r\n" + 
			"12/12 pc\r\n" + 
			"12/12.95 Ounce\r\n" + 
			"12/13 Ounce\r\n" + 
			"12/13.5 Ounce\r\n" + 
			"12/13.66 Ounce\r\n" + 
			"12/14 oz\r\n" + 
			"12/15 Oz.\r\n" + 
			"12/16 Ounce\r\n" + 
			"12/16 Oz\r\n" + 
			"12/16oz\r\n" + 
			"12/17 Ounce\r\n" + 
			"12/18 Oz.\r\n" + 
			"12/2 oz\r\n" + 
			"12/22 Oz.\r\n" + 
			"12/24 Ounce\r\n" + 
			"12/24 oz\r\n" + 
			"12/28 Ounce\r\n" + 
			"12/3 oz\r\n" + 
			"12/30 oz\r\n" + 
			"12/32 Ounce\r\n" + 
			"12/32 oz\r\n" + 
			"12/32 Oz.\r\n" + 
			"12/32oz\r\n" + 
			"12/34 Ounce\r\n" + 
			"12/4 oz\r\n" + 
			"12/4 Oz.\r\n" + 
			"12/4ct\r\n" + 
			"12/5oz\r\n" + 
			"12/6oz\r\n" + 
			"12/7 Ounce\r\n" + 
			"12/7 oz\r\n" + 
			"12/7.5 Ounce\r\n" + 
			"12/70 Ct.\r\n" + 
			"12/8 Ounce\r\n" + 
			"12/8 oz\r\n" + 
			"12/8 Oz.\r\n" + 
			"12/8ct\r\n" + 
			"12/Quarts\r\n" + 
			"12-1.4 oz\r\n" + 
			"12-2.6 Ounce\r\n" + 
			"12pc/3 oz\r\n" + 
			"15 Dozen\r\n" + 
			"15 DZ/CS\r\n" + 
			"15 LB\r\n" + 
			"15/1 LB\r\n" + 
			"15/12 Ct.\r\n" + 
			"16/1 Quart\r\n" + 
			"16oz\r\n" + 
			"18/1 Lb\r\n" + 
			"18/3.2 oz\r\n" + 
			"18/3.4 oz\r\n" + 
			"1lb\r\n" + 
			"2 LB\r\n" + 
			"2 pc/2-2.5 lb\r\n" + 
			"2/ 3.5 lb\r\n" + 
			"2/ 6.1 LB\r\n" + 
			"2/1.32 Gal\r\n" + 
			"2/10 lb\r\n" + 
			"2/2.75 lbs\r\n" + 
			"2/3.25 lb\r\n" + 
			"2/3.5 Lb\r\n" + 
			"2/3.5 Lb / 14 S\r\n" + 
			"2/3.9 LB\r\n" + 
			"2/3.97 LB\r\n" + 
			"2/4 Lb\r\n" + 
			"2/4 Lb / 14 Sl\r\n" + 
			"2/4 lb / 14 Sl.\r\n" + 
			"2/4 lbs\r\n" + 
			"2/4.25 lb\r\n" + 
			"2/4.4 LB\r\n" + 
			"2/4.41 LB\r\n" + 
			"2/5 lb\r\n" + 
			"2/5.5 lb\r\n" + 
			"2/5-6 lb Avg\r\n" + 
			"2/6 lb\r\n" + 
			"2/6.1 LB\r\n" + 
			"2/80 ct\r\n" + 
			"20 lb\r\n" + 
			"20/1 lb\r\n" + 
			"24 CT.\r\n" + 
			"24/12 Oz.\r\n" + 
			"24/14 Oz.\r\n" + 
			"24/17 Ounce\r\n" + 
			"25 Lb\r\n" + 
			"25#\r\n" + 
			"25Lb\r\n" + 
			"3/ 1 L\r\n" + 
			"3/20 lb avg\r\n" + 
			"3/48oz\r\n" + 
			"3/5 LB\r\n" + 
			"3/5 Ltr\r\n" + 
			"30 LB\r\n" + 
			"30 lb Carton\r\n" + 
			"30/1 lb\r\n" + 
			"35 LB\r\n" + 
			"36/1 lb\r\n" + 
			"4 ct\r\n" + 
			"4 gal tub\r\n" + 
			"4 LB\r\n" + 
			"4/1 gal\r\n" + 
			"4/1 Gallon\r\n" + 
			"4/1.5 LB\r\n" + 
			"4/11 LB\r\n" + 
			"4/16 Ounce\r\n" + 
			"4/160 oz\r\n" + 
			"4/22 Ounce\r\n" + 
			"4/4 lb\r\n" + 
			"4/5 lb\r\n" + 
			"4/64 oz\r\n" + 
			"4/8 lb\r\n" + 
			"40 ct\r\n" + 
			"40 LB\r\n" + 
			"400 ct/case\r\n" + 
			"48/3.5 oz\r\n" + 
			"5 Gal\r\n" + 
			"5 Lb\r\n" + 
			"5 lb tub\r\n" + 
			"5/2.2 lb\r\n" + 
			"50 ct\r\n" + 
			"50 LB\r\n" + 
			"50/ 1 oz\r\n" + 
			"500/4.5 Grams\r\n" + 
			"6/1 Half Gal\r\n" + 
			"6/1 lb\r\n" + 
			"6/1 Oz.\r\n" + 
			"6/1.1 lb\r\n" + 
			"6/1.12 lb\r\n" + 
			"6/1.69 Ounce\r\n" + 
			"6/1.76 Ounce\r\n" + 
			"6/10 Ounce\r\n" + 
			"6/10.5 Ounce\r\n" + 
			"6/10.5 Oz.\r\n" + 
			"6/104 Ounce\r\n" + 
			"6/106 oz\r\n" + 
			"6/112 Ounce\r\n" + 
			"6/12 Ounce\r\n" + 
			"6/12oz\r\n" + 
			"6/14 Ounce\r\n" + 
			"6/15 Ounce\r\n" + 
			"6/15.2 Ounce\r\n" + 
			"6/15.2oz\r\n" + 
			"6/16 Ounce\r\n" + 
			"6/16 Oz.\r\n" + 
			"6/16.9 Ounce\r\n" + 
			"6/16oz\r\n" + 
			"6/18 Ounce\r\n" + 
			"6/18 Oz.\r\n" + 
			"6/2 LB\r\n" + 
			"6/2 Ounce\r\n" + 
			"6/2.3 Ounce\r\n" + 
			"6/2.6 Ounce\r\n" + 
			"6/23 Ounce\r\n" + 
			"6/24 oz\r\n" + 
			"6/24oz\r\n" + 
			"6/26.4 Ounce\r\n" + 
			"6/28 oz\r\n" + 
			"6/3 lb\r\n" + 
			"6/3 lb tray\r\n" + 
			"6/3.4 Ounce\r\n" + 
			"6/30 oz\r\n" + 
			"6/32 Ounce\r\n" + 
			"6/32 oz\r\n" + 
			"6/32 Oz.\r\n" + 
			"6/33.8 Ounce\r\n" + 
			"6/33.8 oz\r\n" + 
			"6/38 oz\r\n" + 
			"6/4-4oz\r\n" + 
			"6/4-8oz\r\n" + 
			"6/5 lb\r\n" + 
			"6/5 Ounce\r\n" + 
			"6/5 Oz.\r\n" + 
			"6/5.25 Oz.\r\n" + 
			"6/6 Ounce\r\n" + 
			"6/64 Ounce\r\n" + 
			"6/64 Oz.\r\n" + 
			"6/64oz\r\n" + 
			"6/7 Ounce\r\n" + 
			"6/8 Ounce\r\n" + 
			"6/8.5 oz\r\n" + 
			"6/96 oz\r\n" + 
			"60 ct\r\n" + 
			"60-3.5 Oz.\r\n" + 
			"6-18.2oz\r\n" + 
			"7/18 Ounce\r\n" + 
			"75 ct\r\n" + 
			"7-8 lb avg\r\n" + 
			"7-9 lb avg\r\n" + 
			"8/12 Ounce\r\n" + 
			"8/14 Ounce\r\n" + 
			"8/14.11 oz\r\n" + 
			"8/22 Ounce\r\n" + 
			"8/4 Ct.\r\n" + 
			"8/4.53 Ounce\r\n" + 
			"8/5.95 Ounce\r\n" + 
			"8/64 Ounce\r\n" + 
			"8-11 Oz\r\n" + 
			"9/ 1/2 Gallons\r\n" + 
			"9/1 L\r\n" + 
			"90 ct\r\n" + 
			"Case\r\n" + 
			"Dozen\r\n" + 
			"Pack\r\n" + 
			"Quart\r\n";
	
	static final String productList = "Almond Flour Ext Fine Nutley Farms\r\n" + 
			"Almond Milk Creamer Vanilla\r\n" + 
			"Almond Milk Multipack Vanilla\r\n" + 
			"Almond Milk Protein Unsweetened\r\n" + 
			"Almond Milk Reduced Sugar 6/64 Oz\r\n" + 
			"Almond Milk Unsweetened 6/64oz\r\n" + 
			"Almond Milk Unsweetened 8/64 Oz\r\n" + 
			"Almondmilk Barista Blend Dairy Free\r\n" + 
			"Anchovy Fillets 12/7oz #9010127\r\n" + 
			"Anchovy Fillets Marinated #901024\r\n" + 
			"Arancini Balls\r\n" + 
			"Artichoke Hearts Stuffed\r\n" + 
			"Artichokes Grilled #902024\r\n" + 
			"Asiago Cheese + Asparagus\r\n" + 
			"Baking Chips Dark Grain 12/10oz\r\n" + 
			"Baking Powder 5#\r\n" + 
			"Baking Powder Aluminum Free 5#\r\n" + 
			"Balsamic Vinegar 6/16.9 Oz\r\n" + 
			"Bars Apple Cinnamon & Pecan\r\n" + 
			"Bars Blueberry Vanilla & Cashew\r\n" + 
			"Base Au Jus\r\n" + 
			"Base Beef Roasted\r\n" + 
			"Base Beef Roasted\r\n" + 
			"Base Beef Type MC\r\n" + 
			"Base Chicken Euro Roast\r\n" + 
			"Base Chicken Roast Euro\r\n" + 
			"Base Chicken Tpe MC\r\n" + 
			"Base Clam\r\n" + 
			"Base Clam No MSG\r\n" + 
			"Base Clam Type MS\r\n" + 
			"Base Garlic Roasted\r\n" + 
			"Base Ham Smoke House\r\n" + 
			"Base Ham Type MC\r\n" + 
			"Base Ham Type MC\r\n" + 
			"Base Lobster\r\n" + 
			"Base Mirepoix\r\n" + 
			"Base Mirepoix\r\n" + 
			"Base Mushroom No MSG\r\n" + 
			"Base Onion Caramelized\r\n" + 
			"Base Onion Roasted\r\n" + 
			"Base Pork No MSG\r\n" + 
			"Base Sauce Glace De Viande\r\n" + 
			"Base Turkey No MSG\r\n" + 
			"Base Turkey Type MC\r\n" + 
			"Beef Empanadas\r\n" + 
			"Beef Hibachi\r\n" + 
			"Beef Kabob\r\n" + 
			"Beef Satay Teriyaki\r\n" + 
			"Beef Wellington\r\n" + 
			"Beef Wellington 1.25 oz\r\n" + 
			"Beef/Veal Demi Glace\r\n" + 
			"Bev Art Spearmint & Lime (Mojito)\r\n" + 
			"Bev Art Yuzu Luxe Sour\r\n" + 
			"Blintz Sweet Cheese 1.75 oz\r\n" + 
			"Bratwurst Links Guinness Beer 5-1\r\n" + 
			"Butter Beumont 83% Bue100 3\r\n" + 
			"Butter Beurremont 83% Log Bue450\r\n" + 
			"Butter Solid Unsalted\r\n" + 
			"Cake Almond Toasted 10\"\r\n" + 
			"Cake Almond Toasted 10\" 14 Slice\r\n" + 
			"Cake Caramel Pecan Fudge\r\n" + 
			"Cake Caramel Pecan Fudge\r\n" + 
			"Cake Carrot Skyscraper 10\"\r\n" + 
			"Cake Choc Fudge Skyscraper 10\"\r\n" + 
			"Cake Choc Gluten Free 10\"\r\n" + 
			"Cake Choc Gluten Free Ct 10\"\r\n" + 
			"Cake Hazelnut Mousse 10\"\r\n" + 
			"Cake Hazelnut Mousse 10\" 14 Slice\r\n" + 
			"Cake Lava Large 12 ct\r\n" + 
			"Cake Lava Mini 20 ct\r\n" + 
			"Cake Limoncello Mascarpone\r\n" + 
			"Cake Limoncello Mascarpone\r\n" + 
			"Cake Mixed Berry 10\"\r\n" + 
			"Cake Mixed Berry 10\" 14 Slice\r\n" + 
			"Cake Oreo Mousse 10\"\r\n" + 
			"Cake Oreo Mousse 10\" 14 Slice\r\n" + 
			"Cake Tiramisu Tray\r\n" + 
			"Cake Tiramisu Tray\r\n" + 
			"Calzone Hors D'oeuvres Mini\r\n" + 
			"Cereal Corn Flakes Juice Swt 6/26.4\r\n" + 
			"Cheese Alejandro Mexican Shredded\r\n" + 
			"Cheese Blue Monforte Crumbles\r\n" + 
			"Cheese Burrata Alla Panna\r\n" + 
			"Cheese Cheddar Mild Shred Feather\r\n" + 
			"Cheese Fontina Wheel\r\n" + 
			"Cheese MJ/Ched Fcy Shred\r\n" + 
			"Cheese MontJack,Oaxaca/Cotija Blend\r\n" + 
			"Cheese Mozzarella Shredded\r\n" + 
			"Cheese Parmesan Shaved\r\n" + 
			"Cheese Pecorino Romano Shaved\r\n" + 
			"Cheese Pizza 4 Blend Shredded\r\n" + 
			"Cheese Soy Non Hydrogenated 30#\r\n" + 
			"Cheese Swiss Shredded Feather\r\n" + 
			"Cheesecake Chocolate Swirl\r\n" + 
			"Cheesecake Italian 10\"\r\n" + 
			"Cheesecake Italian 10\"\r\n" + 
			"Cheesecake NY Style\r\n" + 
			"Cheesecake Plain 10\" Sliced\r\n" + 
			"Cheesecake Plain 12\" Carnegie\r\n" + 
			"Cheesecake Pumpkin Swirl\r\n" + 
			"Cheesecake Strawberry Swirl\r\n" + 
			"Chicken Empanada\r\n" + 
			"Chicken Hibachi\r\n" + 
			"Chicken Pwdr Asian N/MSG\r\n" + 
			"Chicken Sesame Skewer\r\n" + 
			"Chicken Stk Clar Concen\r\n" + 
			"Chicken Stock Clarified Concentrate\r\n" + 
			"Chicken Wellington Mini\r\n" + 
			"Chocolate Couverture Classic 55%\r\n" + 
			"Chocolate Batons 44% Cacao Noel\r\n" + 
			"Chocolate Couv Royal 64%\r\n" + 
			"Chocolate Couverture Blanc 30% Bulk\r\n" + 
			"Chocolate Couverture Milk 35% Noel\r\n" + 
			"Chocolate Grand 58% Bitterswt Noel\r\n" + 
			"Chorizo Sausage Uncooked Smoke Flvr\r\n" + 
			"Coconut Orginial Milk 6/64oz\r\n" + 
			"Compote Assorted Red Fruits\r\n" + 
			"Cookie Mix Bee Sting Florentine\r\n" + 
			"Cool Spray - Decomuse\r\n" + 
			"Cottage Cheese Lowfat\r\n" + 
			"Coulis Raspberry Ravifruit\r\n" + 
			"Crab Cakes Jumbo\r\n" + 
			"Crab Rangoon\r\n" + 
			"Cream Aero Whip Topp 14 Oz.\r\n" + 
			"Cream Half & Half\r\n" + 
			"Cream Half & Half Fr Vanilla\r\n" + 
			"Cream Half & Half Quart\r\n" + 
			"Cream Heavy Whip\r\n" + 
			"Creamer Hazelnut NonDairy 12/16oz\r\n" + 
			"Creme Fraiche\r\n" + 
			"Dairy Free NY Cheesecake 8/14 Oz\r\n" + 
			"Demi Glace Concentrate\r\n" + 
			"Devil on Horseback\r\n" + 
			"Dextrose CT1006\r\n" + 
			"Dressing 1000 Island (Zito)\r\n" + 
			"Dressing Bleu Cheese Classic (Zito)\r\n" + 
			"Dressing Italian Golden (Zito)\r\n" + 
			"Dressing JMH Bleu Cheese\r\n" + 
			"Dressing JMH Italian BnB\r\n" + 
			"Dressing JMH Ranch no/MSG\r\n" + 
			"Dressing JMH Ranch w/msg BnB\r\n" + 
			"Dressing Ranch Hidden Villa (Zito)\r\n" + 
			"Dressing Ranch w/o MSG (Zito)\r\n" + 
			"Dried Beans Garbanzo Chickpeas 25#\r\n" + 
			"Dried Coconut Medium Shredded 5#\r\n" + 
			"Drops Semisweet 50% Noel\r\n" + 
			"Dry Hemp Seed 16oz\r\n" + 
			"Dry Pickled Beets Sliced 16oz\r\n" + 
			"Eggplant Roasted #903023\r\n" + 
			"Eggplant Roasted 12/7oz #9030127\r\n" + 
			"Eggplant Rollette with Cheese\r\n" + 
			"Eggs Brown Large USDA\r\n" + 
			"Eggs Brown Medium USDA\r\n" + 
			"Eggs Duck\r\n" + 
			"Eggs Large USDA\r\n" + 
			"Eggs Medium USDA 15 Doz/Cs\r\n" + 
			"Eggs Quail\r\n" + 
			"Eggs Small USDA\r\n" + 
			"Evolution Defense Up Fruit Puree\r\n" + 
			"Flour Arrowroot Starch 4/16 oz.\r\n" + 
			"Flour Baking 1 to 1 25#\r\n" + 
			"Gluten Free Bun Hamburger 8/4ct.\r\n" + 
			"Gluten Free White Bread 8/12oz\r\n" + 
			"Grapeseed Oil \"De Choix\"\r\n" + 
			"Gravy Turkey Concentrate\r\n" + 
			"Ham Bone-In\r\n" + 
			"Herring Chopped Salad\r\n" + 
			"Herring Chopped Salad\r\n" + 
			"Herring in Cream\r\n" + 
			"Herring in Wine\r\n" + 
			"Herring Spicy Matjes\r\n" + 
			"Herring Tidbits Cream\r\n" + 
			"Herring Tidbits in Wine\r\n" + 
			"Ice Cream Almond NonDairy Choc 6/32\r\n" + 
			"Ice Cream Almond NonDairy Van 6/32\r\n" + 
			"IQF Apricot Halves - Bergeron\r\n" + 
			"IQF Fruit Cherry Morello Pitted\r\n" + 
			"IQF Lemon Zest Ravi\r\n" + 
			"IQF Orange Zest Ravi\r\n" + 
			"Juice Agave Margarita M1000\r\n" + 
			"Juice Agave Mojito Bar Juice\r\n" + 
			"Juice Apple\r\n" + 
			"Juice Apple Cider\r\n" + 
			"Juice Beet Red\r\n" + 
			"Juice Blackberry Blast 12/10oz\r\n" + 
			"Juice Blackberry Hibiscus 12/10oz\r\n" + 
			"Juice Carrot\r\n" + 
			"Juice Cherry Charge 12/10oz\r\n" + 
			"Juice Coconut Mango 12/10oz\r\n" + 
			"Juice Coconut Water/Green 6/15.2oz\r\n" + 
			"Juice Cranberry 35% Bar Juice\r\n" + 
			"Juice Cranberry Lemonade 12/10oz\r\n" + 
			"Juice Cucumber\r\n" + 
			"Juice Essential Greens 6/15.2oz\r\n" + 
			"Juice Goji\r\n" + 
			"Juice Grape Power 12/10oz\r\n" + 
			"Juice Grapefruit\r\n" + 
			"Juice Grapefruit 6/15.2oz\r\n" + 
			"Juice Green Apple\r\n" + 
			"Juice Green Grape\r\n" + 
			"Juice Key Lime\r\n" + 
			"Juice Lemon\r\n" + 
			"Juice Lemon (Golden Sun)\r\n" + 
			"Juice Lemon 100% Concentrate\r\n" + 
			"Juice Lime\r\n" + 
			"Juice Mango\r\n" + 
			"Juice Mangosteen & Mango 12/17oz\r\n" + 
			"Juice Orange\r\n" + 
			"Juice Organic Cherry Lime 12/10oz\r\n" + 
			"Juice Organic Pineapple Coconut\r\n" + 
			"Juice Organic Strawberry Lemonade\r\n" + 
			"Juice Pineapple\r\n" + 
			"Juice Pineapple Quart\r\n" + 
			"Juice Pom Peach Passion Wht 6/12oz\r\n" + 
			"Juice Pomegranate\r\n" + 
			"Juice Pomegranate Blueberry 6/16oz\r\n" + 
			"Juice Pomegranate Cherry 6/16oz\r\n" + 
			"Juice Pomegranate Mint 12/10oz\r\n" + 
			"Juice Pomegrante 6/16oz\r\n" + 
			"Juice Raspberry\r\n" + 
			"Juice Raspberry Passion 12/10oz\r\n" + 
			"Juice Raspberry Razz 12/10oz\r\n" + 
			"Juice Red Grape\r\n" + 
			"Juice San Marzano Bloody Bar Juice\r\n" + 
			"Juice Smooth Greens 6/15.2oz\r\n" + 
			"Juice Spicy Lemonade 6/15.2oz\r\n" + 
			"Juice Strawberry\r\n" + 
			"Juice Sweet Greens &Ginger 6/15.2oz\r\n" + 
			"Juice Tangerine\r\n" + 
			"Juice Tangerine\r\n" + 
			"Juice Vegetable& Fruit Mix 6/15.2oz\r\n" + 
			"Juice Watermelon\r\n" + 
			"Juice Watermelon\r\n" + 
			"Juice Watermelon\r\n" + 
			"Juice Watermelon & Peach 12/17oz\r\n" + 
			"Juice Wheat Grass\r\n" + 
			"Juice Wheatgrass Awaken 12/17oz\r\n" + 
			"Juice Yuzu Sodium Free\r\n" + 
			"Kabob Chicken With Pineapple\r\n" + 
			"Knish Potato Square 30 pc\r\n" + 
			"Kobe Beef Franks in Puff Pastry\r\n" + 
			"Lemonade\r\n" + 
			"Liver Chopped\r\n" + 
			"Lobster Fillo Real\r\n" + 
			"Mayonnaise Supreme\r\n" + 
			"Meatball Cooked .5 oz\r\n" + 
			"Milk 2% 5 Gallon Bag in Box\r\n" + 
			"Milk 2% Gallon\r\n" + 
			"Milk Almond Dark Chocolate 12/8oz\r\n" + 
			"Milk Almond Vanilla 12/8oz\r\n" + 
			"Milk Buttermilk 1/2 Gallon\r\n" + 
			"Milk Coconut Non-Dairy Unsweetened\r\n" + 
			"Milk Hemp NonDairy Dry 12/32oz\r\n" + 
			"Milk Nonfat Gallon\r\n" + 
			"Milk Whole Gallon\r\n" + 
			"Mini Bars Coconut Almond\r\n" + 
			"Mini Bars Mocha Almond Fudge\r\n" + 
			"Mini Ice Cream Sandwich Vanilla\r\n" + 
			"Mushroom Caps Sausage\r\n" + 
			"Mushroom Caps Spinach/Cheese\r\n" + 
			"Mushroom Wild Tart Mini\r\n" + 
			"Mushrooms Grilled #904024\r\n" + 
			"Mushrooms Grilled 12/7oz #9040127\r\n" + 
			"Mushrooms Mixed #904124\r\n" + 
			"Mushrooms Mixed 12/7oz #9041127\r\n" + 
			"Nuts Almonds Raw 5#\r\n" + 
			"Nuts Walnut Raw Halved Pieces 5#\r\n" + 
			"Oatmeal 5 Berry 6/2.3oz\r\n" + 
			"Oatmeal Apple Walnut 6/2.6oz\r\n" + 
			"Oatmeal Berry Medley Gluten Free\r\n" + 
			"Oatmeal Brown Sugar & Flax\r\n" + 
			"Oatmeal Nuts & Seeds 6/2.3oz\r\n" + 
			"Octopus Salad #905024\r\n" + 
			"Octopus Salad 12/7oz # 9050127\r\n" + 
			"Oil Arrope Grape Must\r\n" + 
			"Oil Asian Wok Cooking 6/10oz\r\n" + 
			"Oil Grapeseed Spray 6/6oz\r\n" + 
			"Oil Olive Extra Virgin Bag in Box\r\n" + 
			"Oil Olive White Truffle 6/8oz\r\n" + 
			"Old Texas BBQ Blend\r\n" + 
			"Olives Blk Monacale #906126\r\n" + 
			"Olives Blk Monacale 12/7oz #9061127\r\n" + 
			"Olives Green Pitted #906026\r\n" + 
			"Olives Green Pitted 12/7oz #9061127\r\n" + 
			"Onions Borettane 12/7oz #9070127\r\n" + 
			"Onions Borettane Grilled #907024\r\n" + 
			"Organic Apple Fruit Crunchers\r\n" + 
			"Organic Bacon 10/8 Oz.\r\n" + 
			"Organic Baking Flours 50#\r\n" + 
			"Organic Bananas Frozen 4/5#\r\n" + 
			"Organic Basmati Rice Brown 25#\r\n" + 
			"Organic BBQ Sauce Hickory 12/18 Oz.\r\n" + 
			"Organic Beans Black 25#\r\n" + 
			"Organic Beans Black Turtle 25#\r\n" + 
			"Organic Beans Garbanzo 25#\r\n" + 
			"Organic Beans Pinto 25#\r\n" + 
			"Organic Beans Red 25#\r\n" + 
			"Organic Berry Medley Fruit Snacks\r\n" + 
			"Organic Black Cocoa Powder 16 Oz\r\n" + 
			"Organic Blue Cheese Crumbles 12/4oz\r\n" + 
			"Organic Blueberries Frozen 6/2#\r\n" + 
			"Organic Blueberries Wild Frozen 4/5\r\n" + 
			"Organic Bouillon Veg Base 6/8 Oz.\r\n" + 
			"Organic Bread Crumbs 6/10.5 Oz.\r\n" + 
			"Organic Bread Crumbs Light Salt\r\n" + 
			"Organic Bread Crumbs Panko 6/10.5oz\r\n" + 
			"Organic Bread Rye Light 8/22 Oz.\r\n" + 
			"Organic Bread Sourdough 8/22 Oz\r\n" + 
			"Organic Bun Hamburger Wheat 7/18 Oz\r\n" + 
			"Organic Butter Salted Sticks 18/1#\r\n" + 
			"Organic Butter Unsalted 15/1#\r\n" + 
			"Organic Cacao Nibs 1#\r\n" + 
			"Organic Cake Mix 6-18.2oz\r\n" + 
			"Organic Canned Milk 24/12 Oz.\r\n" + 
			"Organic Cereal Corn Flakes 12/10oz\r\n" + 
			"Organic Cereal Granola Almond 25#\r\n" + 
			"Organic Cheese Colby Sticks 12/6oz\r\n" + 
			"Organic Cheese Monterey 12/8 Oz.\r\n" + 
			"Organic Cheese Mozzarella Sticks\r\n" + 
			"Organic Cheese Parmesan 12/4 Oz.\r\n" + 
			"Organic Chips Blue Corn 12/16 Oz\r\n" + 
			"Organic Chips Yellow 12/16 Oz.\r\n" + 
			"Organic Choc Syrup 12/15 Oz.\r\n" + 
			"Organic Chocolate NonDairy Desert\r\n" + 
			"Organic Chocolate Syrup 12/22 Oz.\r\n" + 
			"Organic Cocoa Powder Dutch 1#\r\n" + 
			"Organic Coconut Milk 12/13.5oz\r\n" + 
			"Organic Coconut Milk Chocolate\r\n" + 
			"Organic Coffee Dark 6/10 Oz\r\n" + 
			"Organic Coffee Dark Power Ground\r\n" + 
			"Organic Coffee Dark Power Whl Bean\r\n" + 
			"Organic Corn Starch 6/1 Oz.\r\n" + 
			"Organic Cornflakes 6/26.4 Oz.\r\n" + 
			"Organic Cornmeal Yellow Coars 25#\r\n" + 
			"Organic Cornstarch 1#\r\n" + 
			"Organic Cottage Cheese 6/16oz\r\n" + 
			"Organic Cracker Sand Peanut Butter\r\n" + 
			"Organic Crackers Saltine 6/7os.\r\n" + 
			"Organic Crackers Sandwich Cheddar\r\n" + 
			"Organic Cream Cheese 12/8 Oz.\r\n" + 
			"Organic Cream Half & Half 6/64 Oz.\r\n" + 
			"Organic Creamer Coconut 12/5oz\r\n" + 
			"Organic Croutons Italian 6/5.25oz\r\n" + 
			"Organic Dried Fruit Dates Medjool\r\n" + 
			"Organic Dry Spice Paprika 1#\r\n" + 
			"Organic Eggs Brown 15 Dz.\r\n" + 
			"Organic English Muffin 6 Ct. 8/12oz\r\n" + 
			"Organic Extra Virgin Med Olive Oil\r\n" + 
			"Organic Extra Virgin Olive Oil 35#\r\n" + 
			"Organic Fakin Bacon Tempeh Strips\r\n" + 
			"Organic Flour Bread & Bun 25#\r\n" + 
			"Organic Flour Brown Rice 25#\r\n" + 
			"Organic Flour Corn 25#\r\n" + 
			"Organic Flour Wheat Gluten 25#\r\n" + 
			"Organic Frozen Blueberries IQF 30#\r\n" + 
			"Organic Frozen Mangos 4/5#\r\n" + 
			"Organic Granola 10#\r\n" + 
			"Organic Granola Chocolate 6/16 Oz\r\n" + 
			"Organic Granola Vanilla 25#\r\n" + 
			"Organic Harvest Misc\r\n" + 
			"Organic Hemp Milk Non Dairy 12/32oz\r\n" + 
			"Organic Hemp Seed Nut 6/12oz\r\n" + 
			"Organic Hemp Seed Shelled 5#\r\n" + 
			"Organic Herbs & Spices 1# Paprika\r\n" + 
			"Organic Honey Clover 6/18 Oz.\r\n" + 
			"Organic Horseradish 12/8 Oz.\r\n" + 
			"Organic Juice Acai 6/10.5 Oz\r\n" + 
			"Organic Juice Aloe Vera Gallon\r\n" + 
			"Organic Juice Cold Press Suja Cucum\r\n" + 
			"Organic Juice Mango Magic 6/12oz\r\n" + 
			"Organic Ketchup #10 Can 6/112 Oz\r\n" + 
			"Organic Ketchup 12/24oz\r\n" + 
			"Organic Ketchup 12/32 Oz.\r\n" + 
			"Organic Lemongrass Sifted 1#\r\n" + 
			"Organic Lentils Green Laird 25#\r\n" + 
			"Organic Maple Syrup 12/32 Oz\r\n" + 
			"Organic Mayonnaise 12/32 Oz\r\n" + 
			"Organic Mayonnaise 12/32 Oz.\r\n" + 
			"Organic Milk 1% Chcolate Club Pack\r\n" + 
			"Organic Milk 1% Strawberry Club Pac\r\n" + 
			"Organic Milk 1% Vanilla Club Pack\r\n" + 
			"Organic Milk Buttermilk 2% 6/32 Oz.\r\n" + 
			"Organic Milk Buttermilk LF 12/32 Oz\r\n" + 
			"Organic Milk Fat Free 6/64oz\r\n" + 
			"Organic Milk Low Fat 1% 6/64oz\r\n" + 
			"Organic Milk Soy Plain 6/64oz\r\n" + 
			"Organic Milk Whole 4/1 Gallon\r\n" + 
			"Organic Milk Whole Fortified 6/64oz\r\n" + 
			"Organic Mini Bars Strawberry\r\n" + 
			"Organic Miso White Mellow 15#\r\n" + 
			"Organic Mustard Dijon 12/8 Oz.\r\n" + 
			"Organic Nuts Cashew 5#\r\n" + 
			"Organic Nuts Whole Raw Brazil 5#\r\n" + 
			"Organic Oil Olive Ex Virgin 6/16.9\r\n" + 
			"Organic Oil Olive Extra Virgin\r\n" + 
			"Organic Oil Sunflower Spray 6/5 Oz.\r\n" + 
			"Organic Pasta Chiocciole 12/16 Oz\r\n" + 
			"Organic Pasta Linguine 12/16 Oz\r\n" + 
			"Organic Pasta Penne Rigate 12/16oz\r\n" + 
			"Organic Pasta Rigatoni 12/16oz\r\n" + 
			"Organic Peas Split Green 25#\r\n" + 
			"Organic Peppercorn Black Glass Btl\r\n" + 
			"Organic Pickles Kosher Dill 6/24oz\r\n" + 
			"Organic Pineapple Frozen 4/5#\r\n" + 
			"Organic Quinoa Whirte 25#\r\n" + 
			"Organic Real Pure 4 100g Pks\r\n" + 
			"Organic Rice Brown Jasmine 25#\r\n" + 
			"Organic Rice Long Grain 25#\r\n" + 
			"Organic Snacks Grahams Chocolate\r\n" + 
			"Organic Snacks Grahams Cinnamon\r\n" + 
			"Organic Sour Cream 6/16 Oz\r\n" + 
			"Organic Soy Milk Unsweetened 8/64oz\r\n" + 
			"Organic Spice Cayenne\r\n" + 
			"Organic Spice Cilantro Leaf\r\n" + 
			"Organic Spice Hibiscus Flower 1#\r\n" + 
			"Organic Spray Sunflower Oil 6/5 Oz\r\n" + 
			"Organic Strawberries Frozen 4/5#\r\n" + 
			"Organic Sugar 25#\r\n" + 
			"Organic Sugar Cane 25#\r\n" + 
			"Organic Sugar Packets 500/4.5 Gr\r\n" + 
			"Organic Sugar Raw Cane 500/4.5 Gr\r\n" + 
			"Organic Sunflower Oil 35#\r\n" + 
			"Organic Sweetened Condensed Milk\r\n" + 
			"Organic Sweetener 12/70 Ct.\r\n" + 
			"Organic Sweeteners Sugar Cane 25#\r\n" + 
			"Organic Sweeteners Sugar Powder 50#\r\n" + 
			"Organic Syrup Agave Gallon\r\n" + 
			"Organic Syrup Agave Light Gallon\r\n" + 
			"Organic Syrup Maple Grade B 1/64 Oz\r\n" + 
			"Organic Syrup Maple Grade B 6/16.9\r\n" + 
			"Organic Tamari Sauce Wheat Free\r\n" + 
			"Organic Tea Energy Mate Raspberry\r\n" + 
			"Organic Tea Loose Leaves 1#\r\n" + 
			"Organic Teas Gunpowder Grn Loose\r\n" + 
			"Organic Tempeh Soy 12/8 Oz\r\n" + 
			"Organic Tofu Extra Firm 6/12oz\r\n" + 
			"Organic Tofu Firm 6/14 Oz\r\n" + 
			"Organic Tofu Super Firm 6/16 Oz\r\n" + 
			"Organic Tomatillos Crushed 6/23 Oz\r\n" + 
			"Organic Tomato Sauce 6/106 Oz\r\n" + 
			"Organic Tomato Whole Peeled\r\n" + 
			"Organic Tomatoes Crushed 6/104oz\r\n" + 
			"Organic Tortilla Whl Wheat 10/12 Ct\r\n" + 
			"Organic Unbleached Bread Flour\r\n" + 
			"Organic Unbleached White Flour\r\n" + 
			"Organic Vanilla Bean Ice Cream\r\n" + 
			"Organic Vanilla Creamy NonDairy\r\n" + 
			"Organic Vinegar Red Wine 2/1.32 Gal\r\n" + 
			"Organic Vinegar White 12/32 Oz.\r\n" + 
			"Organic Water Coconut 12/11.2oz\r\n" + 
			"Organic Whole Wheat Pasta Chicciole\r\n" + 
			"Organic Yogurt Plain 6/32 Oz.\r\n" + 
			"Pancake Mix Gluten Free 4/22oz\r\n" + 
			"Paste Ancho Chili\r\n" + 
			"Paste Chipotle Chili\r\n" + 
			"Pastry Cream, Hot Proc Pstry\r\n" + 
			"Patty Veggie Hemp Seed 1/10oz\r\n" + 
			"Pearls Chocolate Crunchy Dark Noel\r\n" + 
			"Pearls Chocolate Crunchy White Noel\r\n" + 
			"Peppers Roasted #908024\r\n" + 
			"Peppers Roasted 12/7oz #9080127\r\n" + 
			"Pickled Specialty Italian Mix 6/16\r\n" + 
			"Pickles Garlic Dill Slices 6/24oz\r\n" + 
			"Pie Key Lime 10\" 12 Slice\r\n" + 
			"Pizza Combo Pack Mini\r\n" + 
			"Potstickers Duck\r\n" + 
			"Powder Arrowroot 5#\r\n" + 
			"Powder Packets Acai Energy 24 Ct.\r\n" + 
			"Protein Bar Cookies & Cream 12/2 Oz\r\n" + 
			"Puree Apricot\r\n" + 
			"Puree Apricot Ravi\r\n" + 
			"Puree Banana\r\n" + 
			"Puree Banana Ravi\r\n" + 
			"Puree Black Currant\r\n" + 
			"Puree Black Currant Cassis Ravi\r\n" + 
			"Puree Blackberry Ravi\r\n" + 
			"Puree Blood Orange\r\n" + 
			"Puree Blood Orange Ravi\r\n" + 
			"Puree Blueberry Ravi\r\n" + 
			"Puree Carmelized Pineapple\r\n" + 
			"Puree Cherry Morello Ravi\r\n" + 
			"Puree Coconut\r\n" + 
			"Puree Coconut Pail Ravi\r\n" + 
			"Puree Ginger\r\n" + 
			"Puree Grapefruit Pink Ravi\r\n" + 
			"Puree Green Apple\r\n" + 
			"Puree Guava Pink Ravi\r\n" + 
			"Puree Key Lime\r\n" + 
			"Puree Kiwi\r\n" + 
			"Puree Lemon (Citron) Pail Ravi\r\n" + 
			"Puree Lemon (Citron) Ravi\r\n" + 
			"Puree Lemon Zest\r\n" + 
			"Puree Lychee\r\n" + 
			"Puree Mandarin Orange Ravi\r\n" + 
			"Puree Mandarin/Tangerine\r\n" + 
			"Puree Mango\r\n" + 
			"Puree Mango Pail Ravi\r\n" + 
			"Puree Mango Ravi\r\n" + 
			"Puree Pabana Ravi\r\n" + 
			"Puree Papaya\r\n" + 
			"Puree Papaya Ravi\r\n" + 
			"Puree Passion Fruit\r\n" + 
			"Puree Passion Fruit Pail Ravi\r\n" + 
			"Puree Passion Ravi\r\n" + 
			"Puree Pear William\r\n" + 
			"Puree Pear William Ravi\r\n" + 
			"Puree Pineapple Ravi\r\n" + 
			"Puree Pink Guava\r\n" + 
			"Puree Prickly Pear\r\n" + 
			"Puree Prickly Pear Ravi\r\n" + 
			"Puree Raspberry\r\n" + 
			"Puree Raspberry Pail Ravi\r\n" + 
			"Puree Raspberry Ravi\r\n" + 
			"Puree Red Pepper Roasted\r\n" + 
			"Puree Strawberry\r\n" + 
			"Puree Strawberry Pail Ravi\r\n" + 
			"Puree Strawberry Ravi\r\n" + 
			"Puree White Peach\r\n" + 
			"Puree White Peach Ravi\r\n" + 
			"Purse Feta Cheese Sundried Tomato\r\n" + 
			"Quesadilla Smoked Chicken\r\n" + 
			"Quesadilla Vegetable Cornucopia\r\n" + 
			"Refried Pinto Beans\r\n" + 
			"Rice Bran Oil 35#\r\n" + 
			"Roast Beef Top Round Split Cooked\r\n" + 
			"Sable Plates\r\n" + 
			"Salad Tuscan Antipasti\r\n" + 
			"Salmon Baked Kipper Large\r\n" + 
			"Salmon Kippered Salad\r\n" + 
			"Salmon Lox 3 oz Vacuum Pack\r\n" + 
			"Salmon Lox Large Sides\r\n" + 
			"Salmon Nova 4 oz\r\n" + 
			"Salmon Nova 8 oz Sliced\r\n" + 
			"Salmon Nova Trimmings\r\n" + 
			"Salmon Sliced Deli Trays\r\n" + 
			"Salmon Smoked Pre-Sliced Nova\r\n" + 
			"Salmon Sockeye 3 oz\r\n" + 
			"Salmon VP Sides (Eastern)\r\n" + 
			"Salsa De Los Piratas\r\n" + 
			"Sandwich Sundried Tomato w/Basil\r\n" + 
			"Sandwiches American Cheese\r\n" + 
			"Sandwiches Brie & Apricot\r\n" + 
			"Sandwiches Cubano\r\n" + 
			"Sandwiches Goat Cheese & Olive\r\n" + 
			"Sandwiches Mini Reuben\r\n" + 
			"Sandwiches Pastrami\r\n" + 
			"Satay Beef\r\n" + 
			"Satay Chicken\r\n" + 
			"Sauce Alfredo\r\n" + 
			"Sauce BBQ Gourmet Zito\r\n" + 
			"Sauce BBQ Kagome Delux\r\n" + 
			"Sauce BBQ Kagome Delux\r\n" + 
			"Sauce Cocktail Classic\r\n" + 
			"Sauce Enchilada Kagome\r\n" + 
			"Sauce Hollandaise\r\n" + 
			"Sauce Marinara\r\n" + 
			"Sauce Marinara Signature\r\n" + 
			"Sauce Nacho Cheese\r\n" + 
			"Sauce Orange Glaze Kagome\r\n" + 
			"Sauce Pizza Delux Kagome\r\n" + 
			"Sauce Sweet and Sour\r\n" + 
			"Sauce Sweet Chili Kagome\r\n" + 
			"Sauce Tartar\r\n" + 
			"Sauce Teriyaki Glaze\r\n" + 
			"Sausage Link Skin On 1.6 oz\r\n" + 
			"Sausage Links Italian 5-1 Cooked\r\n" + 
			"Sausage Rope Mild Italian\r\n" + 
			"Scallops Wrapped In Bacon\r\n" + 
			"Seasoning Canadian Grille\r\n" + 
			"Seitan Chicken Style 6/18 Oz\r\n" + 
			"Shrimp Bacon Wrap Skewer\r\n" + 
			"Shrimp Coconut\r\n" + 
			"Shrimp Coconut Skewer\r\n" + 
			"Skewers Hibachi Beef W/Port Reduction\r\n" + 
			"Smoothie Pack Dragonfruit 60-3.5 Oz\r\n" + 
			"Soup Asparagus Chicken Chowder\r\n" + 
			"Soup Base Chicken Broth 5#\r\n" + 
			"Soup Boston Clam Chowder\r\n" + 
			"Soup Broccoli Ched Chs Signature\r\n" + 
			"Soup Butternut Squash and Carrots\r\n" + 
			"Soup Chicken Tortilla\r\n" + 
			"Soup Clam Chowder Chefs Choice\r\n" + 
			"Soup Clam Chowder M\r\n" + 
			"Soup Corn Chowder Signature\r\n" + 
			"Soup Cream of Mushroom / Morel\r\n" + 
			"Soup Cream of Mushroom w/Morel CC\r\n" + 
			"Soup Italian Sausage Signature\r\n" + 
			"Soup Italian Wedding\r\n" + 
			"Soup Lentil Bean / Chickpea\r\n" + 
			"Soup Lobster Bisque\r\n" + 
			"Soup Mushroom Beef Barley\r\n" + 
			"Soup Onion English Ale\r\n" + 
			"Soup Poultry\r\n" + 
			"Soup Roasted Poblano Corn Chowder\r\n" + 
			"Soup Split Pea\r\n" + 
			"Soup Tomato\r\n" + 
			"Soup Tomato Basil M\r\n" + 
			"Soup Tomato Bisque\r\n" + 
			"Soup Tuscan Sausage Signature\r\n" + 
			"Soup White Bean\r\n" + 
			"Sour Cream\r\n" + 
			"Soy Beverage Blenders Plain Dry\r\n" + 
			"Soy Beverage Unsweetened Low Salt\r\n" + 
			"Soy Cheese Cheddar Style 12/8 oz\r\n" + 
			"Soy Cheese Cheddar Style 3/5#\r\n" + 
			"Soy Cheese Mozarella Style 3/5#\r\n" + 
			"Soy Cheese Mozzarella Vegan 1/4#\r\n" + 
			"Spanakopita\r\n" + 
			"Spanakopita 1.5oz\r\n" + 
			"Spice Basil Leaf Cut & Sifted 1#\r\n" + 
			"Spice Blends Goji Berries Whole 1#\r\n" + 
			"Spice Garlic Granulated 1#\r\n" + 
			"Spice Onion Granulated 1#\r\n" + 
			"Spice Oregano Leaf Cut & Sifted 1#\r\n" + 
			"Spice Pepper Black Ground 1#\r\n" + 
			"Spice Peppercorn Herbs Glass Btl\r\n" + 
			"Spice Salt Grinder Pink 6/3.4oz\r\n" + 
			"Spice Sea Salt Table Grind 1/5#\r\n" + 
			"Spice Seasoning Blackened Redfish\r\n" + 
			"Spice Tumeric Ground 1#\r\n" + 
			"Spinch & Artichoke Tart Fillo\r\n" + 
			"Spring Roll Black Bean Southwest\r\n" + 
			"Spring Roll Blk Bn1oz No Chorizo\r\n" + 
			"Spring Roll Pork Southwest 6 3/4 \"\r\n" + 
			"Spring Roll Shrimp & Lobster\r\n" + 
			"Spring Roll Vegetable\r\n" + 
			"Stabilizer Creme 64 (Sorbet)\r\n" + 
			"Stabilizer Cream Whipped Sieben\r\n" + 
			"Stabilizer Creme 30 (Ice Cream)\r\n" + 
			"Steak Sauce Calif Style 6/12 Oz\r\n" + 
			"Sugar Granulated Cane 10/4#\r\n" + 
			"Sunflower Seeds Raw Bulk 25#\r\n" + 
			"Syrup Glucose\r\n" + 
			"Tomatoes Dried #909024\r\n" + 
			"Tomatoes Dried 12/7oz #9090127\r\n" + 
			"Tomatoes Semi Dry #909124\r\n" + 
			"Tomatoes Semi Dry 12/7oz #9091127\r\n" + 
			"Trimoline (Inverted Sugar) Nevuline\r\n" + 
			"Turkey Links Skin On .8oz Cooked\r\n" + 
			"Turkey Patties Buffet Style\r\n" + 
			"Vanilla Beans Gahara Indonesian\r\n" + 
			"Vanilla Extract Pure 1/32 Oz\r\n" + 
			"Vanilla Manana Essence\r\n" + 
			"Vegan Burger 12/13 oz\r\n" + 
			"Vegan Butter NonDairy Gluten Free\r\n" + 
			"Vegan Chicken Free Strips 4/5#\r\n" + 
			"Vegan Coconut Milk Pure 12/13.66 Oz\r\n" + 
			"Vegan Sausage Italian 12-12.95oz\r\n" + 
			"Vegan Tomato Loaf Smoked 12/12oz\r\n" + 
			"Vegenaise Original 1/1 Gal\r\n" + 
			"Vegetable Empanadas\r\n" + 
			"Water Carbon Filtered 24/17 Oz\r\n" + 
			"Water Purified 12/34 Oz\r\n" + 
			"Water Purified W/Minerals 12/1Liter\r\n" + 
			"Wellington Beef En Croute\r\n" + 
			"Whiskey Sage Blend\r\n" + 
			"Whitefish Large\r\n" + 
			"Whitefish Large Vac Pack\r\n" + 
			"Whitefish Salad\r\n" + 
			"Wonton Pork Firecracker\r\n" + 
			"Yeast Dry Instant\r\n" + 
			"Yeast Mini Baking 1#\r\n" + 
			"Yogurt Soy Black Cherry 12/5oz\r\n" + 
			"Yogurt Soy Blueberry 12/5oz\r\n" + 
			"Yogurt Soy Peach & Mango 12/5oz\r\n" + 
			"Yogurt Soy Strawberry 12/5oz\r\n" + 
			"Yogurt Soy Trop Pineapple 12/5oz\r\n" + 
			"Yogurt Soy Vanilla 12/5oz\r\n" + 
			"Zucchini Grilled #910023\r\n" + 
			"Zucchini Grilled 12/7oz #9100127\r\n" + 
			"";

}
