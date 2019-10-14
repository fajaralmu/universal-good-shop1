package com.fajar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.dto.Physical;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.Entity;
import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.parameter.EntityParameter;
import javax.annotation.PostConstruct;
@Service
public class RealtimeService {
	Logger log = LoggerFactory.getLogger(RealtimeService.class);
	
	private Integer bonusCount=0;
	private List<Entity> entities = new ArrayList<>();
	private List<Entity> layouts = new ArrayList<>();
	private Random random = new Random();
	private Long currentTime = new Date().getTime();
	private Boolean isRegistering = false;
	private Long deltaTime= 8000L;
	
	@Autowired
	private SimpMessagingTemplate webSocket;
	@Autowired
	private LayoutService layoutService;
	
	public RealtimeService() {
		log.info("-----------------REALTIME SERVICE-------------------");
		startThread();
		
	}
	
	@PostConstruct
	private void loadLayout() {
		List<Entity> layouts = layoutService.getLayouts();
		this.layouts.addAll(layouts);
	}
	
	
	
	public List<Entity> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<Entity> layouts) {
		this.layouts = layouts;
	}

	private void startThread() {
		currentTime = new Date().getTime();
		Thread thread  = new Thread(new Runnable() {
			
			@Override
			public void run() {
			 while(true) {
					Long systemDate = new Date().getTime();
					Long delta =systemDate - currentTime;
				 	if(delta >= deltaTime && isRegistering == false) {
						addBonusLife();
						currentTime=systemDate;
					}
				}
			}
		});
		thread.start();
	}
	
	public synchronized void addUser(Entity user) {
		entities.add(user);
	}
	
	public List<Entity> getUsers(){
		return entities;
	}
	
	private boolean intersectLayout(Entity player) {
		for (Entity layoutItem : layouts) {
			if(Physical.intersect(player, layoutItem))
				return true;
		}
		return false;
	}
	
	public Entity getUser(Integer id) {
		for(Entity user:entities) {
			if(user.getId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public Entity getUser(String name) {
		for(Entity user:entities) {
			if(user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	
	public synchronized RealtimeResponse registerUser(HttpServletRequest request) {
		isRegistering=true;
		RealtimeResponse responseObject = new RealtimeResponse();
		String name =request.getParameter("name");
		
		Entity user;
		if(getUser(name)!=null) {
//			responseObject.setResponseCode("01");
//			responseObject.setResponseMessage("Please choose another name!");
//			return responseObject;
			user =getUser(name);
		}else {
			user = new Entity(random.nextInt(100),name,new Date());
			Physical entity = new Physical();
			entity.setX(10);
			entity.setY(10);
			entity.setColor("rgb("+random.nextInt(200)+","+random.nextInt(200)+","+random.nextInt(200)+")");
			user.setPhysical(entity);
			
		}
		responseObject.setResponseCode("00");
		responseObject.setResponseMessage("OK");
		
		
		addUser(user);
		responseObject.setEntity(user);
		responseObject.setEntities(getUsers());
		isRegistering = false;
		return responseObject;
	}
	
	public void removePlayer(Integer id) {
		for (Entity realtimeUser : entities) {
			if(realtimeUser.getId().equals(id)) {
				entities.remove(realtimeUser);
				break;
			}
		}
	}
	
	public void addBonusLife() {
		Random rand = new Random();
		Entity bonus = new Entity();
		bonus.setId(rand.nextInt(101010)+1);
		bonus.setActive(true);
		bonus.setName("Extra Life "+bonus.getId());
		bonus.setLife(rand.nextInt(9)+1);
		Physical entity = new Physical();
		Integer x = rand.nextInt(EntityParameter.WIN_W-entity.getW());
		Integer y = rand.nextInt(EntityParameter.WIN_H-entity.getH());
		entity.setRole(EntityParameter.ROLE_BONUS_LIFE);
		entity.setPeriod(10000L);
		entity.setX(x);
		entity.setY(y);
		bonus.setPhysical(entity);
		removeByRole(EntityParameter.ROLE_BONUS_LIFE);
		if(intersectLayout(bonus)) {
			return;
		}
		entities.add(bonus);
		bonusCount++;
		RealtimeResponse response = new  RealtimeResponse("00","OK");
		response.setEntities(entities);
		log.info("..............Adding new Bonus");
		
		webSocket.convertAndSend("/wsResp/players", response);
		
	}
	
	private synchronized void removeByRole(Integer role) {
		List<Entity> playerList = new ArrayList<>();
		playerList.addAll(entities);
		for(Entity player:playerList) {
			if(player.getPhysical().getRole().equals(role)) {
				removePlayer(player.getId());
			}
		}
	}

	public RealtimeResponse disconnectUser(RealtimeRequest request) {
		Integer userId = request.getEntity().getId();
		Entity user = getUser(userId);
		log.info("REQ: {}",request);
		if(user == null) {
			RealtimeResponse response = new RealtimeResponse("01","Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}
		removePlayer(userId);
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setEntity(user);
		response.setEntities(entities);
		response.setMessage(new  OutputMessage(user.getName(), "Good bye! i'm leaving now", new Date().toString()));
		return response;
	}
	
	public RealtimeResponse connectUser(RealtimeRequest request) {
		 Integer userId = request.getEntity().getId();
		Entity user = getUser(userId);
		log.info("REQ: {}",request);
		if(user == null) {
			RealtimeResponse response = new RealtimeResponse("01","Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setEntity(user);
		response.setMessage(new  OutputMessage(user.getName(), "HI!, i'm joining conversation!", new Date().toString()));
		return response;
	}
	
	public RealtimeResponse addEntity(RealtimeRequest request) {
		 
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setEntities(entities);
		 return response;
	}

	public RealtimeResponse move(RealtimeRequest request) {
		RealtimeResponse response = new RealtimeResponse("00","OK");
		for(Entity entity:entities) {
			entity.getPhysical().setLastUpdated(new Date());
			if(entity.getId().equals(request.getEntity().getId())) {
				
				entity.setPhysical(request.getEntity().getPhysical());
				entity.setMissiles(request.getEntity().getMissiles());
				entity.setLife(request.getEntity().getLife());
				entity.setActive(request.getEntity().isActive());
			}
		}
		
		
		response.setEntities(entities);
		return response;
	}
	
	
}
