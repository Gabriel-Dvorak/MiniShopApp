package at.spengergasse.Mini.controller;

// Controller Layer (Presentation Layer)
// ---------------------------------
// Handles HTTP requests and responses
// Maps URLs to methods
// Validates incoming data (@Valid)
// Delegates business logic to Service Layer
// Does NOT contain business logic or database access


// Spring Framework Concepts
// ---------------------------------
// Inversion of Control (IoC):
//   - Spring manages object lifecycle and dependencies
//   - Framework calls your code (Hollywood Principle: "Don't call us, we'll call you")
//   - Objects are created and wired by Spring Container
//
// Dependency Injection (DI):
//   - Dependencies are provided (injected) by Spring
//   - Constructor injection (recommended): @RequiredArgsConstructor + final fields
//   - Field injection (not recommended): @Autowired on fields
//   - Setter injection: @Autowired on setters
//   - Benefits: Loose coupling, easier testing, better maintainability


// HTTP Protocol Fundamentals
// ---------------------------------
// HTTP Request <-> Response cycle
// Reference: https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages
//
// HTTP Request contains:
//   - Method (GET, POST, PUT, DELETE, etc.)
//   - URL/URI path
//   - Headers (Content-Type, Authorization, etc.)
//   - Body (for POST, PUT, PATCH)
//
// HTTP Response contains:
//   - Status Code (200, 404, 500, etc.)
//   - Headers (Content-Type, Location, etc.)
//   - Body (JSON data)


// HTTP Methods (Verbs)
// ----------------------------------------------
// Method    | Idempotent | Safe  | Use Case
// -----------------------------------------------
// GET       | YES        | YES   | Retrieve resource(s), no side effects
// POST      | NO         | NO    | Create new resource, has side effects
// PUT       | YES        | NO    | Replace entire resource (idempotent updates)
// PATCH     | NO         | NO    | Partial update (modify specific fields)
// DELETE    | YES        | NO    | Remove resource (same result if repeated)
//
// Idempotent: Multiple identical requests have same effect as single request
//   - GET /todos/1 → always returns same todo (safe)
//   - DELETE /todos/1 → first call deletes, subsequent calls = already deleted (same state)
//   - PUT /todos/1 → always replaces with same data (same state)
//   - POST /todos → creates NEW resource each time (NOT idempotent)
//
// Safe: Does not modify server state (read-only operations)
//   - Only GET is safe (read-only)
//   - All others modify data


// HTTP Methods -> CRUD Operations
// -----------------------------------------------
// HTTP Method   | CRUD      | Database Operation
// -----------------------------------------------
// GET           | READ      | SELECT (query rows)
// POST          | CREATE    | INSERT (new row)
// PUT           | UPDATE    | UPDATE (replace entire row)
// PATCH         | UPDATE    | UPDATE (modify specific columns)
// DELETE        | DELETE    | DELETE (soft or hard delete)


// HTTP Status Codes (Most Common)
// ---------------------------------
// 2xx Success:
//   200 OK              -> Successful GET, PUT (resource in response body)
//   201 CREATED         -> Successful POST (new resource created, Location header)
//   204 NO_CONTENT      -> Successful DELETE (no response body needed)
//
// 4xx Client Errors:
//   400 BAD_REQUEST     -> Validation failed, malformed request
//   401 UNAUTHORIZED    -> Authentication required
//   403 FORBIDDEN       -> Authenticated but not authorized
//   404 NOT_FOUND       -> Resource does not exist
//   409 CONFLICT        -> Business rule violation (e.g., duplicate)
//
// 5xx Server Errors:
//   500 INTERNAL_SERVER_ERROR -> Unexpected server error (bug, exception)
//   503 SERVICE_UNAVAILABLE   -> Server temporarily unavailable


