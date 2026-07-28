package bo.capital.tec.pet.modules.menu.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.menu.dto.MenuRequestDTO;
import bo.capital.tec.pet.modules.menu.dto.MenuResponseDTO;
import bo.capital.tec.pet.modules.menu.dto.MenuSummaryDTO;
import bo.capital.tec.pet.modules.menu.service.MenuService;
import bo.capital.tec.pet.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "API de gestion de menus")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear menu")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> create(@Valid @RequestBody MenuRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(menuService.create(dto), "Menu creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener menu por ID")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar menus")
    public ResponseEntity<ApiResponse<PagedResponse<MenuSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getAll(nombre, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar menu")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody MenuRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(menuService.update(id, dto), "Menu actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar menu")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menu eliminado"));
    }

    @GetMapping("/by-rol/{rolId}")
    @Operation(summary = "Obtener menus por rol")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getMenusByRoleId(@PathVariable Long rolId) {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByRoleId(rolId)));
    }

    @GetMapping("/my-menus")
    @Operation(summary = "Obtener menus del usuario autenticado")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getMyMenus() {
        String username = SecurityUtil.getCurrentUsername();
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenusByUsername(username)));
    }

    @GetMapping("/tree")
    @Operation(summary = "Obtener arbol de menus")
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> getMenuTree() {
        return ResponseEntity.ok(ApiResponse.success(menuService.getMenuTree()));
    }
}
