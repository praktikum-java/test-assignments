package ru.practicum.categories.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для создания новой категории")
public class NewCategoryDto {
    @NotBlank
    @Schema(description = "Название категории")
    private String name;
}
