package bo.capital.tec.pet.modules.cliente.application.service.impl;

import bo.capital.tec.pet.modules.cliente.dto.ClienteRequestDTO;
import bo.capital.tec.pet.modules.cliente.dto.ClienteResponseDTO;
import bo.capital.tec.pet.modules.cliente.dto.ClienteSummaryDTO;
import bo.capital.tec.pet.modules.cliente.domain.model.Cliente;
import bo.capital.tec.pet.modules.cliente.domain.port.ClienteRepository;
import bo.capital.tec.pet.modules.cliente.application.service.ClienteService;
import bo.capital.tec.pet.common.exceptions.BusinessException;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.domain.model.Persona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaApi personaApi;

    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        Cliente existing = clienteRepository.findByPersonaId(dto.getPersonaId());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe un cliente para la persona con id: " + dto.getPersonaId());
        }
        Cliente cliente = Cliente.builder()
                .personaId(dto.getPersonaId())
                .usuarioId(dto.getUsuarioId())
                .notas(dto.getNotas())
                .build();
        clienteRepository.insert(cliente);
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO getById(Long id) {
        Cliente cliente = clienteRepository.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO getByPersonaId(Long personaId) {
        Cliente cliente = clienteRepository.findByPersonaId(personaId);
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
        List<ClienteSummaryDTO> content = clienteRepository.selectAll(nombre, ci, offset, size);
        long total = clienteRepository.countAll(nombre, ci);
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
        Cliente cliente = clienteRepository.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        cliente.setPersonaId(dto.getPersonaId());
        cliente.setUsuarioId(dto.getUsuarioId());
        cliente.setNotas(dto.getNotas());
        clienteRepository.update(cliente);
        return toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cliente cliente = clienteRepository.selectById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente", id);
        }
        clienteRepository.softDelete(id);
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        Persona persona = personaApi.selectById(cliente.getPersonaId());
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
