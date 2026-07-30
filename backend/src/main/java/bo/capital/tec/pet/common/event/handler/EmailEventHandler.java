package bo.capital.tec.pet.common.event.handler;

import bo.capital.tec.pet.common.email.EmailService;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class EmailEventHandler {

    private final EmailService emailService;

    @EventListener
    public void handleReservaCreada(ReservaCreadaEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getCodigo());
        variables.put("servicio", event.getServicioNombre());
        variables.put("mascota", event.getMascotaNombre());
        variables.put("fecha", event.getFechaInicio() != null ? event.getFechaInicio().toString() : "");
        variables.put("hora", event.getHoraInicio() != null ? event.getHoraInicio().toString() : "");
        variables.put("monto", event.getPrecioTotal() != null ? event.getPrecioTotal().toString() : "");
        emailService.sendEmail(event.getClienteEmail(), "Reserva Creada - PETCare",
                "email/reserva-creada", variables);
    }

    @EventListener
    public void handleReservaConfirmada(ReservaConfirmadaEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getCodigo());
        variables.put("servicio", event.getServicioNombre());
        variables.put("proveedor", event.getProveedorEmpresa());
        variables.put("fecha", event.getFechaInicio() != null ? event.getFechaInicio().toString() : "");
        variables.put("hora", event.getHoraInicio() != null ? event.getHoraInicio().toString() : "");
        variables.put("monto", event.getPrecioTotal() != null ? event.getPrecioTotal().toString() : "");
        emailService.sendEmail(event.getClienteEmail(), "Reserva Confirmada - PETCare",
                "email/reserva-confirmada", variables);
    }

    @EventListener
    public void handleReservaCancelada(ReservaCanceladaEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getCodigo());
        variables.put("servicio", event.getServicioNombre());
        variables.put("motivo", event.getMotivo() != null ? event.getMotivo() : "No especificado");
        emailService.sendEmail(event.getClienteEmail(), "Reserva Cancelada - PETCare",
                "email/reserva-cancelada", variables);
    }

    @EventListener
    public void handleReservaCompletada(ReservaCompletadaEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getCodigo());
        variables.put("servicio", event.getServicioNombre());
        variables.put("monto", event.getPrecioTotal() != null ? event.getPrecioTotal().toString() : "");
        emailService.sendEmail(event.getClienteEmail(), "Servicio Completado - PETCare",
                "email/servicio-finalizado", variables);
    }

    @EventListener
    public void handlePagoConfirmado(PagoConfirmadoEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getReservaCodigo());
        variables.put("monto", event.getMonto() != null ? event.getMonto().toString() : "");
        variables.put("metodo", event.getMetodoPago());
        variables.put("referencia", event.getReferenciaTransaccion() != null ? event.getReferenciaTransaccion() : "");
        emailService.sendEmail(event.getClienteEmail(), "Pago Confirmado - PETCare",
                "email/reserva-confirmada", variables);
    }

    @EventListener
    public void handlePagoReembolsado(PagoReembolsadoEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", event.getClienteNombre());
        variables.put("codigo", event.getReservaCodigo());
        variables.put("monto", event.getMonto() != null ? event.getMonto().toString() : "");
        emailService.sendEmail(event.getClienteEmail(), "Pago Reembolsado - PETCare",
                "email/reserva-cancelada", variables);
    }
}
