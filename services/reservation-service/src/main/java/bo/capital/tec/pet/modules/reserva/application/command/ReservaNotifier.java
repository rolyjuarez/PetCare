package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.common.email.EmailService;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.modules.reserva.application.query.ReservaQueryMapper;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.NotificationPort;
import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Efectos secundarios del lado escritura: publicación de eventos de dominio,
 * notificaciones internas y correos al cliente.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaNotifier {

    private final DomainEventPublisher eventPublisher;
    private final NotificationPort notificationPort;
    private final EmailService emailService;
    private final CatalogInfoRepository catalogInfoRepository;
    private final ReservaQueryMapper queryMapper;

    public void publicarCreada(Reserva reserva, ReservaResponseDTO response) {
        try {
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogInfoRepository.findCliente(reserva.getClienteId()) : null;
            eventPublisher.publish(new ReservaCreadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(),
                    cliente != null ? cliente.getEmail() : "",
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    reserva.getMascotaId(), response.getMascotaNombre(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal(), reserva.getRegistroVacunacionId(),
                    reserva.getModalidadEntrega()));
        } catch (Exception e) {
            log.warn("Error publicando ReservaCreadaEvent: {}", e.getMessage());
        }
    }

    public void publicarConfirmada(Reserva reserva) {
        try {
            eventPublisher.publish(new ReservaConfirmadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), reserva.getProveedorId(),
                    reserva.getServicioId(), reserva.getMascotaId(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal(), reserva.getModalidadEntrega(),
                    reserva.getRegistroVacunacionId()));
        } catch (Exception e) {
            log.warn("Error publicando ReservaConfirmadaEvent: {}", e.getMessage());
        }
    }

    public void publicarCancelada(Reserva reserva, String motivo) {
        try {
            ReservaResponseDTO response = queryMapper.toResponseDTO(reserva);
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogInfoRepository.findCliente(reserva.getClienteId()) : null;
            eventPublisher.publish(new ReservaCanceladaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(),
                    cliente != null ? cliente.getEmail() : "",
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    motivo));
        } catch (Exception e) {
            log.warn("Error publicando ReservaCanceladaEvent: {}", e.getMessage());
        }
    }

    public void notificarCliente(Reserva reserva, String titulo, String mensaje, String tipo) {
        try {
            if (reserva.getClienteId() == null) {
                return;
            }
            notificationPort.notificar(reserva.getClienteId(), titulo, mensaje, tipo);
            log.debug("Notificación creada para cliente {}: {}", reserva.getClienteId(), titulo);
        } catch (Exception e) {
            log.warn("Error creando notificación para reserva {}: {}", reserva.getCodigo(), e.getMessage());
        }
    }

    public void enviarEmailCliente(Reserva reserva, String asunto, String cuerpo) {
        try {
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogInfoRepository.findCliente(reserva.getClienteId()) : null;
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
                log.debug("Cliente sin email, se omite envío para reserva {}", reserva.getCodigo());
                return;
            }
            emailService.sendHtml(cliente.getEmail(), asunto, cuerpo);
        } catch (Exception e) {
            log.warn("Error preparando email para reserva {}: {}", reserva.getCodigo(), e.getMessage());
        }
    }
}
