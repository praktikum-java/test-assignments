package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.events.entity.Event;
import ru.practicum.users.entity.User;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByInitiator(User initiator, Pageable pageable);

    Long countByCategoryId(Long catId);

    List<Event> findAllByIdIn(List<Long> list);
}