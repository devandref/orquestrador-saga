package io.github.devandref.orquestrador_saga.core.saga;

import static io.github.devandref.orquestrador_saga.core.enums.EEventSource.*;
import static io.github.devandref.orquestrador_saga.core.enums.ESagaStatus.*;
import static io.github.devandref.orquestrador_saga.core.enums.ETopic.*;

public final class SagaHandler {

    private SagaHandler() {
    }

    public static final Object[][] SAGA_HANDLER = {

            {ORCHESTRATOR, SUCCESS, PRODUCT_VALIDATION_SUCESS},
            {ORCHESTRATOR, FAIL, FINISH_FAIL},

            {PRODUCT_VALIDATION_SERVICE, ROLLBACK_PENDING, PRODUCT_VALIDATION_FAIL},
            {PRODUCT_VALIDATION_SERVICE, FAIL, FINISH_FAIL},
            {PRODUCT_VALIDATION_SERVICE, SUCCESS, PAYMENT_SUCCESS},

            {PAYMENT_SERVICE, ROLLBACK_PENDING, PAYMENT_FAIL},
            {PAYMENT_SERVICE, FAIL, PRODUCT_VALIDATION_FAIL},
            {PAYMENT_SERVICE, SUCCESS, INVENTORY_SUCCESS},

            {INVENTORY_SERVICE, ROLLBACK_PENDING, INVENTORY_FAIL},
            {INVENTORY_SERVICE, FAIL, PAYMENT_FAIL},
            {INVENTORY_SERVICE, SUCCESS, FINISH_SUCCESS}

    };

    public static final Integer EVENT_SOURCE_INDEX = 0;
    public static final Integer SAGA_STATUS_INDEX = 1;
    public static final Integer TOPIC_INDEX = 2;

}
