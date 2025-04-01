package com.example.notification_service;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Autowired
    NotificationServiceApplication nsa;
    @Bean
    public Queue paymentQueue() {
        return new Queue("notification_payment_queue",true,false,false); 
    }

    @Bean
    public Queue enrollmentQueue() {
        return new Queue("notification_course_queue",true,false,false); 
    }
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("rabbitmq");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange("notification_payment_queue_exchange",true,false);
    }
    @Bean
    public TopicExchange exchange1(){
        return new TopicExchange("notification_course_queue_exchange",true,false);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(paymentQueue()).to(exchange()).with("notification_payment_key");
    }

    @Bean
    public Binding binding1(){
        return BindingBuilder.bind(enrollmentQueue()).to(exchange1()).with("notification_course_key");
    }

   
}