// RESTful API Design
// ---------------------------------
// REST = Representational State Transfer
// Roy Fielding, PhD Dissertation, 2000
// https://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm
//
// REST Principles:
//   - Resource-based (not action-based)
//   - Stateless (no session state on server)
//   - Use HTTP methods correctly
//   - Client-server separation
//   - Uniform interface (consistent patterns)
//
// Resource Naming:
//   - Use nouns (not verbs): /todos (good), /getTodos (bad)
//   - Use plural for collections: /todos
//   - Use path parameters for IDs: /todos/{id}
//   - Hierarchical structure: /users/{userId}/todos/{todoId}


// RESTful Endpoint Design (This API)
// ---------------------------------------------------------------------------
// Endpoint                  | Method  | Status Codes    | Description
// ---------------------------------------------------------------------------
// GET /api/todos            | GET     | 200             | Retrieve all todos
// GET /api/todos/{id}       | GET     | 200, 404        | Retrieve single todo by ID
// POST /api/todos           | POST    | 201, 400        | Create new todo (Location header)
// PUT /api/todos/{id}       | PUT     | 200, 400, 404   | Replace entire todo by ID
// DELETE /api/todos/{id}    | DELETE  | 204, 404        | Delete todo by ID


// JSON Serialization & Deserialization
// ---------------------------------
// Handled automatically by Jackson ObjectMapper (included in Spring Boot)
//
// Serialization (Java Object -> JSON):
//   - Happens on HTTP Response
//   - Spring converts return value to JSON
//   - Example: Todo object -> {"id": 1, "title": "Buy milk"}
//
// Deserialization (JSON -> Java Object):
//   - Happens on HTTP Request
//   - Spring converts JSON body to @RequestBody parameter
//   - Example: {"title": "Buy milk"} -> TodoRequest object
//   - Validation happens AFTER deserialization (@Valid triggers bean validation)

import at.spengergasse.Mini.model.Product;
import at.spengergasse.Mini.model.Shop;
import at.spengergasse.Mini.service.ProductService;
import at.spengergasse.Mini.service.ShopService;
import at.spengergasse.Mini.viewmodel.ProductRequest;
import at.spengergasse.Mini.viewmodel.ShopRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ProductService productService;

    // Hole einen einzelnen Shop
    @GetMapping("/{id}")
    public Shop getShop(@PathVariable Long id) {
        return shopService.getShopById(id);
    }

    // Erstelle einen neuen Shop
    @PostMapping
    public Shop createShop(@RequestBody @Valid ShopRequest request) {
        return shopService.createShop(request);
    }

    // Lösche einen Shop
    @DeleteMapping("/{id}")
    public void deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
    }

    // Produkt zu Shop hinzufügen (ruft ShopService.addProductToShop auf)
    @PostMapping("/{shopId}/products")
    public Product addProductToShop(@PathVariable Long shopId, @RequestBody @Valid ProductRequest request) {
        return shopService.addProductToShop(shopId, request);
    }

    // Produkt aus Shop entfernen (ShopService.removeProductFromShop)
    @DeleteMapping("/{shopId}/products/{productId}")
    public void removeProductFromShop(@PathVariable Long shopId, @PathVariable Long productId) {
        shopService.removeProductFromShop(shopId, productId);
    }

    // Alle Produkte eines Shops holen
    @GetMapping("/{shopId}/products")
    public List<Product> getAllProductsFromShop(@PathVariable Long shopId) {
        return shopService.getAllProductsFromShop(shopId);
    }

    // Produkt eines Shops updaten (ShopService.updateProductInShop)
    @PutMapping("/{shopId}/products/{productId}")
    public Product updateProductInShop(@PathVariable Long shopId,
                                       @PathVariable Long productId,
                                       @RequestBody @Valid ProductRequest request) {
        return shopService.updateProductInShop(shopId, productId, request);
    }

    // Produkt direkt löschen, wenn es keinem Shop zugeordnet ist (ProductService.deleteProduct)
    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }

    // Produkt direkt erstellen, optional Shop-ID (ProductService.createProduct)
    @PostMapping("/products")
    public Product createProduct(@RequestParam(required = false) Long shopId,
                                 @RequestBody @Valid ProductRequest request) {
        return productService.createProduct(request, shopId);
    }
}