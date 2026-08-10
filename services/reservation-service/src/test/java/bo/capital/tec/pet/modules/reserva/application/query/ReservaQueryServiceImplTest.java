package bo.capital.tec.pet.modules.reserva.application.query;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import bo.capital.tec.pet.modules.reserva.domain.service.DisponibilidadCalculator;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaQueryServiceImplTest {

    @Mock
    private ReservaQueryRepository queryRepository;
    @Mock
    private ProviderCatalogClient providerCatalogClient;
    @Mock
    private ReservaQueryMapper queryMapper;

    private ReservaQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReservaQueryServiceImpl(queryRepository, providerCatalogClient,
                new DisponibilidadCalculator(), queryMapper);
    }

    @Test
    void getByIdMapeaYDevuelveDto() {
        Reserva reserva = Reserva.builder().id(4L).build();
        when(queryRepository.findById(4L)).thenReturn(reserva);
        when(queryMapper.toResponseDTO(reserva))
                .thenReturn(ReservaResponseDTO.builder().id(4L).build());

        ReservaResponseDTO result = service.getById(4L);

        assertThat(result.getId()).isEqualTo(4L);
    }

    @Test
    void getByIdLanzaEntityNotFoundSiNoExiste() {
        when(queryRepository.findById(4L)).thenReturn(null);

        assertThatThrownBy(() -> service.getById(4L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAllConFiltroClienteUsaConsultaFiltrada() {
        Reserva r1 = Reserva.builder().id(1L).build();
        when(queryRepository.findFiltered(2L, null, null, null, null, null, 0, 10)).thenReturn(List.of(r1));
        when(queryRepository.countFiltered(2L, null, null, null, null, null)).thenReturn(1L);
        when(queryMapper.toSummaryDTO(r1))
                .thenReturn(ReservaSummaryDTO.builder().id(1L).build());

        PagedResponse<ReservaSummaryDTO> page =
                service.getAll(2L, null, null, null, null, null, 0, 10);

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
        verify(queryRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void getAllConFiltrosCombinaCondiciones() {
        Reserva r1 = Reserva.builder().id(7L).build();
        when(queryRepository.findFiltered(null, 3L, 5L, "PENDIENTE", null, null, 0, 10))
                .thenReturn(List.of(r1));
        when(queryRepository.countFiltered(null, 3L, 5L, "PENDIENTE", null, null)).thenReturn(1L);
        when(queryMapper.toSummaryDTO(r1))
                .thenReturn(ReservaSummaryDTO.builder().id(7L).build());

        PagedResponse<ReservaSummaryDTO> page =
                service.getAll(null, 3L, 5L, "PENDIENTE", null, null, 0, 10);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1L);
        verify(queryRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void getAllSinFiltroUsaFindAll() {
        when(queryRepository.findAll(0, 20)).thenReturn(List.of());
        when(queryRepository.countAll()).thenReturn(0L);

        PagedResponse<ReservaSummaryDTO> page =
                service.getAll(null, null, null, null, null, null, 0, 20);

        assertThat(page.getTotalElements()).isZero();
    }

}
