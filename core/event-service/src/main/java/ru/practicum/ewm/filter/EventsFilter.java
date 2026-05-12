package ru.practicum.ewm.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.constant.EventSort;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.core.annotation.DateRange;
import ru.practicum.ewm.core.annotation.DateRangeAware;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@DateRange
public class EventsFilter implements DateRangeAware {

    private String text;

    private Boolean paid;

    private List<Long> users;

    private List<String> states;

    private List<Long> categories;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private EventSort sort;

    @Override
    public LocalDateTime getStart() {
        return rangeStart;
    }

    @Override
    public LocalDateTime getEnd() {
        return rangeEnd;
    }

    public List<EventState> getStatesAsEnum() {
        if (this.states == null || this.states.isEmpty()) {
            return null;
        }
        return this.states.stream()
                .map(String::toUpperCase)
                .map(EventState::valueOf)
                .toList();
    }
}