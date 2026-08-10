package bo.capital.tec.pet.modules.reserva.application.query;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.client.ProviderCatalogClient;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.reserva.domain.model.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.model.SlotHorario;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import bo.capital.tec.pet.modules.reserva.domain.service.DisponibilidadCalculator;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.SlotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservaQueryServiceImpl implements ReservaQueryService {

    private final ReservaQueryRepository queryRepository;
    private final ProviderCatalogClient providerCatalogClient;
    private final DisponibilidadCalculator disponibilidadCalculator;
    private final ReservaQueryMapper queryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadSlotsDTO> getSlots(Long proveedorId, Long servicioId,
                                                 LocalDate desde, LocalDate hasta, Long excluirReservaId) {
        LocalDate from = desde != null ? desde : LocalDate.now();
        LocalDate to = hasta != null ? hasta : from.plusDays(13);
        if (to.isBefore(from)) {
            to = from;
        }
        ServicioInfoDTO servicio = providerCatalogClient.getServicio(servicioId);
        int duracion = servicio != null && servicio.getDuracionMinutos() != null
                ? servicio.getDuracionMinutos() : 60;
        List<ModalidadInfoDTO> modalidades = providerCatalogClient.getModalidades(servicioId);
        List<Disponibilidad> windows = providerCatalogClient.getDisponibilidades(proveedorId, servicioId);
        List<Reserva> booked = queryRepository.findBooked(proveedorId, servicioId, from, to, excluirReservaId);
        boolean requiereCertificado = Boolean.TRUE.equals(
                providerCatalogClient.getRequiereCertificado(proveedorId, servicioId));

        Map<LocalDate, List<SlotHorario>> slotsPorDia =
                disponibilidadCalculator.calcular(windows, booked, from, to, duracion);

        List<DisponibilidadSlotsDTO> result = new ArrayList<>();
        slotsPorDia.forEach((fecha, slots) -> result.add(DisponibilidadSlotsDTO.builder()
                .fecha(fecha)
                .slots(slots.stream()
                        .map(s -> SlotDTO.builder()
                                .horaInicio(s.inicio().toString())
                                .horaFin(s.fin().toString())
                                .build())
                        .toList())
                .requiereCertificado(requiereCertificado)
                .modalidades(modalidades)
                .build()));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO getById(Long id) {
        Reserva reserva = requireReserva(id);
        return queryMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
                                                   String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                                   int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Reserva> reservas;
        long total;
        boolean filtros = clienteId != null || mascotaId != null || servicioId != null
                || (estado != null && !estado.isBlank()) || fechaDesde != null || fechaHasta != null;
        if (filtros) {
            reservas = queryRepository.findFiltered(clienteId, mascotaId, servicioId,
                    estado, fechaDesde, fechaHasta, offset, size);
            total = queryRepository.countFiltered(clienteId, mascotaId, servicioId,
                    estado, fechaDesde, fechaHasta);
        } else {
            reservas = queryRepository.findAll(offset, size);
            total = queryRepository.countAll();
        }
        return buildPage(reservas, total, page, size);
    }

    private PagedResponse<ReservaSummaryDTO> buildPage(List<Reserva> reservas, long total, int page, int size) {
        List<ReservaSummaryDTO> content = reservas.stream().map(queryMapper::toSummaryDTO).toList();
        return PagedResponse.<ReservaSummaryDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    private Reserva requireReserva(Long id) {
        Reserva reserva = queryRepository.findById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        return reserva;
    }
}
