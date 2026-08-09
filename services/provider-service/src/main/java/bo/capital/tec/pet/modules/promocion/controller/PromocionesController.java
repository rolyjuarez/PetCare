package bo.capital.tec.pet.modules.promocion.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/promociones")
@RequiredArgsConstructor
public class PromocionesController {

    private final PromocionService promocionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> create(
            @Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(promocionService.crearGlobal(dto), "Promocion creada"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.obtenerPorId(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PromocionSummaryDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.listarTodas(page, size)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromocionSummaryDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(promocionService.listarActivasGlobales()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                promocionService.actualizarGlobal(id, dto), "Promocion actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        promocionService.eliminarGlobal(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promocion eliminada"));
    }
}
