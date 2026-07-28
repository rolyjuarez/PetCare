package bo.capital.tec.pet.modules.bitacora.controller;

import bo.capital.tec.pet.modules.bitacora.dto.BitacoraResponseDTO;
import bo.capital.tec.pet.modules.bitacora.service.BitacoraService;
import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/bitacoras")
@RequiredArgsConstructor
@Tag(name = "Bitacora", description = "API de consulta de bitacora de auditoria")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de bitacora por ID")
    public ResponseEntity<ApiResponse<BitacoraResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bitacoraService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar registros de bitacora")
    public ResponseEntity<ApiResponse<PagedResponse<BitacoraResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(bitacoraService.getAll(page, size)));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar bitacora por filtros")
    public ResponseEntity<ApiResponse<PagedResponse<BitacoraResponseDTO>>> getByFilters(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                bitacoraService.getByFilters(usuario, accion, entidad, fechaDesde, fechaHasta, page, size)));
    }
}
