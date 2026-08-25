package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PageUtils {

    private PageUtils() {
    }

    public static Pageable fromOffset(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
