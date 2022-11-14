package ru.practicum.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "statistics", schema = "public", indexes = @Index(name = "uri_index", columnList = "uri"))
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class,
        defaultForType = AdditionalFields.class
)
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uri")
    private String uri;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Type(type = "jsonb")
    @Column(name = "additional_fields", columnDefinition = "jsonb")
    private AdditionalFields additionalFields;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return Objects.equals(id, statistic.id) && Objects.equals(uri, statistic.uri) && Objects.equals(createdAt, statistic.createdAt) && Objects.equals(additionalFields, statistic.additionalFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uri, createdAt, additionalFields);
    }
}
