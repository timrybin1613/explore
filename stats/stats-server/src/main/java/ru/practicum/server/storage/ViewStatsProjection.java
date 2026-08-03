package ru.practicum.server.storage;

public interface ViewStatsProjection {
    String getApp();

    String getUri();

    Integer getHits();
}
