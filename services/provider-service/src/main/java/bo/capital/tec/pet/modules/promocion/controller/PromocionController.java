package bo.capital.tec.pet.modules.promocion.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.security.CustomUserDetails;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorCatalogMapper;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
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

import java.util.List;

@RestController
@RequestMapping("/proveedores/{proveedorId}/descuentos")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService promocionService;
    private final ProveedorCatalogMapper proveedorCatalogMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PromocionSummaryDTO>>> listar(
            @PathVariable Long proveedorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.listar(proveedorId, page, size), "Descuentos del proveedor"));
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<PromocionSummaryDTO>>> listarActivas(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.listarActivas(proveedorId), "Descuentos activos del proveedor"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> crear(
            @PathVariable Long proveedorId,
            @Valid @RequestBody PromocionRequestDTO dto,
            Authentication authentication) {
        verificarProveedor(proveedorId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                promocionService.crear(proveedorId, dto), "Descuento creado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> actualizar(
            @PathVariable Long proveedorId,
            @PathVariable Long id,
            @Valid @RequestBody PromocionRequestDTO dto,
            Authentication authentication) {
        verificarProveedor(proveedorId, authentication);
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.actualizar(proveedorId, id, dto), "Descuento actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long proveedorId,
            @PathVariable Long id,
            Authentication authentication) {
        verificarProveedor(proveedorId, authentication);
        promocionService.eliminar(proveedorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Descuento eliminado"));
    }

    private void verificarProveedor(Long proveedorId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BusinessException("No autenticado");
        }
        ProveedorInfoDTO proveedor = proveedorCatalogMapper.selectProveedorByUsuarioId(user.getId());
        if (proveedor == null) {
            throw new BusinessException("El usuario no tiene perfil de proveedor");
        }
        if (!proveedor.getId().equals(proveedorId)) {
            throw new BusinessException("No tiene permisos para gestionar los descuentos de este proveedor");
        }
    }
}
