package bo.capital.tec.pet.modules.notificacion.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.notificacion.dto.NotificacionRequestDTO;
import bo.capital.tec.pet.modules.notificacion.dto.NotificacionResponseDTO;
import bo.capital.tec.pet.modules.notificacion.dto.NotificacionSummaryDTO;
import bo.capital.tec.pet.modules.notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API de gestion de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear notificacion")
    public ResponseEntity<ApiResponse<NotificacionResponseDTO>> create(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(notificacionService.create(dto), "Notificacion creada"));
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones")
    public ResponseEntity<ApiResponse<PagedResponse<NotificacionResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(notificacionService.getAll(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener notificacion por ID")
    public ResponseEntity<ApiResponse<NotificacionResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notificacionService.getById(id)));
    }

    @GetMapping("/by-usuario/{usuarioId}")
    @Operation(summary = "Obtener notificaciones por usuario")
    public ResponseEntity<ApiResponse<PagedResponse<NotificacionSummaryDTO>>> getByUsuarioId(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "false") boolean soloNoLeidas,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                notificacionService.getByUsuarioId(usuarioId, soloNoLeidas, page, size)));
    }

    @GetMapping("/unread-count/{usuarioId}")
    @Operation(summary = "Contar notificaciones no leidas")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ApiResponse.success(notificacionService.getUnreadCount(usuarioId)));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar como leida")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificacionService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notificacion marcada como leida"));
    }

    @PutMapping("/read-all/{usuarioId}")
    @Operation(summary = "Marcar todas como leidas")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long usuarioId) {
        notificacionService.markAllAsRead(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(null, "Todas las notificaciones marcadas como leidas"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificacion")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificacionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notificacion eliminada"));
    }
}
