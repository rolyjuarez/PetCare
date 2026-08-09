package bo.capital.tec.pet.modules.reserva.service.impl;

import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.SlotDTO;
import bo.capital.tec.pet.modules.reserva.entity.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaCatalogMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.VacunaCatalogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplSlotsTest {

    @Mock
    private ReservaMapper reservaMapper;
    @Mock
    private ReservaCatalogMapper catalogMapper;
    @Mock
    private ProviderCatalogClient providerCatalogClient;
    @Mock
    private VacunaCatalogMapper vacunaCatalogMapper;

    @InjectMocks
    private ReservaServiceImpl reservaService;

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

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Peluquería", 60, new BigDecimal("50.00"), "PELUQUERIA", false);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        Reserva booked = Reserva.builder()
                .id(1L)
                .fechaInicio(desde)
                .horaInicio(LocalTime.of(9, 0)).horaFin(LocalTime.of(10, 0))
                .build();
        when(reservaMapper.selectBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of(booked));

        List<DisponibilidadSlotsDTO> result = reservaService.getSlots(1L, 1L, desde, hasta, null);

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

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Baño", 120, new BigDecimal("80.00"), "PELUQUERIA", false);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        when(reservaMapper.selectBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of());

        List<DisponibilidadSlotsDTO> result = reservaService.getSlots(1L, 1L, desde, hasta, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSlots())
                .extracting(SlotDTO::getHoraInicio)
                .containsExactly("09:00");
    }

    @Test
    void createExigeCertificadoSiServicioLoRequiere() {
        ReservaRequestDTO dto = buildRequest(null);

        when(reservaMapper.selectByClienteId(1L, 0, 100)).thenReturn(List.of());
        when(reservaMapper.selectBooked(1L, 1L, dto.getFechaInicio(), dto.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> reservaService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("certificado de vacunación");
    }

    @Test
    void createRechazaRegistroSinCertificadoAdjunto() {
        ReservaRequestDTO dto = buildRequest(10L);

        when(reservaMapper.selectByClienteId(1L, 0, 100)).thenReturn(List.of());
        when(reservaMapper.selectBooked(1L, 1L, dto.getFechaInicio(), dto.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(true);
        when(vacunaCatalogMapper.selectRegistroVacunacion(10L)).thenReturn(
                RegistroVacunacionInfoDTO.builder()
                        .id(10L).mascotaId(1L).vacunaId(1L)
                        .fechaAplicacion(LocalDate.of(2026, 1, 15))
                        .certificadoUrl(null)
                        .build());

        assertThatThrownBy(() -> reservaService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("certificado");
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

        ServicioInfoDTO servicio = new ServicioInfoDTO(1L, "Baño", 60, new BigDecimal("80.00"), "PELUQUERIA", true);
        when(providerCatalogClient.getServicio(1L)).thenReturn(servicio);

        when(reservaMapper.selectBooked(1L, 1L, desde, hasta, null)).thenReturn(List.of());

        List<DisponibilidadSlotsDTO> result = reservaService.getSlots(1L, 1L, desde, hasta, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequiereCertificado()).isTrue();
    }

    @Test
    void createExigeCoordenadasParaModalidadDomicilio() {
        ReservaRequestDTO dto = buildRequest(null);
        dto.setModalidadEntrega("DOMICILIO");

        when(reservaMapper.selectByClienteId(1L, 0, 100)).thenReturn(List.of());
        when(reservaMapper.selectBooked(1L, 1L, dto.getFechaInicio(), dto.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);
        when(providerCatalogClient.isModalidadValida(1L, "DOMICILIO")).thenReturn(true);

        assertThatThrownBy(() -> reservaService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("latitud");
    }

    @Test
    void createRechazaModalidadNoDisponibleParaServicio() {
        ReservaRequestDTO dto = buildRequest(null);
        dto.setModalidadEntrega("ENTREGA_RAPIDA");

        when(reservaMapper.selectByClienteId(1L, 0, 100)).thenReturn(List.of());
        when(reservaMapper.selectBooked(1L, 1L, dto.getFechaInicio(), dto.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> reservaService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está disponible");
    }

    private ReservaRequestDTO buildRequest(Long registroVacunacionId) {
        return ReservaRequestDTO.builder()
                .clienteId(1L)
                .proveedorId(1L)
                .servicioId(1L)
                .mascotaId(1L)
                .fechaReserva(LocalDate.now())
                .fechaInicio(LocalDate.of(2026, 8, 12))
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(10, 0))
                .precioTotal(new BigDecimal("80.00"))
                .registroVacunacionId(registroVacunacionId)
                .build();
    }
}
