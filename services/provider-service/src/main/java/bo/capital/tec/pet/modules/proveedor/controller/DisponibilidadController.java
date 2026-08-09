package bo.capital.tec.pet.modules.proveedor.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadResponseDTO;
import bo.capital.tec.pet.modules.proveedor.service.DisponibilidadService;
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
@RequestMapping("/disponibilidades")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @PostMapping
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> create(
            @Valid @RequestBody DisponibilidadRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(disponibilidadService.create(dto), "Disponibilidad creada"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<DisponibilidadResponseDTO>>> getAll(
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                disponibilidadService.getAll(proveedorId, servicioId, page, size)));
    }

    @GetMapping("/by-proveedor-servicio/{proveedorId}/{servicioId}")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponseDTO>>> getByProveedorAndServicio(
            @PathVariable Long proveedorId, @PathVariable Long servicioId) {
        return ResponseEntity.ok(ApiResponse.success(
                disponibilidadService.getByProveedorAndServicio(proveedorId, servicioId)));
    }

    @GetMapping("/by-servicio/{servicioId}")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponseDTO>>> getByServicio(
            @PathVariable Long servicioId) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.getByServicio(servicioId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                disponibilidadService.update(id, dto), "Disponibilidad actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        disponibilidadService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Disponibilidad eliminada"));
    }
}
