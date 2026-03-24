package rest.controller;

import dao.ICategoryDao;
import dao.impl.CategoryDaoImpl;
import dto.CategoryDto;
import dto.mapper.CategoryMapper;
import entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.BadRequestException;
import rest.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des catégories
 * Base URL: /api/categories
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Categories", description = "API de gestion des catégories de concerts")
public class CategoryController {

    private final ICategoryDao categoryDao;

    public CategoryController() {
        this.categoryDao = new CategoryDaoImpl();
    }

    // ============================================================
    // ENDPOINTS CRUD
    // ============================================================

    /**
     * GET /api/categories
     * Liste toutes les catégories
     */
    @GET
    @Operation(summary = "Liste toutes les catégories")
    public Response getAllCategories() {
        List<Category> categories = categoryDao.findAll();
        List<CategoryDto> dtos = categories.stream()
                .map(category -> {
                    CategoryDto dto = CategoryMapper.toDto(category);
                    dto.setEventCount(categoryDao.getEventCount(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/categories/{id}
     * Récupère une catégorie par son ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère une catégorie par ID")
    public Response getCategoryById(
            @Parameter(description = "ID de la catégorie", required = true)
            @PathParam("id") Long id) {
        
        Category category = categoryDao.findOne(id);
        
        if (category == null) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        
        CategoryDto dto = CategoryMapper.toDto(category);
        dto.setEventCount(categoryDao.getEventCount(id));
        
        return Response.ok(dto).build();
    }

    /**
     * POST /api/categories
     * Crée une nouvelle catégorie
     */
    @POST
    @Operation(summary = "Crée une nouvelle catégorie")
    public Response createCategory(CategoryDto dto) {
        // Validation
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Le nom est obligatoire");
        }
        
        categoryDao.findByName(dto.getName()).ifPresent(existing -> {
            throw new BadRequestException("Une catégorie avec ce nom existe déjà");
        });
        
        Category category = CategoryMapper.toEntity(dto);
        categoryDao.save(category);
        
        return Response.status(Response.Status.CREATED)
                .entity(CategoryMapper.toDto(category))
                .build();
    }

    /**
     * PUT /api/categories/{id}
     * Met à jour une catégorie
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Met à jour une catégorie")
    public Response updateCategory(
            @PathParam("id") Long id,
            CategoryDto dto) {
        
        Category existing = categoryDao.findOne(id);
        
        if (existing == null) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        
        Category updated = categoryDao.update(existing);
        
        return Response.ok(CategoryMapper.toDto(updated)).build();
    }

    /**
     * DELETE /api/categories/{id}
     * Supprime une catégorie
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime une catégorie")
    public Response deleteCategory(@PathParam("id") Long id) {
        Category category = categoryDao.findOne(id);
        
        if (category == null) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        
        categoryDao.deleteById(id);
        return Response.noContent().build();
    }

    // ============================================================
    // ENDPOINTS MÉTIER
    // ============================================================

    /**
     * GET /api/categories/name/{name}
     * Recherche une catégorie par nom
     */
    @GET
    @Path("/name/{name}")
    @Operation(summary = "Recherche par nom")
    public Response getCategoryByName(@PathParam("name") String name) {
        Category category = categoryDao.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", name));
        
        CategoryDto dto = CategoryMapper.toDto(category);
        dto.setEventCount(categoryDao.getEventCount(category.getId()));
        
        return Response.ok(dto).build();
    }

    /**
     * GET /api/categories/popular
     * Catégories les plus populaires (avec le plus d'événements)
     */
    @GET
    @Path("/popular")
    @Operation(summary = "Catégories populaires")
    public Response getPopularCategories(
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        List<Category> categories = categoryDao.findPopularCategories();
        List<CategoryDto> dtos = categories.stream()
                .limit(limit)
                .map(category -> {
                    CategoryDto dto = CategoryMapper.toDto(category);
                    dto.setEventCount(categoryDao.getEventCount(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/categories/search
     * Recherche de catégories
     */
    @GET
    @Path("/search")
    @Operation(summary = "Recherche de catégories")
    public Response searchCategories(
            @QueryParam("keyword") String keyword,
            @QueryParam("hasEvents") @DefaultValue("false") boolean hasEvents) {
        
        List<Category> categories;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            categories = categoryDao.findByNameContaining(keyword);
        } else {
            categories = categoryDao.findByCriteria(null, hasEvents);
        }
        
        List<CategoryDto> dtos = categories.stream()
                .map(category -> {
                    CategoryDto dto = CategoryMapper.toDto(category);
                    dto.setEventCount(categoryDao.getEventCount(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/categories/with-min-events
     * Catégories avec au moins N événements
     */
    @GET
    @Path("/with-min-events")
    @Operation(summary = "Catégories avec un nombre minimum d'événements")
    public Response getCategoriesWithMinEvents(
            @QueryParam("min") @DefaultValue("1") int minEvents) {
        
        List<Category> categories = categoryDao.findCategoriesWithMinEvents(minEvents);
        List<CategoryDto> dtos = categories.stream()
                .map(category -> {
                    CategoryDto dto = CategoryMapper.toDto(category);
                    dto.setEventCount(categoryDao.getEventCount(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
        
        return Response.ok(dtos).build();
    }
}