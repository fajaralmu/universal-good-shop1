package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.EventMessagingService;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.RealtimeService2;

@CrossOrigin
@RestController
public class SocketController {
	Logger log = LoggerFactory.getLogger(SocketController.class);
//	@Autowired
//	private SimpMessagingTemplate webSocket;
	@Autowired
	RealtimeService2 realtimeUserService;
	@Autowired
	private EventMessagingService eventMessagingService;
	 
	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}

	@PostConstruct
	public void init() { 
	
			LogProxyFactory.setLoggers(this);
		 
	}

	@PostMapping(value = "/api/stream/disconnect", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse getRequestId(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		realtimeUserService.disconnectLiveStream(request);
		return new WebResponse();
	}

	

	@GetMapping(value = "/api/kafkasend/{msg}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse kafkasend(@PathVariable(name = "msg") String message, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse)  { 
	 
		String success = "True";
		try{
			eventMessagingService.sendEvent("product", "TEST_KEY_"+message, "TEST_VALUE_"+message);
		}catch (Exception e) {
			e.printStackTrace();
			success = "False";
		}
		
		return new WebResponse("00", success);
	}
 
	
	// @MessageMapping("/move")
//	//@SendTo("/wsResp/players")
//	public RealtimeResponse join2( RealtimeResponse response) throws IOException {
//		webSocket.convertAndSend("/wsResp/players", response);
//		return response;
//	}
//	
//	@MessageMapping("/addUser")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse join( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.connectUser(request);
//	}
//	
//	@MessageMapping("/addEntity")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse addEntity( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.addEntity(request);
//	}
//	
//	@MessageMapping("/move")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse move( RealtimeRequest request) throws IOException {
//		log.info("MOVE: {},",request);
//		return realtimeUserService.move(request);
//	}
//	
//	@MessageMapping("/leave")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse leave( RealtimeRequest request) throws IOException {
//		
//		return realtimeUserService.disconnectUser(request);
//	}

//	@MessageMapping("/chat")
//	@SendTo("/wsResp/players")
//	public RealtimeResponse send(Message message){
//		RealtimeResponse response = new RealtimeResponse();
//		System.out.println("Message > "+message);
//	    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//	    OutputMessage msg =new  OutputMessage(message.getSender(), message.getText(), time);
//	    System.out.println("Output > "+msg);
//	    response.setMessage(msg);
//	    return response;
//	}
	@MessageMapping("/stream")
	public WebResponse leave(WebRequest request) throws IOException {

		return realtimeUserService.stream(request);
	}

}
