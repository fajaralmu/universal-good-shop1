//package com.fajar.websocket;
//
//import java.util.Date;
//import java.util.Random;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.messaging.core.MessageSendingOperations;
//import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RandomDataGenerator implements
//    ApplicationListener<BrokerAvailabilityEvent> {
//
//    private final MessageSendingOperations<String> messagingTemplate;
//
//    @Autowired
//    public RandomDataGenerator(
//        final MessageSendingOperations<String> messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @Override
//    public void onApplicationEvent(final BrokerAvailabilityEvent event) {
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void sendDataUpdates() {
//    //System.out.println("send Update");
//        this.messagingTemplate.convertAndSend(
//            "/wsResp/data", new Random().nextInt(100));
//
//    }
//    
//    @Scheduled(fixedDelay = 1000)
//    public void sendTime() {
//    //System.out.println("send Update");
//        this.messagingTemplate.convertAndSend(
//            "/wsResp/time", new Date().toString());
//
//    }
//}