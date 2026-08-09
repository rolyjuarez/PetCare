package bo.capital.tec.pet.modules.proveedor.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.security.CustomUserDetails;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorEspecialidadRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullCreateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullUpdateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorSummaryDTO;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorCrudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorCrudService proveedorCrudService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> create(
            @Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(proveedorCrudService.create(dto), "Proveedor creado"));
    }

    @PostMapping("/full")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> createFull(
            @Valid @RequestBody ProveedorFullCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(proveedorCrudService.createFull(dto), "Proveedor registrado exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(proveedorCrudService.getById(id)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> getMyProveedor(Authentication authentication) {
        Long usuarioId = requireUsuarioId(authentication);
        return ResponseEntity.ok(ApiResponse.success(proveedorCrudService.getByUsuarioId(usuarioId)));
    }

    @PutMapping("/{proveedorId}/especialidad/{servicioId}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> setRequiereCertificado(
            @PathVariable Long proveedorId,
            @PathVariable Long servicioId,
            @RequestBody ProveedorEspecialidadRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                proveedorCrudService.setRequiereCertificado(proveedorId, servicioId, dto.getRequiereCertificado()),
                "Configuracion actualizada"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProveedorSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(proveedorCrudService.getAll(nombre, page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                proveedorCrudService.update(id, dto), "Proveedor actualizado"));
    }

    @PutMapping("/{id}/full")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> updateFull(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorFullUpdateDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                proveedorCrudService.updateFull(id, dto), "Proveedor actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        proveedorCrudService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Proveedor eliminado"));
    }

    @GetMapping("/{id}/has-reservas")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> hasReservas(@PathVariable Long id) {
        boolean has = proveedorCrudService.hasReservas(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("hasReservas", has)));
    }

    private Long requireUsuarioId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new bo.capital.tec.pet.common.exception.BusinessException("No autenticado");
        }
        return user.getId();
    }
}
