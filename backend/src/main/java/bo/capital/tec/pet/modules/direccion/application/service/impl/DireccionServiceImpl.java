package bo.capital.tec.pet.modules.direccion.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.direccion.dto.DireccionRequestDTO;
import bo.capital.tec.pet.modules.direccion.dto.DireccionResponseDTO;
import bo.capital.tec.pet.modules.direccion.dto.DireccionSummaryDTO;
import bo.capital.tec.pet.modules.direccion.domain.model.Ciudad;
import bo.capital.tec.pet.modules.direccion.domain.model.Direccion;
import bo.capital.tec.pet.modules.direccion.domain.model.Estado;
import bo.capital.tec.pet.modules.direccion.domain.port.CiudadRepository;
import bo.capital.tec.pet.modules.direccion.domain.port.DireccionRepository;
import bo.capital.tec.pet.modules.direccion.domain.port.EstadoRepository;
import bo.capital.tec.pet.modules.direccion.application.service.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final CiudadRepository ciudadRepository;
    private final EstadoRepository estadoRepository;

    @Override
    @Transactional
    public DireccionResponseDTO create(DireccionRequestDTO dto) {
        Direccion direccion = Direccion.builder()
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .piso(dto.getPiso())
                .apartamento(dto.getApartamento())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .referencia(dto.getReferencia())
                .ciudadId(dto.getCiudadId())
                .estadoId(dto.getEstadoId())
                .build();
        direccionRepository.insert(direccion);
        return toResponseDTO(direccion);
    }

    @Override
    @Transactional(readOnly = true)
    public DireccionResponseDTO getById(Long id) {
        Direccion direccion = direccionRepository.selectById(id);
        if (direccion == null) {
            throw new EntityNotFoundException("Direccion", id);
        }
        return toResponseDTO(direccion);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DireccionSummaryDTO> getAll(String calle, Long ciudadId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Direccion> direcciones = direccionRepository.selectByCiudadId(ciudadId);
        List<DireccionSummaryDTO> allContent = direcciones.stream()
                .filter(d -> calle == null || d.getCalle().toLowerCase().contains(calle.toLowerCase()))
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        int total = allContent.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<DireccionSummaryDTO> paged = allContent.subList(fromIndex, toIndex);
        return PagedResponse.<DireccionSummaryDTO>builder()
                .content(paged)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    @Override
    @Transactional
    public DireccionResponseDTO update(Long id, DireccionRequestDTO dto) {
        Direccion direccion = direccionRepository.selectById(id);
        if (direccion == null) {
            throw new EntityNotFoundException("Direccion", id);
        }
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setPiso(dto.getPiso());
        direccion.setApartamento(dto.getApartamento());
        direccion.setLatitud(dto.getLatitud());
        direccion.setLongitud(dto.getLongitud());
        direccion.setReferencia(dto.getReferencia());
        direccion.setCiudadId(dto.getCiudadId());
        direccion.setEstadoId(dto.getEstadoId());
        direccionRepository.update(direccion);
        return toResponseDTO(direccion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Direccion direccion = direccionRepository.selectById(id);
        if (direccion == null) {
            throw new EntityNotFoundException("Direccion", id);
        }
        direccionRepository.softDelete(id);
    }

    private DireccionResponseDTO toResponseDTO(Direccion direccion) {
        String ciudadNombre = "";
        if (direccion.getCiudadId() != null) {
            Ciudad ciudad = ciudadRepository.selectById(direccion.getCiudadId());
            if (ciudad != null) {
                ciudadNombre = ciudad.getNombre();
            }
        }
        String estadoNombre = "";
        if (direccion.getEstadoId() != null) {
            Estado estado = estadoRepository.selectById(direccion.getEstadoId());
            if (estado != null) {
                estadoNombre = estado.getNombre();
            }
        }
        return DireccionResponseDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .piso(direccion.getPiso())
                .apartamento(direccion.getApartamento())
                .latitud(direccion.getLatitud())
                .longitud(direccion.getLongitud())
                .referencia(direccion.getReferencia())
                .ciudadId(direccion.getCiudadId())
                .ciudadNombre(ciudadNombre)
                .estadoId(direccion.getEstadoId())
                .estadoNombre(estadoNombre)
                .build();
    }

    private DireccionSummaryDTO toSummaryDTO(Direccion direccion) {
        String ciudadNombre = "";
        if (direccion.getCiudadId() != null) {
            Ciudad ciudad = ciudadRepository.selectById(direccion.getCiudadId());
            if (ciudad != null) {
                ciudadNombre = ciudad.getNombre();
            }
        }
        return DireccionSummaryDTO.builder()
                .id(direccion.getId())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .ciudadId(direccion.getCiudadId())
                .ciudadNombre(ciudadNombre)
                .build();
    }
}
