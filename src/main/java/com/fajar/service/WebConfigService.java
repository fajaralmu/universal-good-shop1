package com.fajar.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.fajar.annotation.Dto;
import com.fajar.dto.UserTempRequest;
import com.fajar.entity.ShopProfile;
import com.fajar.repository.ShopProfileRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebConfigService {

	@Autowired
	private ShopProfileRepository shopProfileRepository;

	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String martCode;

	private static Map<String, UserTempRequest> userTemporaryData;

	@Autowired
	private Session hibernateSession;

	@Autowired
	private SessionFactory sessionFactory;
	
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
//		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);
//		if (null == dbProfile) {
//			shopProfileRepository.save(defaultProfile());
//		}
	}

	public ShopProfile getShopProfileFromSession() {
		hibernateSession = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = hibernateSession.beginTransaction();
			ShopProfile profile = (ShopProfile) hibernateSession.createCriteria(ShopProfile.class)
					.add(Restrictions.naturalId().set("martCode", martCode)).list().get(0);
			System.out.println("++++++++++SHOP PROFILE: " + profile);
			tx.commit();
			return profile;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			return null;
		} finally {

			hibernateSession.close();
		}
	}

	public ShopProfile getProfile() {
//		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);

		return getShopProfileFromSession();// (ShopProfile) EntityUtil.validateDefaultValue(dbProfile);
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
				"Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.");
		return profile;
	}

	public static UserTempRequest getUserTemporaryData(String key) {
		if (userTemporaryData == null) {
			userTemporaryData = new HashMap<>();
		}
		return userTemporaryData.get(key);
	}

	public static Map<String, UserTempRequest> putUserTempData(String key, UserTempRequest userData) {
		if (userTemporaryData == null) {
			userTemporaryData = new HashMap<>();
		}
		userTemporaryData.put(key, userData);
		return userTemporaryData;
	}

}
