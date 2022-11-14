package ru.practicum.events.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.categories.entity.Category;
import ru.practicum.compilations.entity.Compilation;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.Location;
import ru.practicum.users.entity.User;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "annotation", length = 2000, nullable = false)
    @Size(min = 20)
    private String annotation;                                  // Краткое описание
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;                                  // Категория {"id": 1, "name": "Концерты"}
    @Column(name = "description", length = 7000, nullable = false)
    @Size(min = 20)
    private String description;                                 // Полное описание события
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;            // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "location", nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "lon")),
    })
    private Location location;              // Широта и долгота места проведения события {"lat": 55.754167, "lon": 37.62}
    @Column(name = "paid", nullable = false)
    private Boolean paid;                                       // Нужно ли оплачивать участие
    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;       // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения.
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;                          // Нужна ли пре-модерация заявок на участие. Дефолтно: true
    @Column(name = "title", length = 120, nullable = false)
    @Size(min = 3)
    private String title;                                       // Заголовок
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;                          // Пользователь (краткая информация) {"id": 3, "name": "Фёдоров Матвей"};
    @Column(name = "state", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;                       // Список состояний жизненного цикла события. [PENDING, PUBLISHED, CANCELED]
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;                // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    private LocalDateTime publishedOn;              // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @ToString.Exclude
    @ManyToMany(mappedBy = "events")
    List<Compilation> compilations;                 // Подборки, в которых состоит
    @Column(name = "available_for_request")
    private Boolean availableForRequest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(annotation, event.annotation) && Objects.equals(category, event.category) && Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) && Objects.equals(location, event.location) && Objects.equals(paid, event.paid) && Objects.equals(participantLimit, event.participantLimit) && Objects.equals(requestModeration, event.requestModeration) && Objects.equals(title, event.title) && Objects.equals(initiator, event.initiator) && state == event.state && Objects.equals(createdOn, event.createdOn) && Objects.equals(publishedOn, event.publishedOn) && Objects.equals(compilations, event.compilations) && Objects.equals(availableForRequest, event.availableForRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title, initiator, state, createdOn, publishedOn, compilations, availableForRequest);
    }
}
