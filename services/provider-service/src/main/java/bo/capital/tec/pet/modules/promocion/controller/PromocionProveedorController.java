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

import java.util.Map;

@RestController
@RequestMapping("/proveedores/my/promociones")
@RequiredArgsConstructor
public class PromocionProveedorController {

    private final PromocionService promocionService;
    private final ProveedorCatalogMapper proveedorCatalogMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> create(
            @Valid @RequestBody PromocionRequestDTO dto,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        promocionService.crear(proveedorId, dto), "Promocion creada y notificada a los clientes"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PromocionSummaryDTO>>> getMyPromociones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(promocionService.listar(proveedorId, page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PromocionRequestDTO dto,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.actualizar(proveedorId, id, dto), "Promocion actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        promocionService.eliminar(proveedorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promocion eliminada"));
    }

    @PostMapping("/{id}/notificar")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> notificar(
            @PathVariable Long id,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        PromocionResponseDTO promocion = promocionService.obtenerPorId(id);
        if (!promocion.getProveedorId().equals(proveedorId)) {
            throw new BusinessException("La promocion no pertenece al proveedor autenticado");
        }
        int enviados = promocionService.notificarClientes(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("enviados", enviados),
                "Promocion notificada a los clientes"));
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
