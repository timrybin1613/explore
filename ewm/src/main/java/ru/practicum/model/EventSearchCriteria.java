package ru.practicum.model;

import lombok.Data;
import ru.practicum.dto.event.EventPublicSearchParams;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class EventSearchCriteria {
    private final String text;
    private final Boolean textEmpty;
    private final List<Long> categories;
    private final Boolean categoriesEmpty;
    private final Boolean paid;
    private final Boolean paidNotSet;
    private final LocalDateTime rangeStart;
    private final Boolean rangeStartNotSet;
    private final LocalDateTime rangeEnd;
    private final Boolean rangeEndNotSet;
    private final Boolean onlyAvailable;

    public EventSearchCriteria(EventPublicSearchParams params) {
        this.text = params.getText() == null ? "" : params.getText();

        this.textEmpty = text.isEmpty();

        this.categories = params.getCategories() == null
                ? Collections.emptyList()
                : params.getCategories();

        this.categoriesEmpty = categories.isEmpty();

        this.paid = params.getPaid();

        this.paidNotSet = paid == null;

        this.rangeStart = params.getRangeStart();

        this.rangeStartNotSet = rangeStart == null;

        this.rangeEnd = params.getRangeEnd();

        this.rangeEndNotSet = rangeEnd == null;

        this.onlyAvailable = params.getOnlyAvailable();
    }
}