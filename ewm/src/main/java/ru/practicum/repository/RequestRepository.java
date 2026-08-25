package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            select
                r.event.id as eventId,
                count(r.id) as requestCount
            from Request r
            where r.status = :status
              and r.event.id in :eventIds
            group by r.event.id
            """)
    List<RequestCountProjection> getRequestCountsByEventIds(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status
    );

    List<Request> findByEventId(Long eventId);

    List<Request> findAllByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);
}