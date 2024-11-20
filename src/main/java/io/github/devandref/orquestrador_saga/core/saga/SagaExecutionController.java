package io.github.devandref.orquestrador_saga.core.saga;

import io.github.devandref.orquestrador_saga.config.exeception.ValidationException;
import io.github.devandref.orquestrador_saga.core.dto.Event;
import io.github.devandref.orquestrador_saga.core.enums.ETopic;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

import static io.github.devandref.orquestrador_saga.core.saga.SagaHandler.*;
import static java.lang.String.format;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    public ETopic getNextTopic(Event event) {
        if (ObjectUtils.isEmpty(event.getSource()) || ObjectUtils.isEmpty(event.getStatus())) {
            throw new ValidationException("Source and status must be informed.");
        }
        var topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private ETopic findTopicBySourceAndStatus(Event event) {
        return (ETopic) (Arrays.stream(SAGA_HANDLER)
                .filter(row -> isEventSourceAndStatusValid(event, row))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topic not found!")));
    }

    private Boolean isEventSourceAndStatusValid(Event event, Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, ETopic topic) {
        var sagaId = createSagaId(event);
        var source = event.getSource();

        switch (event.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}", source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}", source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | SENDING ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}", source, topic, sagaId);
        }
    }

    private String createSagaId(Event event) {
        return format("ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s",
                event.getPayload().getId(), event.getTransactionalId(), event.getId());
    }

}
