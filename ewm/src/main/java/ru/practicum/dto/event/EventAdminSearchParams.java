package ru.practicum.dto.event;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventAdminSearchParams {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    @Min(0)
    private Integer from = 0;
    @Min(1)
    private Integer size = 10;
}
