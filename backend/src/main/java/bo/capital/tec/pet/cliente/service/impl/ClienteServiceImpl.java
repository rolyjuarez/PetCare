package bo.capital.tec.pet.cliente.service.impl;

import bo.capital.tec.pet.cliente.dto.ClienteRequestDTO;
import bo.capital.tec.pet.cliente.dto.ClienteResponseDTO;
import bo.capital.tec.pet.cliente.dto.ClienteSummaryDTO;
import bo.capital.tec.pet.cliente.entity.Cliente;
import bo.capital.tec.pet.cliente.mapper.ClienteMapper;
import bo.capital.tec.pet.cliente.service.ClienteService;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.persona.entity.Persona;
import bo.capital.tec.pet.persona.mapper.PersonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteMapper clienteMapper;
    private final PersonaMapper personaMapper;

    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        Cliente existing = clienteMapper.findByPersonaId(dto.getPersonaId());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe un cliente para la persona con id: " + dto.getPersonaId());
        }
        Cliente cliente = Cliente.builder()
                .personaId(dto.getPersonaId())
                .usuarioId(dto.getUsuarioId())
                .notas(dto.getNotas())
                .build();
        clienteMapper.insert(cliente);
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO getById(Long id) {
        Cliente cliente = clienteMapper.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO getByPersonaId(Long personaId) {
        Cliente cliente = clienteMapper.findByPersonaId(personaId);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", "personaId", personaId);
        }
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ClienteSummaryDTO> getAll(String nombre, String ci, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<ClienteSummaryDTO> content = clienteMapper.selectAll(nombre, ci, offset, size);
        long total = clienteMapper.countAll(nombre, ci);
        return PagedResponse.<ClienteSummaryDTO>builder()
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
    @Transactional
    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteMapper.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        cliente.setPersonaId(dto.getPersonaId());
        cliente.setUsuarioId(dto.getUsuarioId());
        cliente.setNotas(dto.getNotas());
        clienteMapper.update(cliente);
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cliente cliente = clienteMapper.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        clienteMapper.softDelete(id);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        Persona persona = personaMapper.selectById(cliente.getPersonaId());
        String personaNombre = "";
        String personaApellido = "";
        String personaCi = "";
        String personaTelefono = "";
        String personaEmail = "";
        if (persona != null) {
            personaNombre = persona.getNombre() != null ? persona.getNombre() : "";
            personaApellido = persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "";
            personaCi = persona.getCi() != null ? persona.getCi() : "";
            personaTelefono = persona.getTelefono() != null ? persona.getTelefono() : "";
            personaEmail = persona.getEmail() != null ? persona.getEmail() : "";
        }
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .personaId(cliente.getPersonaId())
                .personaNombre(personaNombre)
                .personaApellido(personaApellido)
                .personaCi(personaCi)
                .personaTelefono(personaTelefono)
                .personaEmail(personaEmail)
                .usuarioId(cliente.getUsuarioId())
                .notas(cliente.getNotas())
                .createdAt(cliente.getCreatedAt())
                .build();
    }
}
