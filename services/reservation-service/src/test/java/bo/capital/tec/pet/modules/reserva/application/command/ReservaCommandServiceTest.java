package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.EstadoReservaRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaCommandRepository;
import bo.capital.tec.pet.modules.reserva.application.query.ReservaQueryMapper;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaCommandServiceTest {

    @Mock
    private ReservaCommandRepository commandRepository;
    @Mock
    private EstadoReservaRepository estadoReservaRepository;
    @Mock
    private ReservaDatosValidator datosValidator;
    @Mock
    private ReservaQueryMapper queryMapper;
    @Mock
    private ReservaNotifier notifier;

    @InjectMocks
    private ReservaCommandService service;

    @Test
    void crearInsertaPendienteConCodigoYPublicaEvento() {
        CrearReservaCommand comando = buildCrear();
        when(datosValidator.validarParaGuardar(comando)).thenReturn("EN_ESTABLECIMIENTO");
        when(estadoReservaRepository.findByNombre("PENDIENTE")).thenReturn(estado(1L, "PENDIENTE"));
        doAnswer(inv -> {
            Reserva reserva = inv.getArgument(0);
            reserva.setId(5L);
            return reserva;
        }).when(commandRepository).insert(any(Reserva.class));
        when(queryMapper.toResponseDTO(any(Reserva.class)))
                .thenReturn(ReservaResponseDTO.builder().id(5L).build());

        ReservaResponseDTO result = service.crear(comando);

        assertThat(result.getId()).isEqualTo(5L);
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(commandRepository).insert(captor.capture());
        Reserva guardada = captor.getValue();
        assertThat(guardada.getCodigo()).startsWith("RES-");
        assertThat(guardada.getEstadoReservaId()).isEqualTo(1L);
        assertThat(guardada.getModalidadEntrega()).isEqualTo("EN_ESTABLECIMIENTO");
        verify(notifier).publicarCreada(guardada, result);
    }

    @Test
    void actualizarValidaYActualizaDatosDeReservaExistente() {
        Reserva existente = Reserva.builder().id(9L).codigo("RES-ABC").estadoReservaId(1L).build();
        when(commandRepository.findById(9L)).thenReturn(existente);
        when(datosValidator.validarParaGuardar(any(CrearReservaCommand.class))).thenReturn("DOMICILIO");
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(9L).build());

        service.actualizar(9L, buildActualizar());

        assertThat(existente.getModalidadEntrega()).isEqualTo("DOMICILIO");
        assertThat(existente.getPrecioTotal()).isEqualTo(new BigDecimal("90.00"));
        verify(commandRepository).update(existente);
    }

    @Test
    void actualizarLanzaEntityNotFoundSiNoExiste() {
        when(commandRepository.findById(9L)).thenReturn(null);

        assertThatThrownBy(() -> service.actualizar(9L, buildActualizar()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void eliminarAplicaSoftDelete() {
        when(commandRepository.findById(3L)).thenReturn(Reserva.builder().id(3L).build());

        service.eliminar(3L);

        verify(commandRepository).softDelete(3L);
    }

    @Test
    void cancelarCambiaEstadoYPublicaCancelacion() {
        Reserva existente = Reserva.builder()
                .id(7L).codigo("RES-X").clienteId(1L).estadoReservaId(1L).build();
        when(commandRepository.findById(7L)).thenReturn(existente);
        when(estadoReservaRepository.findByNombre("CANCELADA")).thenReturn(estado(4L, "CANCELADA"));
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        service.cancelar(7L, "Cambio de planes");

        verify(commandRepository).updateEstado(7L, 4L);
        assertThat(existente.getEstadoReservaId()).isEqualTo(4L);
        verify(notifier).publicarCancelada(existente, "Cambio de planes");
    }

    @Test
    void confirmarRegistraRespuestaDelProveedor() {
        Reserva existente = Reserva.builder()
                .id(7L).codigo("RES-X").clienteId(1L).estadoReservaId(1L).build();
        when(commandRepository.findById(7L)).thenReturn(existente);
        when(estadoReservaRepository.findByNombre("CONFIRMADA")).thenReturn(estado(2L, "CONFIRMADA"));
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        ConfirmarReservaCommand comando =
                new ConfirmarReservaCommand(7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", "Todo ok");
        service.confirmar(comando);

        verify(commandRepository).updateRespuesta(7L, 3L, 2L, null);
        assertThat(existente.getProveedorId()).isEqualTo(3L);
        assertThat(existente.getEstadoReservaId()).isEqualTo(2L);
        assertThat(existente.getRespuestaEn()).isNotNull();
        verify(notifier).notificarCliente(eq(existente), anyString(), anyString(), eq("EXITO"));
    }

    @Test
    void rechazarGuardaMotivoDeRechazo() {
        Reserva existente = Reserva.builder()
                .id(7L).codigo("RES-X").clienteId(1L).estadoReservaId(1L).build();
        when(commandRepository.findById(7L)).thenReturn(existente);
        when(estadoReservaRepository.findByNombre("RECHAZADA")).thenReturn(estado(3L, "RECHAZADA"));
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        RechazarReservaCommand comando = new RechazarReservaCommand(
                7L, "RES-X", 3L, "Juan", "VetPet SRL", "Consulta", "Horario no disponible");
        service.rechazar(comando);

        verify(commandRepository).updateRespuesta(7L, 3L, 3L, "Horario no disponible");
        assertThat(existente.getMotivoRechazo()).isEqualTo("Horario no disponible");
    }

    @Test
    void compensarPagoFallidoCancelaYPublicaCancelacion() {
        Reserva existente = Reserva.builder()
                .id(7L).codigo("RES-X").clienteId(1L).estadoReservaId(1L).build();
        when(commandRepository.findById(7L)).thenReturn(existente);
        when(estadoReservaRepository.findByNombre("CANCELADA")).thenReturn(estado(4L, "CANCELADA"));
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        service.compensarPagoFallido(new CancelarReservaCommand(7L, "RES-X", "Pago rechazado"));

        verify(commandRepository).updateEstado(7L, 4L);
        verify(notifier).publicarCancelada(existente, "Pago fallido: Pago rechazado");
    }

    @Test
    void compensarYaCanceladaNoRepiteCompensacion() {
        Reserva existente = Reserva.builder()
                .id(7L).codigo("RES-X").clienteId(1L).estadoReservaId(4L).build();
        when(commandRepository.findById(7L)).thenReturn(existente);
        when(estadoReservaRepository.findByNombre("CANCELADA")).thenReturn(estado(4L, "CANCELADA"));
        when(queryMapper.toResponseDTO(existente))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        service.compensarPagoFallido(new CancelarReservaCommand(7L, "RES-X", "Pago rechazado"));

        verify(commandRepository, never()).updateEstado(any(Long.class), any(Long.class));
        verify(notifier, never()).publicarCancelada(any(Reserva.class), anyString());
    }

    private CrearReservaCommand buildCrear() {
        return CrearReservaCommand.builder()
                .clienteId(1L)
                .proveedorId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .fechaReserva(LocalDate.now())
                .fechaInicio(LocalDate.of(2026, 8, 12))
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(10, 0))
                .precioTotal(new BigDecimal("80.00"))
                .build();
    }

    private ActualizarReservaCommand buildActualizar() {
        return ActualizarReservaCommand.builder()
                .clienteId(1L)
                .proveedorId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .fechaReserva(LocalDate.now())
                .fechaInicio(LocalDate.of(2026, 8, 14))
                .horaInicio(LocalTime.of(11, 0))
                .horaFin(LocalTime.of(12, 0))
                .precioTotal(new BigDecimal("90.00"))
                .build();
    }

    private EstadoReserva estado(Long id, String nombre) {
        return EstadoReserva.builder().id(id).nombre(nombre).build();
    }
}
