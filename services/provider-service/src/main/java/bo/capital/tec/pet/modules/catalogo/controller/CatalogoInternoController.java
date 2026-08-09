package bo.capital.tec.pet.modules.catalogo.controller;

import bo.capital.tec.pet.modules.catalogo.dto.DisponibilidadInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.PromocionInfoDTO;
import bo.capital.tec.pet.modules.catalogo.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.catalogo.mapper.CatalogoInternoMapper;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/interna")
@RequiredArgsConstructor
public class CatalogoInternoController {

    private final CatalogoInternoMapper catalogoInternoMapper;

    @GetMapping("/proveedores/{proveedorId}")
    public ProveedorInfoDTO proveedor(@PathVariable Long proveedorId) {
        return catalogoInternoMapper.selectProveedorById(proveedorId);
    }

    @GetMapping("/servicios/{servicioId}")
    public ServicioInfoDTO servicio(@PathVariable Long servicioId) {
        return catalogoInternoMapper.selectServicioById(servicioId);
    }

    @GetMapping("/servicios/{servicioId}/modalidades")
    public List<ModalidadInfoDTO> modalidades(@PathVariable Long servicioId) {
        return catalogoInternoMapper.selectModalidadesByServicio(servicioId);
    }

    @GetMapping("/servicios/{servicioId}/modalidades/{modalidad}/valida")
    public boolean modalidadValida(@PathVariable Long servicioId, @PathVariable String modalidad) {
        return catalogoInternoMapper.selectModalidadValida(servicioId, modalidad);
    }

    @GetMapping("/servicios/{servicioId}/modalidades/{modalidad}/costo-adicional")
    public BigDecimal costoAdicional(@PathVariable Long servicioId, @PathVariable String modalidad) {
        return catalogoInternoMapper.selectModalidadCostoAdicional(servicioId, modalidad);
    }

    @GetMapping("/proveedores/{proveedorId}/servicios/{servicioId}/requiere-certificado")
    public Boolean requiereCertificado(@PathVariable Long proveedorId, @PathVariable Long servicioId) {
        return catalogoInternoMapper.selectRequiereCertificado(proveedorId, servicioId);
    }

    @GetMapping("/proveedores/{proveedorId}/servicios/{servicioId}/disponibilidades")
    public List<DisponibilidadInfoDTO> disponibilidades(@PathVariable Long proveedorId,
                                                        @PathVariable Long servicioId) {
        return catalogoInternoMapper.selectDisponibilidades(proveedorId, servicioId);
    }

    @GetMapping("/promociones/activa")
    public PromocionInfoDTO promocionActiva(@RequestParam Long proveedorId,
                                            @RequestParam Long servicioId) {
        return catalogoInternoMapper.selectPromocionActiva(proveedorId, servicioId);
    }

    @PostMapping("/promociones/{promocionId}/usos/incrementar")
    public void incrementarUsos(@PathVariable Long promocionId) {
        catalogoInternoMapper.incrementarUsosPromocion(promocionId);
    }

    @PostMapping("/promociones/{promocionId}/usos/decrementar")
    public void decrementarUsos(@PathVariable Long promocionId) {
        catalogoInternoMapper.decrementarUsosPromocion(promocionId);
    }
}
