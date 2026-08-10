package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservaInternaControllerTest {

    @Mock
    private ReservaQueryRepository queryRepository;
    @Mock
    private CatalogInfoRepository catalogInfoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ReservaInternaController(queryRepository, catalogInfoRepository)).build();
    }

    @Test
    void registroVacunacionDelegaEnCatalogo() throws Exception {
        when(catalogInfoRepository.findRegistroVacunacion(10L))
                .thenReturn(RegistroVacunacionInfoDTO.builder().id(10L).mascotaId(1L).build());

        mockMvc.perform(get("/interna/registros-vacunacion/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.mascotaId").value(1));
    }

    @Test
    void ubicacionDevuelveCoordenadasDeLaReserva() throws Exception {
        Reserva reserva = Reserva.builder()
                .id(7L)
                .latitud(new BigDecimal("-17.7833"))
                .longitud(new BigDecimal("-63.1822"))
                .direccionReferencia("Av. Banzer, Casa 123")
                .build();
        when(queryRepository.findById(7L)).thenReturn(reserva);

        mockMvc.perform(get("/interna/reservas/7/ubicacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservaId").value(7))
                .andExpect(jsonPath("$.direccionReferencia").value("Av. Banzer, Casa 123"));
    }

    @Test
    void ubicacionDeReservaInexistenteDevuelveNull() throws Exception {
        when(queryRepository.findById(9L)).thenReturn(null);

        mockMvc.perform(get("/interna/reservas/9/ubicacion"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void countByProveedorDevuelveConteo() throws Exception {
        when(queryRepository.countByProveedorId(3L)).thenReturn(5L);

        mockMvc.perform(get("/interna/reservas/count-by-proveedor/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }
}
