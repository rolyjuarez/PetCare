package bo.capital.tec.pet.modules.persona.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.persona.dto.PersonaRequestDTO;
import bo.capital.tec.pet.modules.persona.dto.PersonaResponseDTO;
import bo.capital.tec.pet.modules.persona.dto.PersonaSummaryDTO;
import bo.capital.tec.pet.modules.persona.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
@Tag(name = "Personas", description = "API de gestion de personas")
public class PersonaController {

    private final PersonaService personaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear persona")
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> create(@Valid @RequestBody PersonaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(personaService.create(dto), "Persona creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener persona por ID")
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(personaService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar personas")
    public ResponseEntity<ApiResponse<PagedResponse<PersonaSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ci,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(personaService.getAll(nombre, ci, email, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar persona")
    public ResponseEntity<ApiResponse<PersonaResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody PersonaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(personaService.update(id, dto), "Persona actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar persona")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        personaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Persona eliminada"));
    }
}
