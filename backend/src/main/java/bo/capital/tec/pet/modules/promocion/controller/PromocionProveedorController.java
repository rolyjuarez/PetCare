package bo.capital.tec.pet.modules.promocion.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorMapper;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
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

import java.util.Map;

@RestController
@RequestMapping("/proveedores/my/promociones")
@RequiredArgsConstructor
@Tag(name = "Promociones del Proveedor", description = "Promociones creadas por el proveedor autenticado")
public class PromocionProveedorController {

    private final PromocionService promocionService;
    private final UsuarioApi usuarioApi;
    private final ProveedorMapper proveedorMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear promocion como proveedor (notifica por correo a todos los clientes)")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> create(@Valid @RequestBody PromocionRequestDTO dto) {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promocionService.createForProveedor(proveedorId, dto),
                        "Promocion creada y notificada a los clientes"));
    }

    @GetMapping
    @Operation(summary = "Listar promociones del proveedor autenticado")
    public ResponseEntity<ApiResponse<PagedResponse<PromocionSummaryDTO>>> getMyPromociones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.ok(ApiResponse.success(promocionService.getByProveedor(proveedorId, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promocion propia")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody PromocionRequestDTO dto) {
        Long proveedorId = proveedorActualId();
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.updateForProveedor(proveedorId, id, dto), "Promocion actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promocion propia")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Long proveedorId = proveedorActualId();
        promocionService.deleteForProveedor(proveedorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promocion eliminada"));
    }

    @PostMapping("/{id}/notificar")
    @Operation(summary = "Reenviar por correo la promocion a todos los clientes")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> notificar(@PathVariable Long id) {
        Long proveedorId = proveedorActualId();
        int enviados = promocionService.notificarClientes(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("enviados", enviados),
                "Promocion notificada a los clientes"));
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
