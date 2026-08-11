package bo.capital.tec.pet.modules.proveedor.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorCatalogoDTO;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorCatalogoController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProveedorCatalogoDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                proveedorService.listarCatalogo(page, size), "Catálogo de proveedores"));
    }
}
