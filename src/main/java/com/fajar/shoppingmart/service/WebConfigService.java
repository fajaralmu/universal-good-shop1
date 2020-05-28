package com.fajar.shoppingmart.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.entity.ShopProfile;
import com.fajar.shoppingmart.repository.ShopProfileRepository;
import com.fajar.shoppingmart.test.RmiStopper;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.Data;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */
@Dto
@Data 
public class WebConfigService {

	@Autowired
	private ShopProfileRepository shopProfileRepository;

	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath; 
	private String reportPath;
	private String martCode;  
	
	@PreDestroy
	public void preDestroy() {
		System.out.println("========= will destroy ========");
		RmiStopper.main(new String[] {});
	}
	
	public static String readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    return everything;
		} finally {
		    br.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		String path ="C:\\Users\\Republic Of Gamers\\Documents\\ORCALE_DUMPS\\";
		File folder = new File(path );
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
		  if (file.isFile()) {
			 // System.out.println(path+file.getName());
			  String content = readFile(path+file.getName());
			   
			  if(!file.getName().startsWith("MASTER_PFM_DETAIL")){
				  continue;
			  }
			  filter(content);
		  } else if (file.isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
	}
	
	public static String filter(String content) {
		String[] lines = (content.split("\n"));
		String result = "";
		String firstLine = lines[0].trim();
		if(firstLine.startsWith("INSERT") == false) {
			firstLine = firstLine.substring(3, firstLine.length());
			firstLine = firstLine.replace("MMB", "DEVELOPMENT");
		}
		 
		for (String string : lines) {
			if(string.trim().equals(";")) {
				continue;
			}
			
			String insertStatement = string.substring(3, string.length());
			if(insertStatement.startsWith("INSERT")) {
				string=	string.substring(3, string.length());
			}
			
			string = string.trim();
			if(string.startsWith(",(")) {
				string = string.substring(0,string.length());
				string = firstLine+string;
			}
			if(string.endsWith(")")){
				string+=";";
			}
			string = string.replace("MMB", "DEVELOPMENT");
			string = string.replace("VALUES,", "VALUES");
			result+="\n"+string;
			
			System.out.println(string);
		}
		return result;
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);
		if (null == dbProfile) {
			shopProfileRepository.save(defaultProfile());
		}
	} 
	
	public ShopProfile getProfile() {
		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);

		/*return getShopProfileFromSession(); */ return  EntityUtil.validateDefaultValue(dbProfile);
	}

	private ShopProfile defaultProfile() {
		ShopProfile profile = new ShopProfile();
		profile.setName("Universal Good Shop");
		profile.setAddress("Spring Mvc, Java Virtual Machine, Win 10 64");
		profile.setContact("087737666614");
		profile.setWebsite("http://localhost:8080/universal-good-shop");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setBackgroundUrl("DefaultBackground.BMP");
		profile.setMartCode(martCode);
		profile.setShortDescription("we provide what u need");
		profile.setColor("green");
		profile.setAbout(
				"Nam libero tempore.");
		return profile;
	}

	 

}
