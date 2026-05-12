package ru.practicum.ewm.feign.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.constant.RequestStatus;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "request-service", fallbackFactory = RequestClientFallbackFactory.class)
public interface RequestClient {
    @GetMapping("/requests/{id}/{status}")
    Long countByEventIdAndStatus(@PathVariable("id") Long id, @PathVariable("status") RequestStatus status);

    @GetMapping("/requests/count")
    Map<Long, Long> countConfirmedByEventIds(@RequestParam("eventIds") Set<Long> eventIds);
}