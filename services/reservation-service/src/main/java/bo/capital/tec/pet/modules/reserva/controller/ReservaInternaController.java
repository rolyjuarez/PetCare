package bo.capital.tec.pet.modules.reserva.controller;

import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaUbicacionDTO;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.VacunaCatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interna")
@RequiredArgsConstructor
public class ReservaInternaController {

    private final ReservaMapper reservaMapper;
    private final VacunaCatalogMapper vacunaCatalogMapper;

    @GetMapping("/registros-vacunacion/{id}")
    public RegistroVacunacionInfoDTO registroVacunacion(@PathVariable Long id) {
        return vacunaCatalogMapper.selectRegistroVacunacion(id);
    }

    @GetMapping("/reservas/{id}/ubicacion")
    public ReservaUbicacionDTO ubicacion(@PathVariable Long id) {
        Reserva reserva = reservaMapper.selectById(id);
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
        return reservaMapper.countByProveedorId(proveedorId);
    }
}
