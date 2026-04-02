package az.edu.ada.wm2.lab6.service;

import az.edu.ada.wm2.lab6.model.Category;
import az.edu.ada.wm2.lab6.model.Product;
import az.edu.ada.wm2.lab6.model.dto.CategoryRequestDto;
import az.edu.ada.wm2.lab6.model.dto.CategoryResponseDto;
import az.edu.ada.wm2.lab6.model.dto.ProductResponseDto;
import az.edu.ada.wm2.lab6.model.mapper.CategoryMapper;
import az.edu.ada.wm2.lab6.model.mapper.ProductMapper;
import az.edu.ada.wm2.lab6.repository.CategoryRepository;
import az.edu.ada.wm2.lab6.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               ProductMapper productMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public CategoryResponseDto create(CategoryRequestDto dto) {
        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toResponseDto(saved);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto addProduct(UUID categoryId, UUID productId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Link product to category (Product is owner)
        List<Category> productCategories = product.getCategories();
        if (!productCategories.contains(category)) {
            productCategories.add(category);
        }

        productRepository.save(product);

        return CategoryMapper.toResponseDto(category);
    }

    @Override
    public List<ProductResponseDto> getProducts(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return category.getProducts()
                .stream()
                .map(productMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}    private ProductMapper productMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private UUID productId;

    @Test
    void create_shouldSaveCategory() {
        CategoryRequestDto dto = new CategoryRequestDto("Food");

        Category category = new Category();
        category.setName("Food");

        when(categoryRepository.save(any())).thenReturn(category);

        CategoryResponseDto result = categoryService.create(dto);

        assertEquals("Food", result.getName());
    }

    @Test
    void getAll_shouldReturnList() {
        Category category = new Category();
        category.setName("Food");

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponseDto> result = categoryService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void addProduct_shouldLinkProductToCategory() {
        categoryId = UUID.randomUUID();
        productId = UUID.randomUUID();

        Category category = new Category();
        Product product = new Product();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        categoryService.addProduct(categoryId, productId);

        assertTrue(product.getCategories().contains(category));
        verify(productRepository).save(product);
    }

    @Test
    void getProducts_shouldReturnDtos() {
        categoryId = UUID.randomUUID();

        Category category = new Category();

        Product product = new Product();
        category.setProducts(List.of(product));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productMapper.toResponseDto(product)).thenReturn(new ProductResponseDto());

        List<ProductResponseDto> result = categoryService.getProducts(categoryId);

        assertEquals(1, result.size());
    }

    
    @Test
    void addProduct_shouldThrow_whenCategoryNotFound() {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> categoryService.addProduct(UUID.randomUUID(), UUID.randomUUID()));
    }
}
