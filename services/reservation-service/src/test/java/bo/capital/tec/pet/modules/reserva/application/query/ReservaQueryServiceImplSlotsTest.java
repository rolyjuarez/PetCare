package bo.capital.tec.pet.modules.reserva.application.query;

import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import bo.capital.tec.pet.modules.reserva.domain.service.DisponibilidadCalculator;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.SlotDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaQueryServiceImplSlotsTest {

    @Mock
    private ReservaQueryRepository queryRepository;
    @Mock
    private ProviderCatalogClient providerCatalogClient;
    @Mock
    private ReservaQueryMapper queryMapper;

    private ReservaQueryServiceImpl queryService;

    @BeforeEach
    void setUp() {
        queryService = new ReservaQueryServiceImpl(queryRepository, providerCatalogClient,
                new DisponibilidadCalculator(), queryMapper);
    }

    @Test
    void getSlotsGeneraSlotsLibresExcluyendoReservados() {
        LocalDate desde = LocalDate.of(2026, 8, 10);
        LocalDate hasta = LocalDate.of(2026, 8, 10);

        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L)
                .diaSemana(1)
                .horaInicio(LocalTime.of(8, 0)).horaFin(LocalTime.of(12, 0))
                .build();
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of(window));
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);
        when(providerCatalogClient.getModalidades(1L)).thenReturn(List.of());

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Peluquería", 60,
                new BigDecimal("50.00"), "PELUQUERIA", false);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        Reserva booked = Reserva.builder()
                .id(1L)
                .fechaInicio(desde)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();
        when(queryRepository.findBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of(booked));

        List<DisponibilidadSlotsDTO> result = queryService.getSlots(1L, 1L, desde, hasta, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFecha()).isEqualTo(desde);
        assertThat(result.get(0).getSlots())
                .extracting(SlotDTO::getHoraInicio)
                .containsExactly("08:00", "10:00", "11:00");
    }

    @Test
    void getSlotsRespetaDuracionDelServicio() {
        LocalDate desde = LocalDate.of(2026, 8, 12);
        LocalDate hasta = LocalDate.of(2026, 8, 12);

        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L)
                .diaSemana(3)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(11, 0))
                .build();
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of(window));
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);
        when(providerCatalogClient.getModalidades(1L)).thenReturn(List.of());

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Baño", 120,
                new BigDecimal("80.00"), "PELUQUERIA", false);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        when(queryRepository.findBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of());

        List<DisponibilidadSlotsDTO> result = queryService.getSlots(1L, 1L, desde, hasta, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSlots())
                .extracting(SlotDTO::getHoraInicio)
                .containsExactly("09:00");
    }

    @Test
    void getSlotsMarcaRequiereCertificado() {
        LocalDate desde = LocalDate.of(2026, 8, 12);
        LocalDate hasta = LocalDate.of(2026, 8, 12);

        Disponibilidad window = Disponibilidad.builder()
                .proveedorId(1L).servicioId(1L)
                .diaSemana(3)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of(window));
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(true);
        when(providerCatalogClient.getModalidades(1L)).thenReturn(List.of());

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Baño", 60,
                new BigDecimal("80.00"), "PELUQUERIA", true);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        when(queryRepository.findBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of());

        List<DisponibilidadSlotsDTO> result = queryService.getSlots(1L, 1L, desde, hasta, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequiereCertificado()).isTrue();
    }
}
