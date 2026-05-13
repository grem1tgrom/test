package ru.practicum.ewm.tesh;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import ru.practicum.grpc.stats.event.ActionTypeProto;
import ru.practicum.grpc.stats.event.UserActionControllerGrpc;
import ru.practicum.grpc.stats.event.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
public class StatsClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionStub;

    @Retryable(
            retryFor = {StatusRuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void recordView(Long userId, Long eventId) {
        sendAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
        log.info("Отправлено действие VIEW: userId={}, eventId={}", userId, eventId);
    }

    @Retryable(
            retryFor = {StatusRuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void recordRegister(Long userId, Long eventId) {
        sendAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
        log.info("Отправлено действие REGISTER: userId={}, eventId={}", userId, eventId);
    }

    @Retryable(
            retryFor = {StatusRuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void recordLike(Long userId, Long eventId) {
        sendAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
        log.info("Отправлено действие LIKE: userId={}, eventId={}", userId, eventId);
    }

    private void sendAction(Long userId, Long eventId, ActionTypeProto actionType) {
        if (userId == null) {
            throw new IllegalArgumentException("userId не может быть null");
        }
        if (eventId == null) {
            throw new IllegalArgumentException("eventId не может быть null");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("actionType не может быть null");
        }

        try {
            UserActionProto userAction = UserActionMapper.toProto(userId, eventId, actionType, Instant.now());
            userActionStub.collectUserAction(userAction);
            log.info("Действие пользователя успешно отправлено: userId={}, eventId={}, actionType={}",
                    userId, eventId, actionType);
        } catch (StatusRuntimeException e) {
            log.error("Не удалось отправить действие пользователя: userId={}, eventId={}, actionType={}, status={}, message={}",
                    userId, eventId, actionType, e.getStatus(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Не удалось отправить действие пользователя: userId={}, eventId={}, actionType={}, error={}",
                    userId, eventId, actionType, e.getMessage(), e);
            throw new RuntimeException("Ошибка при отправке действия пользователя", e);
        }
    }
}