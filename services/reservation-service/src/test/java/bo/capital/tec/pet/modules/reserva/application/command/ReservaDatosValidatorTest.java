package bo.capital.tec.pet.modules.reserva.application.command;

import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaCommandRepository;
import bo.capital.tec.pet.modules.reserva.domain.service.ReservaValidator;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaDatosValidatorTest {

    @Mock
    private ReservaCommandRepository commandRepository;
    @Mock
    private ProviderCatalogClient providerCatalogClient;
    @Mock
    private CatalogInfoRepository catalogInfoRepository;
    @Spy
    private ReservaValidator validator;

    @InjectMocks
    private ReservaDatosValidator datosValidator;

    @Test
    void crearExigeCertificadoSiServicioLoRequiere() {
        CrearReservaCommand comando = buildCommand(null);

        when(commandRepository.findActivasPorCliente(1L)).thenReturn(List.of());
        when(commandRepository.findBooked(1L, 1L, comando.getFechaInicio(), comando.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> datosValidator.validarParaGuardar(comando))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("certificado de vacunación");
    }

    @Test
    void crearRechazaRegistroSinCertificadoAdjunto() {
        CrearReservaCommand comando = buildCommand(10L);

        when(commandRepository.findActivasPorCliente(1L)).thenReturn(List.of());
        when(commandRepository.findBooked(1L, 1L, comando.getFechaInicio(), comando.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(true);
        when(catalogInfoRepository.findRegistroVacunacion(10L)).thenReturn(
                RegistroVacunacionInfoDTO.builder()
                        .id(10L).mascotaId(1L).vacunaId(1L)
                        .fechaAplicacion(LocalDate.of(2026, 1, 15))
                        .certificadoUrl(null)
                        .build());

        assertThatThrownBy(() -> datosValidator.validarParaGuardar(comando))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("certificado");
    }

    @Test
    void crearExigeCoordenadasParaModalidadDomicilio() {
        CrearReservaCommand comando = buildCommand(null);
        comando.setModalidadEntrega("DOMICILIO");

        when(commandRepository.findActivasPorCliente(1L)).thenReturn(List.of());
        when(commandRepository.findBooked(1L, 1L, comando.getFechaInicio(), comando.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);
        when(providerCatalogClient.isModalidadValida(1L, "DOMICILIO")).thenReturn(true);

        assertThatThrownBy(() -> datosValidator.validarParaGuardar(comando))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("latitud");
    }

    @Test
    void crearRechazaModalidadNoDisponibleParaServicio() {
        CrearReservaCommand comando = buildCommand(null);
        comando.setModalidadEntrega("ENTREGA_RAPIDA");

        when(commandRepository.findActivasPorCliente(1L)).thenReturn(List.of());
        when(commandRepository.findBooked(1L, 1L, comando.getFechaInicio(), comando.getFechaInicio(), null))
                .thenReturn(List.of());
        when(providerCatalogClient.getDisponibilidades(1L, 1L)).thenReturn(List.of());
        when(providerCatalogClient.getRequiereCertificado(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> datosValidator.validarParaGuardar(comando))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no está disponible");
    }

    @Test
    void crearRechazaSolapamientoConReservaActiva() {
        CrearReservaCommand comando = buildCommand(null);

        Reserva activa = Reserva.builder()
                .id(2L).mascotaId(1L).servicioId(1L)
                .fechaInicio(comando.getFechaInicio())
                .horaInicio(LocalTime.of(9, 30)).horaFin(LocalTime.of(10, 30))
                .build();
        when(commandRepository.findActivasPorCliente(1L)).thenReturn(List.of(activa));

        assertThatThrownBy(() -> datosValidator.validarParaGuardar(comando))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una reserva activa");
    }

    private CrearReservaCommand buildCommand(Long registroVacunacionId) {
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
                .registroVacunacionId(registroVacunacionId)
                .build();
    }
}
