package com.sb1.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("topic-detailed").build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("topic-scoring").build();
    }

    @Bean
    public NewTopic topic3() {
        return TopicBuilder.name("topic-coverLetter").build();
    }

    @Bean
    public NewTopic topic4() {
        return TopicBuilder.name("topic-userAnswer").build();
    }
}
