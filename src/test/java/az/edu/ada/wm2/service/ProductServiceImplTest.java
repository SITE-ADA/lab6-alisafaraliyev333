package az.edu.ada.wm2.lab6.service;

import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.model.dto.ProductRequestDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.model.mapper.ProductMapper;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Product product = productMapper.toEntity(dto);

      
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            product.setCategories(categories);
        }

        Product saved = productRepository.save(product);
        return productMapper.toResponseDto(saved);
    }

    @Override
    public ProductResponseDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.toResponseDto(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Update fields
        if (dto.getProductName() != null) existing.setProductName(dto.getProductName());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getExpirationDate() != null) existing.setExpirationDate(dto.getExpirationDate());

        // Update categories if provided
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            existing.setCategories(categories);
        }

        Product saved = productRepository.save(existing);
        return productMapper.toResponseDto(saved);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(existing);
    }

    @Override
    public List<ProductResponseDto> getProductsExpiringBefore(LocalDate date) {
        return productRepository.findByExpirationDateBefore(date)
                .stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
