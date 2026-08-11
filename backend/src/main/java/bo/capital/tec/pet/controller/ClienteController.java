package bo.capital.tec.pet.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.domain.Usuario;
import bo.capital.tec.pet.dto.ClienteRequestDTO;
import bo.capital.tec.pet.dto.ClienteResponseDTO;
import bo.capital.tec.pet.dto.ClienteSummaryDTO;
import bo.capital.tec.pet.repository.ClienteMapper;
import bo.capital.tec.pet.repository.UsuarioMapper;
import bo.capital.tec.pet.security.SecurityUtil;
import bo.capital.tec.pet.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API de gestion de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioMapper usuarioMapper;
    private final ClienteMapper clienteMapper;

    @GetMapping("/me")
    @Operation(summary = "Obtener cliente del usuario autenticado")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> getMe() {
        String username = SecurityUtil.getCurrentUsername();
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null || usuario.getPersonaId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuario no autenticado", 401));
        }
        bo.capital.tec.pet.domain.Cliente cliente = clienteMapper.findByPersonaId(usuario.getPersonaId());
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Cliente no encontrado", 404));
        }
        return ResponseEntity.ok(ApiResponse.success(clienteService.getById(cliente.getId())));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> create(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(clienteService.create(dto), "Cliente creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getById(id)));
    }

    @GetMapping("/by-persona/{personaId}")
    @Operation(summary = "Obtener cliente por persona")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> getByPersonaId(@PathVariable Long personaId) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getByPersonaId(personaId)));
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<ApiResponse<PagedResponse<ClienteSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ci,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getAll(nombre, ci, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.update(id, dto), "Cliente actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cliente eliminado"));
    }
}
