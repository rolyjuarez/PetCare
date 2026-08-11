package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.MascotaRequestDTO;
import bo.capital.tec.pet.dto.MascotaResponseDTO;
import bo.capital.tec.pet.dto.MascotaSummaryDTO;
import bo.capital.tec.pet.service.MascotaService;
import bo.capital.tec.pet.security.SecurityUtil;
import bo.capital.tec.pet.domain.Usuario;
import bo.capital.tec.pet.repository.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "API de gestion de mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear mascota")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> create(@Valid @RequestBody MascotaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mascotaService.create(dto), "Mascota creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(mascotaService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar mascotas")
    public ResponseEntity<ApiResponse<PagedResponse<MascotaSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long especieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(mascotaService.getAll(nombre, clienteId, especieId, page, size)));
    }

    @GetMapping("/my")
    @Operation(summary = "Obtener mascotas del usuario autenticado")
    public ResponseEntity<ApiResponse<List<MascotaSummaryDTO>>> getMyMascotas() {
        String username = SecurityUtil.getCurrentUsername();
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuario no autenticado", 401));
        }
        return ResponseEntity.ok(ApiResponse.success(mascotaService.getByUsuarioId(usuario.getId())));
    }

    @GetMapping("/by-cliente/{clienteId}")
    @Operation(summary = "Obtener mascotas por cliente")
    public ResponseEntity<ApiResponse<List<MascotaSummaryDTO>>> getByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.success(mascotaService.getByClienteId(clienteId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota")
    public ResponseEntity<ApiResponse<MascotaResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody MascotaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(mascotaService.update(id, dto), "Mascota actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Mascota eliminada"));
    }
}
