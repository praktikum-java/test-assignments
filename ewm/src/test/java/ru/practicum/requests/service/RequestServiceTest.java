package ru.practicum.requests.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapperImpl;
import ru.practicum.requests.entity.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RequestMapperImpl requestMapper;

    // getParticipationRequest
    @Test
    public void shouldGetParticipationRequest() {
        User requester = User.builder().id(1L).name("User1").email("user1@gmail.com").build();
        Event event1 = Event.builder().id(1L).title("Title1").build();
        Request request1 = Request.builder().id(1L).requester(requester).event(event1).build();
        when(requestRepository.findAllByRequesterId(1L)).thenReturn(List.of(request1));
        List<ParticipationRequestDto> result = requestService.getParticipationRequest(1L);
        ParticipationRequestDto requestDto = ParticipationRequestDto.builder()
                .id(1L).requester(1L).event(1L).build();
        List<ParticipationRequestDto> listToCheck = List.of(requestDto);
        assertEquals(listToCheck, result);
    }

    // createParticipationRequest
    @Test
    public void shouldCreateParticipationRequest() {
        User requester = User.builder().id(1L).name("User1").email("user1@gmail.com").build();
        User initiatorOfEvent = User.builder().id(2L).name("User2").email("user2@gmail.com").build();
        Event event1 = Event.builder().id(1L).title("Title1").initiator(initiatorOfEvent)
                .state(EventState.PUBLISHED).requestModeration(false).availableForRequest(true).build();
        Request request1 = Request.builder().id(1L).requester(requester).event(event1).build();
        when(requestRepository.existsByRequesterIdAndEventId(1L, 1L))
                .thenReturn(false);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(requestRepository.save(any(Request.class))).thenReturn(request1);
        ParticipationRequestDto result = requestService.createParticipationRequest(1L, 1L);
        ParticipationRequestDto requestToCheck = ParticipationRequestDto.builder()
                .id(1L).requester(1L).event(1L).build();
        assertEquals(requestToCheck, result);
    }

    // cancelParticipationRequest
    @Test
    public void shouldCancelParticipationRequest() {
        User requester = User.builder().id(1L).name("User1").email("user1@gmail.com").build();
        User initiatorOfEvent = User.builder().id(2L).name("User2").email("user2@gmail.com").build();
        Event event1 = Event.builder().id(1L).title("Title1").initiator(initiatorOfEvent)
                .state(EventState.PUBLISHED).requestModeration(false).availableForRequest(true).build();
        Request request1 = Request.builder().id(1L).requester(requester).event(event1).build();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request1));
        Request updatedRequest = Request.builder().id(1L).requester(requester).event(event1)
                .status(RequestStatus.CANCELED).build();
        when(requestRepository.save(any(Request.class))).thenReturn(updatedRequest);
        ParticipationRequestDto result = requestService.cancelParticipationRequest(1L, 1L);
        ParticipationRequestDto requestToCheck = ParticipationRequestDto.builder()
                .id(1L).requester(1L).event(1L).status(RequestStatus.CANCELED).build();
        assertEquals(requestToCheck, result);
    }

}
