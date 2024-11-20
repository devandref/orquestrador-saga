package io.github.devandref.orquestrador_saga.core.service;

import io.github.devandref.orquestrador_saga.core.consumer.SagaOrchestratorConsumer;
import io.github.devandref.orquestrador_saga.core.dto.Event;
import io.github.devandref.orquestrador_saga.core.dto.History;
import io.github.devandref.orquestrador_saga.core.enums.ETopic;
import io.github.devandref.orquestrador_saga.core.producer.SagaProducer;
import io.github.devandref.orquestrador_saga.core.saga.SagaExecutionController;
import io.github.devandref.orquestrador_saga.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.github.devandref.orquestrador_saga.core.enums.EEventSource.ORCHESTRATOR;
import static io.github.devandref.orquestrador_saga.core.enums.ESagaStatus.FAIL;
import static io.github.devandref.orquestrador_saga.core.enums.ESagaStatus.SUCCESS;
import static io.github.devandref.orquestrador_saga.core.enums.ETopic.NOTIFY_ENDING;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {

    private final JsonUtil jsonUtil;
    private final SagaOrchestratorConsumer sagaOrchestratorConsumer;
    private final SagaProducer sagaProducer;
    private final SagaExecutionController sagaExecutionController;

    public void startSaga(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED!");
        addHistory(event, "Saga started!");
        sagaProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    public void finishSagaSuccess(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished successfully!");
        notifyFinishedSaga(event);
    }

    public void finishSagaFail(Event event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAIL);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished with errors!");
        notifyFinishedSaga(event);
    }

    public void continueSaga(Event event) {
        var topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sagaProducer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private ETopic getTopic(Event event) {
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String msg) {
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(msg)
                .createdAt(LocalDateTime.now())
                .build();
        event.addHistory(history);
    }

    private void notifyFinishedSaga(Event event) {
        sagaProducer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
    }

}
