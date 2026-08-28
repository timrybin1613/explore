package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long id, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("""
            select e
            from Event e
            where (:usersEmpty = true or e.initiator.id in :users)
              and (:statesEmpty = true or e.state in :states)
              and (:categoriesEmpty = true or e.category.id in :categories)
              and (:rangeStartNotSet = true or e.eventDate >= :rangeStart)
              and (:rangeEndNotSet = true or e.eventDate <= :rangeEnd)
            """)
    List<Event> search(
            @Param("users") List<Long> users,
            @Param("usersEmpty") boolean usersEmpty,
            @Param("states") List<EventState> states,
            @Param("statesEmpty") boolean statesEmpty,
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeStartNotSet") boolean rangeStartNotSet,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("rangeEndNotSet") boolean rangeEndNotSet,
            Pageable pageable);

    @Query("""
            select e
            from Event e
            where e.state = ru.practicum.model.EventState.PUBLISHED
              and (:textEmpty = true
                   or lower(e.annotation) like lower(concat('%', :text, '%'))
                   or lower(e.description) like lower(concat('%', :text, '%'))
                   )
              and (:categoriesEmpty = true or e.category.id in :categories)
              and (:paidNotSet = true or e.paid = :paid)
              and (:rangeStartNotSet = true or e.eventDate >= :rangeStart)
              and (:rangeEndNotSet = true or e.eventDate <= :rangeEnd)
              and (:onlyAvailable = false
                   or e.participantLimit = 0
                   or (select count(r.id)
                       from Request r
                       where r.event.id = e.id
                       and r.status = ru.practicum.model.RequestStatus.CONFIRMED) < e.participantLimit
                   )
              and (:rangeStartNotSet = false
                   or :rangeEndNotSet = false
                   or e.eventDate > :now)
            """)
    List<Event> searchPublicPageable(
            @Param("text") String text,
            @Param("textEmpty") boolean textEmpty,
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("paid") Boolean paid,
            @Param("paidNotSet") boolean paidNotSet,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeStartNotSet") boolean rangeStartNotSet,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("rangeEndNotSet") boolean rangeEndNotSet,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            select e
            from Event e
            where e.state = ru.practicum.model.EventState.PUBLISHED
              and (:textEmpty = true
                   or lower(e.annotation) like lower(concat('%', :text, '%'))
                   or lower(e.description) like lower(concat('%', :text, '%'))
                   )
              and (:categoriesEmpty = true or e.category.id in :categories)
              and (:paidNotSet = true or e.paid = :paid)
              and (:rangeStartNotSet = true or e.eventDate >= :rangeStart)
              and (:rangeEndNotSet = true or e.eventDate <= :rangeEnd)
              and (:onlyAvailable = false
                   or e.participantLimit = 0
                   or (select count(r.id)
                       from Request r
                       where r.event.id = e.id
                       and r.status = ru.practicum.model.RequestStatus.CONFIRMED) < e.participantLimit
                   )
              and (:rangeStartNotSet = false
                   or :rangeEndNotSet = false
                   or e.eventDate > :now)
            """)
    List<Event> searchPublic(
            @Param("text") String text,
            @Param("textEmpty") boolean textEmpty,
            @Param("categories") List<Long> categories,
            @Param("categoriesEmpty") boolean categoriesEmpty,
            @Param("paid") Boolean paid,
            @Param("paidNotSet") boolean paidNotSet,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeStartNotSet") boolean rangeStartNotSet,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("rangeEndNotSet") boolean rangeEndNotSet,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("now") LocalDateTime now
    );

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query("""
            select e
            from Event e
            where e.initiator.id in :ids
              and e.state = ru.practicum.model.EventState.PUBLISHED
              and e.eventDate > :now
            """)
    List<Event> findActualEventsByInitiatorIds(@Param("ids") List<Long> ids,
                                               @Param("now") LocalDateTime now,
                                               Pageable pageable);
}
