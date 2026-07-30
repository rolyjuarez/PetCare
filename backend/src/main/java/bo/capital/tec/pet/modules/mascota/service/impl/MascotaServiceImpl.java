package bo.capital.tec.pet.modules.mascota.service.impl;

import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.entity.Cliente;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.dto.MascotaRequestDTO;
import bo.capital.tec.pet.modules.mascota.dto.MascotaResponseDTO;
import bo.capital.tec.pet.modules.mascota.dto.MascotaSummaryDTO;
import bo.capital.tec.pet.modules.mascota.entity.Especie;
import bo.capital.tec.pet.modules.mascota.entity.Mascota;
import bo.capital.tec.pet.modules.mascota.entity.Raza;
import bo.capital.tec.pet.modules.mascota.event.MascotaActualizadaEvent;
import bo.capital.tec.pet.modules.mascota.event.MascotaCreadaEvent;
import bo.capital.tec.pet.modules.mascota.mapper.EspecieMapper;
import bo.capital.tec.pet.modules.mascota.mapper.MascotaMapper;
import bo.capital.tec.pet.modules.mascota.mapper.RazaMapper;
import bo.capital.tec.pet.modules.mascota.service.MascotaService;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.entity.Persona;
import bo.capital.tec.pet.modules.reserva.api.ReservaApi;
import bo.capital.tec.pet.modules.usuario.api.UsuarioApi;
import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.modules.vacuna.api.RegistroVacunacionApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {

    private final MascotaMapper mascotaMapper;
    private final EspecieMapper especieMapper;
    private final RazaMapper razaMapper;
    private final ClienteApi clienteApi;
    private final PersonaApi personaApi;
    private final UsuarioApi usuarioApi;
    private final RegistroVacunacionApi registroVacunacionApi;
    private final ReservaApi reservaApi;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public MascotaResponseDTO create(MascotaRequestDTO dto) {
        Mascota mascota = Mascota.builder()
                .nombre(dto.getNombre())
                .fechaNacimiento(dto.getFechaNacimiento())
                .genero(dto.getGenero())
                .peso(dto.getPeso())
                .color(dto.getColor())
                .imagenUrl(dto.getImagenUrl())
                .especieId(dto.getEspecieId())
                .razaId(dto.getRazaId())
                .clienteId(dto.getClienteId())
                .build();
        mascotaMapper.insert(mascota);
        MascotaResponseDTO response = toResponseDTO(mascota);
        try {
            eventPublisher.publish(new MascotaCreadaEvent(
                    mascota.getId(), mascota.getNombre(), mascota.getClienteId()
            ));
        } catch (Exception e) {
            log.warn("Error publishing MascotaCreadaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO getById(Long id) {
        Mascota mascota = mascotaMapper.selectById(id);
        if (mascota == null) {
            throw new EntityNotFoundException("Mascota", id);
        }
        return toResponseDTO(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MascotaSummaryDTO> getAll(String nombre, Long clienteId, Long especieId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<MascotaSummaryDTO> content = mascotaMapper.selectAll(nombre, clienteId, especieId, offset, size);
        long total = mascotaMapper.countAll(nombre, clienteId, especieId);
        return PagedResponse.<MascotaSummaryDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaSummaryDTO> getByClienteId(Long clienteId) {
        return mascotaMapper.selectByClienteId(clienteId, 0, 1000);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaSummaryDTO> getByUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioApi.selectById(usuarioId);
        if (usuario == null || usuario.getPersonaId() == null) {
            return List.of();
        }
        Cliente cliente = clienteApi.findByPersonaId(usuario.getPersonaId());
        if (cliente == null) {
            return List.of();
        }
        return mascotaMapper.selectByClienteId(cliente.getId(), 0, 1000);
    }

    @Override
    @Transactional
    public MascotaResponseDTO update(Long id, MascotaRequestDTO dto) {
        Mascota mascota = mascotaMapper.selectById(id);
        if (mascota == null) {
            throw new EntityNotFoundException("Mascota", id);
        }
        mascota.setNombre(dto.getNombre());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setGenero(dto.getGenero());
        mascota.setPeso(dto.getPeso());
        mascota.setColor(dto.getColor());
        mascota.setImagenUrl(dto.getImagenUrl());
        mascota.setEspecieId(dto.getEspecieId());
        mascota.setRazaId(dto.getRazaId());
        mascota.setClienteId(dto.getClienteId());
        mascotaMapper.update(mascota);
        MascotaResponseDTO response = toResponseDTO(mascota);
        try {
            eventPublisher.publish(new MascotaActualizadaEvent(
                    mascota.getId(), mascota.getNombre(), mascota.getClienteId()
            ));
        } catch (Exception e) {
            log.warn("Error publishing MascotaActualizadaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Mascota mascota = mascotaMapper.selectById(id);
        if (mascota == null) {
            throw new EntityNotFoundException("Mascota", id);
        }
        registroVacunacionApi.softDeleteByMascotaId(id);
        reservaApi.softDeleteByMascotaId(id);
        mascotaMapper.softDelete(id);
    }

    private MascotaResponseDTO toResponseDTO(Mascota mascota) {
        Especie especie = especieMapper.selectById(mascota.getEspecieId());
        Raza raza = razaMapper.selectById(mascota.getRazaId());
        String clienteNombre = "";
        if (mascota.getClienteId() != null) {
            Cliente cliente = clienteApi.selectById(mascota.getClienteId());
            if (cliente != null && cliente.getPersonaId() != null) {
                Persona persona = personaApi.selectById(cliente.getPersonaId());
                if (persona != null) {
                    clienteNombre = persona.getNombre() + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
            }
        }
        return MascotaResponseDTO.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .fechaNacimiento(mascota.getFechaNacimiento())
                .genero(mascota.getGenero())
                .peso(mascota.getPeso())
                .color(mascota.getColor())
                .imagenUrl(mascota.getImagenUrl())
                .especieId(mascota.getEspecieId())
                .especieNombre(especie != null ? especie.getNombre() : "")
                .razaId(mascota.getRazaId())
                .razaNombre(raza != null ? raza.getNombre() : "")
                .clienteId(mascota.getClienteId())
                .clienteNombre(clienteNombre.trim())
                .createdAt(mascota.getCreatedAt())
                .build();
    }
}
