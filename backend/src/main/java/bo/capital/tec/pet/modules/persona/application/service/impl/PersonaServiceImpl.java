package bo.capital.tec.pet.modules.persona.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.persona.dto.PersonaRequestDTO;
import bo.capital.tec.pet.modules.persona.dto.PersonaResponseDTO;
import bo.capital.tec.pet.modules.persona.dto.PersonaSummaryDTO;
import bo.capital.tec.pet.modules.persona.domain.model.Persona;
import bo.capital.tec.pet.modules.persona.domain.port.PersonaRepository;
import bo.capital.tec.pet.modules.persona.application.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    @Override
    @Transactional
    public PersonaResponseDTO create(PersonaRequestDTO dto) {
        Persona persona = Persona.builder()
                .nombre(dto.getNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .ci(dto.getCi())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .fechaNacimiento(dto.getFechaNacimiento())
                .genero(dto.getGenero())
                .direccionId(dto.getDireccionId())
                .build();
        personaRepository.insert(persona);
        return toResponseDTO(persona);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaResponseDTO getById(Long id) {
        Persona persona = personaRepository.selectById(id);
        if (persona == null) {
            throw new EntityNotFoundException("Persona", id);
        }
        return toResponseDTO(persona);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PersonaSummaryDTO> getAll(String nombre, String ci, String email, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Persona> personas = personaRepository.selectAll(nombre, ci, email, offset, size);
        long total = personaRepository.countAll(nombre, ci, email);
        List<PersonaSummaryDTO> content = personas.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<PersonaSummaryDTO>builder()
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
    public PersonaResponseDTO update(Long id, PersonaRequestDTO dto) {
        Persona persona = personaRepository.selectById(id);
        if (persona == null) {
            throw new EntityNotFoundException("Persona", id);
        }
        persona.setNombre(dto.getNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setCi(dto.getCi());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setGenero(dto.getGenero());
        persona.setDireccionId(dto.getDireccionId());
        personaRepository.update(persona);
        return toResponseDTO(persona);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Persona persona = personaRepository.selectById(id);
        if (persona == null) {
            throw new EntityNotFoundException("Persona", id);
        }
        personaRepository.softDelete(id);
    }

    private PersonaResponseDTO toResponseDTO(Persona persona) {
        return PersonaResponseDTO.builder()
                .id(persona.getId())
                .nombre(persona.getNombre())
                .primerApellido(persona.getPrimerApellido())
                .segundoApellido(persona.getSegundoApellido())
                .ci(persona.getCi())
                .telefono(persona.getTelefono())
                .email(persona.getEmail())
                .fechaNacimiento(persona.getFechaNacimiento())
                .genero(persona.getGenero())
                .direccionId(persona.getDireccionId())
                .createdAt(persona.getCreatedAt())
                .updatedAt(persona.getUpdatedAt())
                .build();
    }

    private PersonaSummaryDTO toSummaryDTO(Persona persona) {
        String nombreCompleto = persona.getNombre() + " " +
                (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "") +
                (persona.getSegundoApellido() != null ? " " + persona.getSegundoApellido() : "");
        return PersonaSummaryDTO.builder()
                .id(persona.getId())
                .nombreCompleto(nombreCompleto.trim())
                .ci(persona.getCi())
                .telefono(persona.getTelefono())
                .email(persona.getEmail())
                .build();
    }
}
