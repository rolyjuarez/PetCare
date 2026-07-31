package bo.capital.tec.pet.modules.usuario.infrastructure.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioRequestDTO;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioResponseDTO;
import bo.capital.tec.pet.modules.usuario.dto.UsuarioSummaryDTO;
import bo.capital.tec.pet.modules.usuario.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API de gestion de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> create(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(usuarioService.create(dto), "Usuario creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.getById(id)));
    }

    @GetMapping("/by-username/{username}")
    @Operation(summary = "Obtener usuario por username")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.getByUsername(username)));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<ApiResponse<PagedResponse<UsuarioSummaryDTO>>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long personaId,
            @RequestParam(required = false) Long rolId,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.getAll(username, personaId, rolId, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.update(id, dto), "Usuario actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario eliminado"));
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Cambiar contrasena")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id,
            @RequestParam String oldPassword, @RequestParam String newPassword) {
        usuarioService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success(null, "Contrasena cambiada"));
    }

    @PutMapping("/{id}/toggle-active")
    @Operation(summary = "Activar/desactivar usuario")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        usuarioService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Estado del usuario actualizado"));
    }
}
