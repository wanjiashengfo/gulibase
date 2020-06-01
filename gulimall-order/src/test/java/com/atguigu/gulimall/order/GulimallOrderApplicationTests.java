package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {
	@Autowired
	AmqpAdmin amqpAdmin;
	@Autowired
	RabbitTemplate rabbitTemplate;
	@Test
	void createExchange() {
		DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Exchange【{}】创建成功","hello-java-exchange");
	}

	@Test
	public void createQueue(){
		Queue queue = new Queue("hello-java-queue",true,false,false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue【{}】创建成功","hello-java-queue");
	}
	@Test
	public void createBanding(){
		Binding binding = new Binding("hello-java-queue",
				Binding.DestinationType.QUEUE,
				"hello-java-exchange",
				"hello.java",null);
		amqpAdmin.declareBinding(binding);
		log.info("binding【{}】创建成功","hello-java-queue");
	}
	@Test
	public void sendMessageTest(){

		for (int i = 0; i < 10; i++) {
			OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
			reasonEntity.setId(1L);
			reasonEntity.setCreateTime(new Date());
			reasonEntity.setName("哈哈"+i);
			rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
			log.info("信息【{}】发送成功",reasonEntity);
		}
	}
}
