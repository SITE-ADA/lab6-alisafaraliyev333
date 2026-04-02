package az.edu.ada.wm2.lab6.model.mapper;

import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.dto.ProductResponseDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity → Response DTO
    @Mapping(target = "categoryNames", source = "categories")
    ProductResponseDto toResponseDto(Product product);

    // Request DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Product toEntity(ProductRequestDto dto);

    // Custom mapping: List<Category> → List<String>
    default List<String> mapCategoriesToNames(List<Category> categories) {
        if (categories == null) return List.of();

        return categories.stream()
                .map(Category::getName)
                .toList();
    }
}
