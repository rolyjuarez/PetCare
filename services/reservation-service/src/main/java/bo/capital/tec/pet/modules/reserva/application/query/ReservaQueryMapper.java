package bo.capital.tec.pet.modules.reserva.application.query;

import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.EstadoReservaRepository;
import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Proyecta una {@link Reserva} del dominio hacia los DTOs de lectura
 * ({@link ReservaResponseDTO} y {@link ReservaSummaryDTO}) completando la
 * información de catálogo y del estado.
 */
@Component
@RequiredArgsConstructor
public class ReservaQueryMapper {

    private final CatalogInfoRepository catalogInfoRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final ProviderCatalogClient providerCatalogClient;

    public ReservaResponseDTO toResponseDTO(Reserva reserva) {
        ClienteInfoDTO cliente = reserva.getClienteId() != null
                ? catalogInfoRepository.findCliente(reserva.getClienteId()) : null;
        ProveedorInfoDTO proveedor = reserva.getProveedorId() != null
                ? providerCatalogClient.getProveedor(reserva.getProveedorId()) : null;
        ServicioInfoDTO servicio = providerCatalogClient.getServicio(reserva.getServicioId());
        MascotaInfoDTO mascota = catalogInfoRepository.findMascota(reserva.getMascotaId());
        EstadoReserva estado = estadoReservaRepository.findById(reserva.getEstadoReservaId());
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(cliente != null ? cliente.getNombre() : "")
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedor != null ? proveedor.getNombre() : "")
                .proveedorEmpresa(proveedor != null ? proveedor.getEmpresa() : "")
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicio != null ? servicio.getNombre() : "")
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascota != null ? mascota.getNombre() : "")
                .estadoReservaId(reserva.getEstadoReservaId())
                .registroVacunacionId(reserva.getRegistroVacunacionId())
                .modalidadEntrega(reserva.getModalidadEntrega())
                .estadoReservaNombre(estado != null ? estado.getNombre() : "")
                .estadoReservaColor(estado != null ? estado.getColor() : "")
                .fechaReserva(reserva.getFechaReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .latitud(reserva.getLatitud())
                .longitud(reserva.getLongitud())
                .direccionReferencia(reserva.getDireccionReferencia())
                .notas(reserva.getNotas())
                .precioTotal(reserva.getPrecioTotal())
                .motivoRechazo(reserva.getMotivoRechazo())
                .respuestaEn(reserva.getRespuestaEn())
                .createdAt(reserva.getCreatedAt())
                .build();
    }

    public ReservaSummaryDTO toSummaryDTO(Reserva reserva) {
        ClienteInfoDTO cliente = reserva.getClienteId() != null
                ? catalogInfoRepository.findCliente(reserva.getClienteId()) : null;
        ProveedorInfoDTO proveedor = reserva.getProveedorId() != null
                ? providerCatalogClient.getProveedor(reserva.getProveedorId()) : null;
        ServicioInfoDTO servicio = providerCatalogClient.getServicio(reserva.getServicioId());
        MascotaInfoDTO mascota = catalogInfoRepository.findMascota(reserva.getMascotaId());
        EstadoReserva estado = estadoReservaRepository.findById(reserva.getEstadoReservaId());
        return ReservaSummaryDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(cliente != null ? cliente.getNombre() : "")
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedor != null ? proveedor.getNombre() : "")
                .proveedorEmpresa(proveedor != null ? proveedor.getEmpresa() : "")
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicio != null ? servicio.getNombre() : "")
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascota != null ? mascota.getNombre() : "")
                .estadoReservaId(reserva.getEstadoReservaId())
                .registroVacunacionId(reserva.getRegistroVacunacionId())
                .modalidadEntrega(reserva.getModalidadEntrega())
                .estadoReservaNombre(estado != null ? estado.getNombre() : "")
                .estadoReservaColor(estado != null ? estado.getColor() : "")
                .fechaReserva(reserva.getFechaReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .precioTotal(reserva.getPrecioTotal())
                .motivoRechazo(reserva.getMotivoRechazo())
                .build();
    }
}
