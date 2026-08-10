package bo.capital.tec.pet.saga.application.service;

import bo.capital.tec.pet.saga.domain.port.out.EventoProcesadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotenciaSagaServiceTest {

    @Mock
    private EventoProcesadoRepository repositorio;

    @InjectMocks
    private IdempotenciaSagaService servicio;

    @Test
    void marcaComoProcesadoCuandoElEventoEsNuevo() {
        when(repositorio.existsByEventId("ev-1")).thenReturn(false);

        boolean resultado = servicio.marcarProcesado("ev-1", "ReservaCreadaEvent", 1L);

        assertThat(resultado).isTrue();
        verify(repositorio).insert("ev-1", "ReservaCreadaEvent", 1L, "saga-orchestrator");
    }

    @Test
    void ignoraCuandoElEventoYaExiste() {
        when(repositorio.existsByEventId("ev-2")).thenReturn(true);

        boolean resultado = servicio.marcarProcesado("ev-2", "PagoProcesadoEvent", 1L);

        assertThat(resultado).isFalse();
    }

    @Test
    void ignoraCuandoOtroProcesoInsertaPrimero() {
        when(repositorio.existsByEventId("ev-3")).thenReturn(false);
        doThrow(new DuplicateKeyException("duplicado"))
                .when(repositorio).insert(eq("ev-3"), eq("PagoFallidoEvent"), eq(1L), eq("saga-orchestrator"));

        boolean resultado = servicio.marcarProcesado("ev-3", "PagoFallidoEvent", 1L);

        assertThat(resultado).isFalse();
    }
}
