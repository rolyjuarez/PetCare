package bo.capital.tec.pet.modules.proveedor.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.security.CustomUserDetails;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ResponderSolicitudRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.SolicitudReservaResponseDTO;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorCatalogMapper;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proveedor/solicitudes")
@RequiredArgsConstructor
public class ProveedorSolicitudesController {

    private final ProveedorService proveedorService;
    private final ProveedorCatalogMapper proveedorCatalogMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<SolicitudReservaResponseDTO>>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                proveedorService.listarSolicitudes(proveedorId, estado, page, size)));
    }

    @PutMapping("/{id}/aceptar")
    public ResponseEntity<ApiResponse<SolicitudReservaResponseDTO>> aceptar(@PathVariable Long id,
                                                                            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                proveedorService.aceptar(proveedorId, id), "Solicitud aceptada"));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<ApiResponse<SolicitudReservaResponseDTO>> rechazar(
            @PathVariable Long id,
            @RequestBody(required = false) ResponderSolicitudRequestDTO request,
            Authentication authentication) {
        Long proveedorId = requireProveedor(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                proveedorService.rechazar(proveedorId, id, request), "Solicitud rechazada"));
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
