package at.spengergasse.Mini.persistence;

import at.spengergasse.Mini.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository Layer (Data Access Layer)
// ---------------------------------
// Handles database operations (CRUD)
// Interface-based (no implementation needed!)
// Spring Data JPA provides implementation at runtime
// Should ONLY be called from Service Layer (not directly from Controller)


// Spring Data JPA
// ---------------------------------
// Eliminates boilerplate code for database access
// No need to write SQL or implementation
// Just extend JpaRepository and get methods for free:
//
// Provided Methods (no code needed):
//   - findAll()           → SELECT * FROM todo
//   - findById(id)        → SELECT * FROM todo WHERE id = ?
//   - save(entity)        → INSERT or UPDATE
//   - deleteById(id)      → DELETE FROM todo WHERE id = ?
//   - existsById(id)      → SELECT COUNT(*) FROM todo WHERE id = ?
//   - count()             → SELECT COUNT(*) FROM todo
//   - and many more...
//
// Custom Queries (by method naming convention):
//   - findByTitle(String title)              → WHERE title = ?
//   - findByTitleContaining(String keyword)  → WHERE title LIKE %keyword%
//   - findByCreatedAtAfter(LocalDate date)   → WHERE created_at > ?
//
// Or use @Query annotation for complex queries:
//   @Query("SELECT t FROM Todo t WHERE t.title LIKE %:keyword%")
//   List<Todo> searchByKeyword(@Param("keyword") String keyword);


// JpaRepository<Entity, ID Type>
// ---------------------------------
// Generic interface with two type parameters:
//   1. Entity class (Todo)
//   2. ID type (Long)
//
// Example: JpaRepository<Todo, Long>
//   - Works with Todo entity
//   - Primary key is of type Long


// @Repository Annotation
// ---------------------------------
// Marks this interface as a Spring Data Repository
// Spring creates proxy implementation at runtime
// Enables exception translation (SQLException → DataAccessException)

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopName(String name);
}