package com.atguigu.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {
//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void handle(Message message){
//
//    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Exchange stockEventExchange(){
      return   new TopicExchange("stock-event-exchange",true,false);
    }
    @Bean
    public Queue stockReleaseStockQueue(){
        return  new Queue("stock.release.stock.queue",true,false,false);
    }
    @Bean
    public Queue stockDelayQueue(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","stock-event-exchange");//stockDelayQueue里面的消息死了以后，交给stock-event-exchange路由
        arguments.put("x-dead-letter-routing-key","stock.release.order");//stockDelayQueue里面的消息死了以后，用的路由键是stock.release.order
        arguments.put("x-message-ttl",10000);//消息的存活时间
        return  new Queue("stock.delay.queue",true,false,false,arguments);
    }
    @Bean
    public Binding stockReleaseBinding(){
        return  new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }
    @Bean
    public Binding stockLockedBinding(){
        return  new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }
}
