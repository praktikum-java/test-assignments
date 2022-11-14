package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.entity.Statistic;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    Optional<Statistic> findByUri(String uri);

    Optional<Long> countByUri(String uri);

    @Query(value =
            "SELECT count(distinct (additional_fields->>'ip'))  " +
                    "FROM statistics " +
                    "WHERE uri = ?1 " +
                    "AND additional_fields->>'app' = ?2 " +
                    "AND (cast(created_at as date)) BETWEEN ?3 AND ?4", nativeQuery = true)
    Long getStatisticWithUniqueIp(String uri, String app, LocalDateTime start, LocalDateTime end);


    @Query(value =
            "SELECT count(id)  " +
                    "FROM statistics " +
                    "WHERE uri = ?1 " +
                    "AND additional_fields->>'app' = ?2 " +
                    "AND (cast(created_at as date)) BETWEEN ?3 AND ?4", nativeQuery = true)
    Long getStatisticWithoutUniqueIp(String uri, String app, LocalDateTime start, LocalDateTime end);
}
