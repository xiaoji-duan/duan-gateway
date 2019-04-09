package com.xiaoji.duan.zuulserver.service;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class MessageQueueService {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	public void performance(String prefix, Long timestamp, Long costs, Map<String, Object> performance) {
		try {
			Destination dest = new ActiveMQQueue("aak");
	
			Map<String, Object> content = new HashMap<String, Object>();
			content.put("context", JSONObject.toJSON(performance));
			content.put("saprefix", "zul");
			content.put("collection", "zuulserver_performance");
			content.put("timestamp", timestamp);
			content.put("costs", costs);
			content.put("trigger", prefix);

			Map<String, Object> body = new HashMap<String, Object>();
			body.put("body", content);
			
			jmsMessagingTemplate.convertAndSend(dest, body);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}
