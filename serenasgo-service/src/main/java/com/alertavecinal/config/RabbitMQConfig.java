package com.alertavecinal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "alertaVecinal.exchange";

    public static final String QUEUE_INCIDENTE_CREADO = "incidente.creado";
    public static final String QUEUE_INCIDENTE_ASIGNADO = "incidente.asignado";
    public static final String QUEUE_INCIDENTE_ACTUALIZADO = "incidente.actualizado";
    public static final String QUEUE_INCIDENTE_CERRADO = "incidente.cerrado";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queueIncidenteCreado() {
        return new Queue(QUEUE_INCIDENTE_CREADO, true);
    }

    @Bean
    public Queue queueIncidenteAsignado() {
        return new Queue(QUEUE_INCIDENTE_ASIGNADO, true);
    }

    @Bean
    public Queue queueIncidenteActualizado() {
        return new Queue(QUEUE_INCIDENTE_ACTUALIZADO, true);
    }

    @Bean
    public Queue queueIncidenteCerrado() {
        return new Queue(QUEUE_INCIDENTE_CERRADO, true);
    }

    @Bean
    public Binding bindingIncidenteCreado(Queue queueIncidenteCreado, TopicExchange exchange) {
        return BindingBuilder.bind(queueIncidenteCreado).to(exchange).with("incidente.creado");
    }

    @Bean
    public Binding bindingIncidenteAsignado(Queue queueIncidenteAsignado, TopicExchange exchange) {
        return BindingBuilder.bind(queueIncidenteAsignado).to(exchange).with("incidente.asignado");
    }

    @Bean
    public Binding bindingIncidenteActualizado(Queue queueIncidenteActualizado, TopicExchange exchange) {
        return BindingBuilder.bind(queueIncidenteActualizado).to(exchange).with("incidente.actualizado");
    }

    @Bean
    public Binding bindingIncidenteCerrado(Queue queueIncidenteCerrado, TopicExchange exchange) {
        return BindingBuilder.bind(queueIncidenteCerrado).to(exchange).with("incidente.cerrado");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
