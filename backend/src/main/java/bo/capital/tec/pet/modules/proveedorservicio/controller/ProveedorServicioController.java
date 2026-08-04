package bo.capital.tec.pet.modules.proveedorservicio.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorMapper;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioRequestDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioResponseDTO;
import bo.capital.tec.pet.modules.proveedorservicio.service.ProveedorServicioService;
import bo.capital.tec.pet.modules.usuario.api.UsuarioApi;
import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Tag(name = "Servicios del Proveedor", description = "Servicios propios creados y gestionados por cada proveedor")
public class ProveedorServicioController {

    private final ProveedorServicioService proveedorServicioService;
    private final UsuarioApi usuarioApi;
    private final ProveedorMapper proveedorMapper;

    @PostMapping("/my/servicios")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear servicio propio del proveedor autenticado")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> create(
            @Valid @RequestBody ProveedorServicioRequestDTO dto) {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(proveedorServicioService.create(proveedorId, dto), "Servicio creado"));
    }

    @GetMapping("/my/servicios")
    @Operation(summary = "Listar servicios del proveedor autenticado")
    public ResponseEntity<ApiResponse<List<ProveedorServicioResponseDTO>>> getMyServicios() {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.ok(ApiResponse.success(proveedorServicioService.listByProveedor(proveedorId)));
    }

    @PutMapping("/my/servicios/{id}")
    @Operation(summary = "Actualizar servicio propio")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody ProveedorServicioRequestDTO dto) {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.update(proveedorId, id, dto), "Servicio actualizado"));
    }

    @PutMapping("/my/servicios/{id}/activo")
    @Operation(summary = "Activar o desactivar servicio propio")
    public ResponseEntity<ApiResponse<ProveedorServicioResponseDTO>> setActivo(
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Long proveedorId = proveedorActualId();
        boolean activo = body != null && Boolean.TRUE.equals(body.get("activo"));
        return ResponseEntity.ok(ApiResponse.success(
                proveedorServicioService.setActivo(proveedorId, id, activo), "Estado actualizado"));
    }

    @DeleteMapping("/my/servicios/{id}")
    @Operation(summary = "Eliminar servicio propio")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Long proveedorId = proveedorActualId();
        proveedorServicioService.delete(proveedorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Servicio eliminado"));
    }

    @GetMapping("/{proveedorId}/servicios")
    @Operation(summary = "Listar servicios activos de un proveedor (catálogo público)")
    public ResponseEntity<ApiResponse<List<ProveedorServicioResponseDTO>>> getServiciosPublicos(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(ApiResponse.success(proveedorServicioService.listActivosByProveedor(proveedorId)));
    }

    private Long proveedorActualId() {
        String username = SecurityUtil.getCurrentUsername();
        Usuario usuario = username != null ? usuarioApi.findByUsername(username) : null;
        if (usuario == null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Usuario no autenticado");
        }
        var proveedor = proveedorMapper.findByUsuarioId(usuario.getId());
        if (proveedor == null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("El usuario no es un proveedor");
        }
        return proveedor.getId();
    }
}
