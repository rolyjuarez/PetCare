package bo.capital.tec.pet.common.event.handler;

import bo.capital.tec.pet.common.event.DomainEvent;
import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.pago.event.PagoConfirmadoEvent;
import bo.capital.tec.pet.modules.pago.event.PagoReembolsadoEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCompletadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class NotificacionEventHandler {

    private final NotificacionApi notificacionApi;

    @EventListener
    public void handleReservaCreada(ReservaCreadaEvent event) {
        createNotification(event.getClienteId(), "Reserva Creada",
                "Su reserva " + event.getCodigo() + " ha sido creada exitosamente.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Nueva Reserva",
                    "Se le ha asignado la reserva " + event.getCodigo() + ".", "INFO");
        }
    }

    @EventListener
    public void handleReservaConfirmada(ReservaConfirmadaEvent event) {
        createNotification(event.getClienteId(), "Reserva Confirmada",
                "Su reserva " + event.getCodigo() + " ha sido confirmada.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Reserva Confirmada",
                    "La reserva " + event.getCodigo() + " ha sido confirmada.", "INFO");
        }
    }

    @EventListener
    public void handleReservaCancelada(ReservaCanceladaEvent event) {
        createNotification(event.getClienteId(), "Reserva Cancelada",
                "Su reserva " + event.getCodigo() + " ha sido cancelada.", "ADVERTENCIA");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Reserva Cancelada",
                    "La reserva " + event.getCodigo() + " ha sido cancelada.", "ADVERTENCIA");
        }
    }

    @EventListener
    public void handleReservaCompletada(ReservaCompletadaEvent event) {
        createNotification(event.getClienteId(), "Servicio Completado",
                "El servicio de la reserva " + event.getCodigo() + " ha sido completado.", "EXITO");
        if (event.getProveedorId() != null) {
            createNotification(event.getProveedorId(), "Servicio Completado",
                    "La reserva " + event.getCodigo() + " ha sido marcada como completada.", "EXITO");
        }
    }

    @EventListener
    public void handlePagoConfirmado(PagoConfirmadoEvent event) {
        createNotification(event.getClienteId(), "Pago Confirmado",
                "Su pago de Bs " + event.getMonto() + " para la reserva " + event.getReservaCodigo()
                        + " ha sido confirmado.", "EXITO");
    }

    @EventListener
    public void handlePagoReembolsado(PagoReembolsadoEvent event) {
        createNotification(event.getClienteId(), "Pago Reembolsado",
                "Su pago de Bs " + event.getMonto() + " para la reserva " + event.getReservaCodigo()
                        + " ha sido reembolsado.", "INFO");
    }

    private void createNotification(Long usuarioId, String titulo, String mensaje, String tipo) {
        try {
            Notificacion notificacion = Notificacion.builder()
                    .usuarioId(usuarioId)
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .build();
            notificacionApi.insert(notificacion);
        } catch (Exception e) {
            log.warn("Error creating notification for usuario {}: {}", usuarioId, e.getMessage());
        }
    }
}
