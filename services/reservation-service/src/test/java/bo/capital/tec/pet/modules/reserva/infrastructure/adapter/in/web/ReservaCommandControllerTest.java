package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.modules.reserva.application.command.ActualizarReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.CrearReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.ReservaCommandUseCase;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservaCommandControllerTest {

    @Mock
    private ReservaCommandUseCase reservaCommandUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservaCommandController(reservaCommandUseCase)).build();
    }

    @Test
    void crearResponde201ConLaReserva() throws Exception {
        when(reservaCommandUseCase.crear(any(CrearReservaCommand.class)))
                .thenReturn(ReservaResponseDTO.builder().id(5L).codigo("RES-ABC").build());

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"proveedorId\":1,\"servicioId\":1,\"mascotaId\":1,"
                                + "\"fechaReserva\":\"2026-08-10\",\"fechaInicio\":\"2026-08-12\","
                                + "\"horaInicio\":\"09:00\",\"horaFin\":\"10:00\",\"precioTotal\":80.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.codigo").value("RES-ABC"));
    }

    @Test
    void actualizarDelegaEnElCasoDeUso() throws Exception {
        when(reservaCommandUseCase.actualizar(eq(7L), any(ActualizarReservaCommand.class)))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        mockMvc.perform(put("/reservas/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"servicioId\":1,\"mascotaId\":1,"
                                + "\"fechaReserva\":\"2026-08-10\",\"fechaInicio\":\"2026-08-14\","
                                + "\"horaInicio\":\"11:00\",\"horaFin\":\"12:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7));
    }

    @Test
    void eliminarDelegaEnElCasoDeUso() throws Exception {
        mockMvc.perform(delete("/reservas/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(reservaCommandUseCase).eliminar(3L);
    }

    @Test
    void cancelarConMotivoDelega() throws Exception {
        when(reservaCommandUseCase.cancelar(7L, "Cambio de planes"))
                .thenReturn(ReservaResponseDTO.builder().id(7L).build());

        mockMvc.perform(put("/reservas/7/cancelar").param("motivo", "Cambio de planes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7));

        verify(reservaCommandUseCase).cancelar(7L, "Cambio de planes");
    }
}
