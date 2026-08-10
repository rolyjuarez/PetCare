package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.reserva.application.query.ReservaQueryMapper;
import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.EstadoReservaRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaCommandRepository;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Casos de uso de escritura: crea, modifica y cambia el estado de las reservas,
 * delegando las reglas de negocio al dominio y publicando los eventos de
 * dominio correspondientes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaCommandService implements ReservaCommandUseCase {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_RECHAZADA = "RECHAZADA";

    private final ReservaCommandRepository commandRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final ReservaDatosValidator datosValidator;
    private final ReservaQueryMapper queryMapper;
    private final ReservaNotifier notifier;

    @Override
    @Transactional
    public ReservaResponseDTO crear(CrearReservaCommand comando) {
        String modalidad = datosValidator.validarParaGuardar(comando);
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Reserva reserva = Reserva.builder()
                .codigo(codigo)
                .clienteId(comando.getClienteId())
                .proveedorId(comando.getProveedorId())
                .servicioId(comando.getServicioId())
                .mascotaId(comando.getMascotaId())
                .estadoReservaId(estadoId(ESTADO_PENDIENTE))
                .registroVacunacionId(comando.getRegistroVacunacionId())
                .modalidadEntrega(modalidad)
                .fechaReserva(comando.getFechaReserva())
                .fechaInicio(comando.getFechaInicio())
                .fechaFin(comando.getFechaFin())
                .horaInicio(comando.getHoraInicio())
                .horaFin(comando.getHoraFin())
                .latitud(comando.getLatitud())
                .longitud(comando.getLongitud())
                .direccionReferencia(comando.getDireccionReferencia())
                .notas(comando.getNotas())
                .precioTotal(comando.getPrecioTotal())
                .build();
        commandRepository.insert(reserva);

        ReservaResponseDTO response = queryMapper.toResponseDTO(reserva);
        notifier.publicarCreada(reserva, response);
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO actualizar(Long id, ActualizarReservaCommand comando) {
        Reserva reserva = requireReserva(id);
        String modalidad = datosValidator.validarParaGuardar(comando.toCrearReservaCommand());
        reserva.setClienteId(comando.getClienteId());
        reserva.setProveedorId(comando.getProveedorId());
        reserva.setServicioId(comando.getServicioId());
        reserva.setMascotaId(comando.getMascotaId());
        reserva.setRegistroVacunacionId(comando.getRegistroVacunacionId());
        reserva.setModalidadEntrega(modalidad);
        reserva.setFechaReserva(comando.getFechaReserva());
        reserva.setFechaInicio(comando.getFechaInicio());
        reserva.setFechaFin(comando.getFechaFin());
        reserva.setHoraInicio(comando.getHoraInicio());
        reserva.setHoraFin(comando.getHoraFin());
        reserva.setLatitud(comando.getLatitud());
        reserva.setLongitud(comando.getLongitud());
        reserva.setDireccionReferencia(comando.getDireccionReferencia());
        reserva.setNotas(comando.getNotas());
        reserva.setPrecioTotal(comando.getPrecioTotal());
        commandRepository.update(reserva);
        return queryMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        requireReserva(id);
        commandRepository.softDelete(id);
    }

    @Override
    @Transactional
    public ReservaResponseDTO cancelar(Long id, String motivo) {
        Reserva reserva = requireReserva(id);
        Long estadoId = estadoId(ESTADO_CANCELADA);
        commandRepository.updateEstado(id, estadoId);
        reserva.setEstadoReservaId(estadoId);
        ReservaResponseDTO response = queryMapper.toResponseDTO(reserva);
        notifier.publicarCancelada(reserva, motivo);
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO confirmar(ConfirmarReservaCommand comando) {
        Reserva reserva = requireReserva(comando.getReservaId());
        Long estadoId = estadoId(ESTADO_CONFIRMADA);
        commandRepository.updateRespuesta(reserva.getId(), comando.getProveedorId(), estadoId, null);
        reserva.setProveedorId(comando.getProveedorId());
        reserva.setEstadoReservaId(estadoId);
        reserva.setRespuestaEn(LocalDateTime.now());
        log.info("Reserva {} aceptada por proveedor {}", reserva.getCodigo(), comando.getProveedorId());

        String comentario = comando.getComentario();
        String comentarioHtml = comentario != null && !comentario.isBlank()
                ? "<p><strong>Comentario del proveedor:</strong> " + comentario + "</p>" : "";
        notifier.notificarCliente(reserva, "Reserva confirmada",
                "Su reserva " + reserva.getCodigo() + " ha sido aceptada por el proveedor."
                        + (comentario != null && !comentario.isBlank() ? " Comentario: " + comentario : ""), "EXITO");
        notifier.enviarEmailCliente(reserva, "Reserva Confirmada - PETCare",
                "<h2>¡Reserva confirmada!</h2>"
                        + "<p>Su reserva <strong>" + reserva.getCodigo() + "</strong> ("
                        + comando.getServicioNombre() + ") ha sido aceptada por "
                        + comando.getProveedorEmpresa() + ".</p>"
                        + "<p>Fecha: " + reserva.getFechaInicio()
                        + (reserva.getHoraInicio() != null ? " a las " + reserva.getHoraInicio() : "") + "</p>"
                        + comentarioHtml);
        notifier.publicarConfirmada(reserva);
        return queryMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO rechazar(RechazarReservaCommand comando) {
        Reserva reserva = requireReserva(comando.getReservaId());
        Long estadoId = estadoId(ESTADO_RECHAZADA);
        commandRepository.updateRespuesta(reserva.getId(), comando.getProveedorId(), estadoId, comando.getMotivo());
        reserva.setProveedorId(comando.getProveedorId());
        reserva.setEstadoReservaId(estadoId);
        reserva.setMotivoRechazo(comando.getMotivo());
        reserva.setRespuestaEn(LocalDateTime.now());
        log.info("Reserva {} rechazada por proveedor {}: {}", reserva.getCodigo(),
                comando.getProveedorId(), comando.getMotivo());

        notifier.notificarCliente(reserva, "Reserva rechazada",
                "Su reserva " + reserva.getCodigo() + " fue rechazada"
                        + (comando.getMotivo() != null && !comando.getMotivo().isBlank()
                        ? ": " + comando.getMotivo() : "") + ".", "ADVERTENCIA");
        notifier.enviarEmailCliente(reserva, "Reserva Rechazada - PETCare",
                "<h2>Reserva rechazada</h2>"
                        + "<p>Su reserva <strong>" + reserva.getCodigo() + "</strong> ("
                        + comando.getServicioNombre() + ") fue rechazada"
                        + (comando.getMotivo() != null && !comando.getMotivo().isBlank()
                        ? " por el siguiente motivo: " + comando.getMotivo() : "") + ".</p>");
        return queryMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO compensarPagoFallido(CancelarReservaCommand comando) {
        Reserva reserva = requireReserva(comando.getReservaId());
        Long estadoCancelada = estadoId(ESTADO_CANCELADA);
        if (estadoCancelada.equals(reserva.getEstadoReservaId())) {
            log.debug("Reserva {} ya cancelada, compensación omitida", reserva.getCodigo());
            return queryMapper.toResponseDTO(reserva);
        }
        commandRepository.updateEstado(reserva.getId(), estadoCancelada);
        reserva.setEstadoReservaId(estadoCancelada);
        String motivo = comando.getMotivo() != null && !comando.getMotivo().isBlank()
                ? comando.getMotivo() : "El pago no pudo completarse";
        log.info("Reserva {} compensada por pago fallido: {}", reserva.getCodigo(), motivo);

        notifier.notificarCliente(reserva, "Reserva cancelada por pago fallido",
                "Su reserva " + reserva.getCodigo()
                        + " fue cancelada porque el pago no pudo completarse. Motivo: " + motivo + ".",
                "ADVERTENCIA");
        notifier.enviarEmailCliente(reserva, "Reserva Cancelada - PETCare",
                "<h2>Reserva cancelada</h2>"
                        + "<p>Su reserva <strong>" + reserva.getCodigo()
                        + "</strong> fue cancelada porque el pago no pudo completarse.</p>"
                        + "<p><strong>Motivo:</strong> " + motivo + "</p>");
        notifier.publicarCancelada(reserva, "Pago fallido: " + motivo);
        return queryMapper.toResponseDTO(reserva);
    }

    private Reserva requireReserva(Long id) {
        Reserva reserva = commandRepository.findById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        return reserva;
    }

    private Long estadoId(String nombre) {
        EstadoReserva estado = estadoReservaRepository.findByNombre(nombre);
        return estado != null ? estado.getId() : 1L;
    }
}
