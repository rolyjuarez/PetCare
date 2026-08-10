package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.application.query.ReservaQueryService;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservaQueryControllerTest {

    @Mock
    private ReservaQueryService reservaQueryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReservaQueryController(reservaQueryService))
                .setConversionService(new DefaultFormattingConversionService())
                .build();
    }

    @Test
    void getAllPasaFiltrosYDevuelvePagina() throws Exception {
        PagedResponse<ReservaSummaryDTO> page = PagedResponse.<ReservaSummaryDTO>builder()
                .content(List.of()).page(0).size(10).totalElements(0).totalPages(0)
                .first(true).last(true).build();
        when(reservaQueryService.getAll(2L, 3L, 5L, "PENDIENTE",
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 12), 0, 10)).thenReturn(page);

        mockMvc.perform(get("/reservas")
                        .param("clienteId", "2")
                        .param("mascotaId", "3")
                        .param("servicioId", "5")
                        .param("estado", "PENDIENTE")
                        .param("fechaDesde", "2026-08-10")
                        .param("fechaHasta", "2026-08-12")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getSlotsParseaFechasIso() throws Exception {
        when(reservaQueryService.getSlots(1L, 1L,
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 13), null))
                .thenReturn(List.of(new DisponibilidadSlotsDTO()));

        mockMvc.perform(get("/reservas/slots")
                        .param("proveedorId", "1")
                        .param("servicioId", "1")
                        .param("desde", "2026-08-10")
                        .param("hasta", "2026-08-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getByIdDevuelveDto() throws Exception {
        when(reservaQueryService.getById(4L))
                .thenReturn(ReservaResponseDTO.builder().id(4L).codigo("RES-X").build());

        mockMvc.perform(get("/reservas/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.codigo").value("RES-X"));
    }
}
