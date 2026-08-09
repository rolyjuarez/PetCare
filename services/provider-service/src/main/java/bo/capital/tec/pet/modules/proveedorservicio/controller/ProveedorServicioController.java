package bo.capital.tec.pet.modules.proveedorservicio.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.security.CustomUserDetails;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorCatalogMapper;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioRequestDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioResponseDTO;
import bo.capital.tec.pet.modules.proveedorservicio.service.ProveedorServicioService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorServicioController {

    private final ProveedorServicioService proveedorServicioService;
    private final ProveedorCatalogMapper proveedorCatalogMapper;

    @PostMapping("/my/servicios")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> create(
            @Valid @RequestBody ProveedorServicioRequestDTO dto,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        proveedorServicioService.create(proveedorId, dto), "Servicio creado"));
    }

    @GetMapping("/my/servicios")
    public ResponseEntity<ApiResponse<List<ProveedorServicioResponseDTO>>> getMyServicios(
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.listByProveedor(proveedorId)));
    }

    @PutMapping("/my/servicios/{id}")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorServicioRequestDTO dto,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.update(proveedorId, id, dto), "Servicio actualizado"));
    }

    @PutMapping("/my/servicios/{id}/activo")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> setActivo(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        boolean activo = body != null && Boolean.TRUE.equals(body.get("activo"));
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.setActivo(proveedorId, id, activo), "Estado actualizado"));
    }

    @DeleteMapping("/my/servicios/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        proveedorServicioService.delete(proveedorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Servicio eliminado"));
    }

    @GetMapping("/{proveedorId}/servicios")
    public ResponseEntity<ApiResponse<List<ProveedorServicioResponseDTO>>> getServiciosPublicos(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.listActivosByProveedor(proveedorId)));
    }

    private Long requireProveedor(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BusinessException("No autenticado");
        }
        ProveedorInfoDTO proveedor = proveedorCatalogMapper.selectProveedorByUsuarioId(user.getId());
        if (proveedor == null) {
            throw new BusinessException("El usuario no tiene perfil de proveedor");
        }
        return proveedor.getId();
    }
}
