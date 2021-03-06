package com.fajar.shoppingmart.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.service.EventMessagingService;
import com.fajar.shoppingmart.service.LogProxyFactory;
import com.fajar.shoppingmart.service.RealtimeService2;

@CrossOrigin
@RestController
public class SocketController {
	Logger log = LoggerFactory.getLogger(SocketController.class);

	@Autowired
	private RealtimeService2 realtimeUserService;
	@Autowired
	private EventMessagingService eventMessagingService;

	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	

	@GetMapping(value = "/api/kafkasend/{msg}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse kafkasend(@PathVariable(name = "msg") String message, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {

		String success = "True";
		try {
			eventMessagingService.sendEvent("TEST_KEY_" + message, "TEST_VALUE_" + message);
		} catch (Exception e) {
			e.printStackTrace();
			success = "False";
		}

		return new WebResponse("00", success);
	}
 
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

}
