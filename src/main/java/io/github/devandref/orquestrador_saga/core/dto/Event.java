package io.github.devandref.orquestrador_saga.core.dto;

import io.github.devandref.orquestrador_saga.core.enums.EEventSource;
import io.github.devandref.orquestrador_saga.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String id;
    private String transactionalId;
    private String orderId;
    private Order payload;
    private EEventSource source;
    private ESagaStatus status;
    private List<History> eventHistory;
    private LocalDateTime createdAt;

    public void addHistory(History history) {
        if(ObjectUtils.isEmpty(eventHistory)) {
            this.eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }

}
