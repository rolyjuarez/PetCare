package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaUbicacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints internos consumidos por otros microservicios.
 */
@RestController
@RequestMapping("/interna")
@RequiredArgsConstructor
public class ReservaInternaController {

    private final ReservaQueryRepository queryRepository;
    private final CatalogInfoRepository catalogInfoRepository;

    @GetMapping("/registros-vacunacion/{id}")
    public RegistroVacunacionInfoDTO registroVacunacion(@PathVariable Long id) {
        return catalogInfoRepository.findRegistroVacunacion(id);
    }

    @GetMapping("/reservas/{id}/ubicacion")
    public ReservaUbicacionDTO ubicacion(@PathVariable Long id) {
        Reserva reserva = queryRepository.findById(id);
        if (reserva == null) {
            return null;
        }
        return ReservaUbicacionDTO.builder()
                .reservaId(reserva.getId())
                .latitud(reserva.getLatitud())
                .longitud(reserva.getLongitud())
                .direccionReferencia(reserva.getDireccionReferencia())
                .build();
    }

    @GetMapping("/reservas/count-by-proveedor/{proveedorId}")
    public long countByProveedor(@PathVariable Long proveedorId) {
        return queryRepository.countByProveedorId(proveedorId);
    }
}
