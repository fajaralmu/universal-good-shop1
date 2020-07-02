package com.fajar.shoppingmart.service;

import static com.fajar.shoppingmart.util.CollectionUtil.emptyArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.ShopProfile;
import com.fajar.shoppingmart.repository.ShopProfileRepository;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is autowired via XML
 * 
 * @author Republic Of Gamers
 *
 */

@Data
@Slf4j
public class WebConfigService {

	@Autowired
	private ShopProfileRepository shopProfileRepository;
	@Autowired
	private ApplicationContext applicationContext;

	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String reportPath;
	private String martCode;

	private List<JpaRepository<?, ?>> jpaRepositories = new ArrayList<>();
	private List<Type> entityClassess = new ArrayList<>();

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);
		if (null == dbProfile) {
			shopProfileRepository.save(defaultProfile());
		}
		getJpaReporitoriesBean();
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("========= will destroy ========");

	}

	private void getJpaReporitoriesBean() {
		log.info("//////////////GET JPA REPOSITORIES BEANS///////////////");
		jpaRepositories.clear();
		entityClassess.clear();
		String[] beanNames = applicationContext.getBeanNamesForType(JpaRepository.class);
		if (null == beanNames)
			return;

		log.info("JPA REPOSITORIES COUNT: " + beanNames.length);
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];
			JpaRepository<?, ?> beanObject = (JpaRepository<?, ?>) applicationContext.getBean(beanName);

			if (null == beanObject)
				continue;
			Class<?>[] interfaces = beanObject.getClass().getInterfaces(); 
			
			log.info("beanObject: {}", beanObject);
			if (null == interfaces)
				continue;
 
			Type type = getTypeArgument(interfaces[0], 0);
			
			entityClassess.add(type);
			jpaRepositories.add(beanObject);
			
			log.info(i + "." + beanName + ". entity type: "+ type);
		}
	}
	 
	public static void main (String[] args) throws IOException {
//		Class _class = ProductRepository.class;
//		Type[] interfaces = _class.getGenericInterfaces();
//		CollectionUtil.printArray(interfaces);
//		Type type = interfaces[0];
//		System.out.println("type: "+type);
//		ParameterizedType parameterizedType = (ParameterizedType) type;
//		log.info("parameterizedType: {}", parameterizedType );
	}
	
	private ParameterizedType getJpaRepositoryType(Class<?> _class) {
		Type[] genericInterfaces = _class.getGenericInterfaces();
		if(CollectionUtil.emptyArray(genericInterfaces)) return null;
		
		try {
			for (int i = 0; i < genericInterfaces.length; i++) {
				Type genericInterface = genericInterfaces[i];
				if(genericInterface.getTypeName().startsWith("org.springframework.data.jpa.repository.JpaRepository")) {
					return (ParameterizedType) genericInterface;
				}
			}
			return null;
		}catch (Exception e) {
			return null;
		}
	}

	private Type getTypeArgument(Class<?> _class, int argNo) {
		try {
			 
			ParameterizedType jpaRepositoryType = getJpaRepositoryType(_class);
			
			Type[] typeArguments = jpaRepositoryType.getActualTypeArguments();// type.getTypeParameters();
			CollectionUtil.printArray(typeArguments);

			if (emptyArray(typeArguments)) {
				return null;
			}

			Type typeArgument = typeArguments[argNo];
			log.debug("typeArgument: {}", typeArgument);
			return typeArgument;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	public static void main2(String[] args) throws IOException {
		String path = "C:\\Users\\Republic Of Gamers\\Documents\\ORCALE_DUMPS\\";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile()) {
				// System.out.println(path+file.getName());
				String content = readFile(path + file.getName());

				if (!file.getName().startsWith("MASTER_PFM_DETAIL")) {
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
		if (firstLine.startsWith("INSERT") == false) {
			firstLine = firstLine.substring(3, firstLine.length());
			firstLine = firstLine.replace("MMB", "DEVELOPMENT");
		}

		for (String string : lines) {
			if (string.trim().equals(";")) {
				continue;
			}

			String insertStatement = string.substring(3, string.length());
			if (insertStatement.startsWith("INSERT")) {
				string = string.substring(3, string.length());
			}

			string = string.trim();
			if (string.startsWith(",(")) {
				string = string.substring(0, string.length());
				string = firstLine + string;
			}
			if (string.endsWith(")")) {
				string += ";";
			}
			string = string.replace("MMB", "DEVELOPMENT");
			string = string.replace("VALUES,", "VALUES");
			result += "\n" + string;

			System.out.println(string);
		}
		return result;
	}

	public ShopProfile getProfile() {
		ShopProfile dbProfile = shopProfileRepository.findByMartCode(martCode);

		/* return getShopProfileFromSession(); */ return EntityUtil.validateDefaultValue(dbProfile);
	}

	private ShopProfile defaultProfile() {
		ShopProfile profile = new ShopProfile();
		profile.setName("Universal Good Shop [Generated]");
		profile.setAddress("Spring Mvc, Java Virtual Machine, Win 10 64");
		profile.setContact("087737666614");
		profile.setWebsite("http://localhost:8080/universal-good-shop");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setBackgroundUrl("DefaultBackground.BMP");
		profile.setMartCode(martCode);
		profile.setShortDescription("we provide what u need");
		profile.setColor("green");
		profile.setAbout("Nam libero tempore.");
		return profile;
	}

}
