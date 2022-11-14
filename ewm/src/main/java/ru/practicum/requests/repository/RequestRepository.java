package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.requests.entity.Request;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    Optional<Long> countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

}
