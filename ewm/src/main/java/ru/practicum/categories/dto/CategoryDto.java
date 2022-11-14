package ru.practicum.categories.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO категории")
public class CategoryDto {
    @NotNull
    @Schema(description = "Идентификатор категории")
    private Long id;
    @NotBlank
    @Schema(description = "Название категории")
    private String name;
}
