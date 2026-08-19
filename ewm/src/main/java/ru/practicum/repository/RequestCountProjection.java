package ru.practicum.repository;

public interface RequestCountProjection {
    Long getEventId();

    Long getRequestCount();
}