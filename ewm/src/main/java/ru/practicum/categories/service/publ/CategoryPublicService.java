package ru.practicum.categories.service.publ;

import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;

import java.util.List;

@Service
public interface CategoryPublicService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
