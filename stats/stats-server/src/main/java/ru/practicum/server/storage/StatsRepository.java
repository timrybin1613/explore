package ru.practicum.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT e.app as app,
                   e.uri as uri,
                   COUNT(e) as hits
            FROM Hit e
            WHERE e.timestamp BETWEEN :start AND :end
                AND (:uris IS NULL OR e.uri IN :uris)
            GROUP BY e.app, e.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> getStats(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    @Query("""
            SELECT e.app as app,
                   e.uri as uri,
                   COUNT(DISTINCT e.ip) as hits
            FROM Hit e
            WHERE e.timestamp BETWEEN :start AND :end
                AND (:uris IS NULL OR e.uri IN :uris)
            GROUP BY e.app, e.uri
            ORDER BY hits DESC
            """)
    List<ViewStatsProjection> getStatsUniqueIp(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);

}
