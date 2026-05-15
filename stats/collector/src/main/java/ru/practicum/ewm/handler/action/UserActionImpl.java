package ru.practicum.ewm.handler.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.kafka.KafkaClient;
import ru.practicum.ewm.mapper.UserActionMapper;
import ru.practicum.grpc.stats.event.UserActionProto;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionImpl implements UserActionHandler {
    private final KafkaClient kafkaClient;
    private final UserActionMapper hubEventMapper;

    @Value("${topics.user-action}")
    private String topic;

    @Override
    public void handle(UserActionProto eventProto) {
        try {
            RecordMetadata metadata = kafkaClient.getProducer()
                    .send(new ProducerRecord<>(topic, hubEventMapper.toAvro(eventProto)))
                    .get(5, TimeUnit.SECONDS);

            log.info("Действие отправлено в топик: {}, partition={}, offset={}",
                    topic, metadata.partition(), metadata.offset());
        } catch (Exception e) {
            log.error("Не удалось отправить действие в Kafka topic={}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить действие в Kafka", e);
        }
    }
}