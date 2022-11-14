package ru.practicum.events.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.categories.entity.Category;
import ru.practicum.events.entity.Event;
import ru.practicum.events.model.EventState;
import ru.practicum.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class EventFindSpecification {

    public static Specification<Event> specificationForPublicSearchWithDate(String text, List<Category> categories,
                                                                            Boolean paid, LocalDateTime rangeStart,
                                                                            LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                                            EventState eventState) {
        return Specification
                .where(annotationContains(text).or(descriptionContains(text)))
                .and(categoryContainsIn(categories))
                .and(isPaid(paid))
                .and(afterDate(rangeStart))
                .and(beforeDate(rangeEnd))
                .and(isAvailable(onlyAvailable))
                .and(isPublished(eventState));
    }

    public static Specification<Event> specificationForPublicSearchWithoutDate(String text, List<Category> categories,
                                                                               Boolean paid, Boolean onlyAvailable,
                                                                               EventState eventState) {
        LocalDateTime currentTime = LocalDateTime.now();
        return Specification
                .where(annotationContains(text).or(descriptionContains(text)))
                .and(categoryContainsIn(categories))
                .and(isPaid(paid))
                .and(afterCurrentTime(currentTime))
                .and(isAvailable(onlyAvailable))
                .and(isPublished(eventState));
    }

    public static Specification<Event> annotationContains(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%");
    }

    public static Specification<Event> descriptionContains(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + text.toUpperCase() + "%");
    }

    public static Specification<Event> categoryContainsIn(List<Category> categories) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("category")).value(categories);
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> afterDate(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    public static Specification<Event> beforeDate(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
    }

    public static Specification<Event> isAvailable(Boolean onlyAvailable) {
        if (onlyAvailable) {
            return Specification
                    .where(participantLimitEqualZero().or(availableForRequestIs(true)));
        } else {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0);
        }
    }

    public static Specification<Event> participantLimitEqualZero() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("participantLimit"), 0);
    }

    public static Specification<Event> availableForRequestIs(Boolean available) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("availableForRequest"), available);
    }

    public static Specification<Event> isPublished(EventState eventState) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), eventState);
    }

    public static Specification<Event> afterCurrentTime(LocalDateTime currentTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), currentTime);
    }

    public static Specification<Event> specificationForAdminSearchWithDate(List<User> users, List<EventState> states,
                                                                           List<Category> categories, LocalDateTime rangeStart,
                                                                           LocalDateTime rangeEnd) {
        return Specification
                .where(initiatorContainsIn(users))
                .and(stateContainsIn(states))
                .and(categoryContainsIn(categories))
                .and(afterDate(rangeStart))
                .and(beforeDate(rangeEnd));
    }

    public static Specification<Event> specificationForAdminSearchWithoutDate(List<User> users, List<EventState> states,
                                                                              List<Category> categories) {
        return Specification
                .where(initiatorContainsIn(users))
                .and(stateContainsIn(states))
                .and(categoryContainsIn(categories));
    }

    public static Specification<Event> initiatorContainsIn(List<User> users) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("initiator")).value(users);
    }

    public static Specification<Event> stateContainsIn(List<EventState> states) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("state")).value(states);
    }

}
