package bo.capital.tec.pet.modules.proveedor.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.client.ReservationInternalClient;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullCreateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullUpdateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorSummaryDTO;
import bo.capital.tec.pet.modules.proveedor.entity.Disponibilidad;
import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;
import bo.capital.tec.pet.modules.proveedor.entity.ProveedorEspecialidad;
import bo.capital.tec.pet.modules.proveedor.mapper.DisponibilidadMapper;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorEspecialidadMapper;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorMapper;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorCrudService;
import bo.capital.tec.pet.modules.proveedorservicio.service.ProveedorServicioService;
import bo.capital.tec.pet.modules.soporte.entity.Direccion;
import bo.capital.tec.pet.modules.soporte.entity.Persona;
import bo.capital.tec.pet.modules.soporte.entity.Rol;
import bo.capital.tec.pet.modules.soporte.entity.Servicio;
import bo.capital.tec.pet.modules.soporte.entity.Usuario;
import bo.capital.tec.pet.modules.soporte.entity.UsuarioRol;
import bo.capital.tec.pet.modules.soporte.mapper.DireccionMapper;
import bo.capital.tec.pet.modules.soporte.mapper.PersonaMapper;
import bo.capital.tec.pet.modules.soporte.mapper.RolMapper;
import bo.capital.tec.pet.modules.soporte.mapper.ServicioMapper;
import bo.capital.tec.pet.modules.soporte.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorCrudServiceImpl implements ProveedorCrudService {

    private static final String[] DIAS_SEMANA = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};

    private final ProveedorMapper proveedorMapper;
    private final ProveedorEspecialidadMapper proveedorEspecialidadMapper;
    private final DisponibilidadMapper disponibilidadMapper;
    private final PersonaMapper personaMapper;
    private final ServicioMapper servicioMapper;
    private final DireccionMapper direccionMapper;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProveedorServicioService proveedorServicioService;
    private final ReservationInternalClient reservationClient;

    @Override
    @Transactional
    public ProveedorResponseDTO create(ProveedorRequestDTO dto) {
        Proveedor proveedor = Proveedor.builder()
                .personaId(dto.getPersonaId())
                .usuarioId(dto.getUsuarioId())
                .empresa(dto.getEmpresa())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .radioCoberturaKm(dto.getRadioCoberturaKm())
                .descripcion(dto.getDescripcion())
                .verificado(false)
                .calificacion(BigDecimal.ZERO)
                .build();
        proveedorMapper.insert(proveedor);
        if (dto.getServicioIds() != null) {
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .requiereCertificado(false)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO createFull(ProveedorFullCreateDTO dto) {
        if (usuarioMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("El nombre de usuario ya esta en uso");
        }
        if (personaMapper.selectByCi(dto.getCi()) != null) {
            throw new BusinessException("El CI ya esta registrado");
        }
        if (dto.getEmail() != null && personaMapper.selectByEmail(dto.getEmail()) != null) {
            throw new BusinessException("El email ya esta registrado");
        }

        Direccion direccion = Direccion.builder()
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .referencia(dto.getReferencia())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .ciudadId(dto.getCiudadId())
                .build();
        direccionMapper.insert(direccion);

        Persona persona = Persona.builder()
                .nombre(dto.getNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .ci(dto.getCi())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .fechaNacimiento(dto.getFechaNacimiento())
                .genero(dto.getGenero() != null ? dto.getGenero() : "O")
                .direccionId(direccion.getId())
                .build();
        personaMapper.insert(persona);

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .personaId(persona.getId())
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .build();
        usuarioMapper.insert(usuario);

        Rol proveedorRol = rolMapper.selectByNombre("PROVEEDOR");
        if (proveedorRol != null) {
            UsuarioRol usuarioRol = UsuarioRol.builder()
                    .usuarioId(usuario.getId())
                    .rolId(proveedorRol.getId())
                    .build();
            usuarioMapper.insertUsuarioRol(usuarioRol);
        }

        Proveedor proveedor = Proveedor.builder()
                .personaId(persona.getId())
                .usuarioId(usuario.getId())
                .empresa(dto.getEmpresa())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .radioCoberturaKm(dto.getRadioCoberturaKm())
                .descripcion(dto.getDescripcion())
                .verificado(false)
                .calificacion(BigDecimal.ZERO)
                .build();
        proveedorMapper.insert(proveedor);

        if (dto.getServicioIds() != null) {
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .requiereCertificado(false)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }

        if (dto.getDisponibilidades() != null) {
            for (ProveedorFullCreateDTO.DisponibilidadItem item : dto.getDisponibilidades()) {
                Disponibilidad disp = Disponibilidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(item.getServicioId())
                        .diaSemana(item.getDiaSemana())
                        .horaInicio(item.getHoraInicio())
                        .horaFin(item.getHoraFin())
                        .activo(true)
                        .build();
                disponibilidadMapper.insert(disp);
            }
        }

        log.info("Proveedor creado: {} - {}", dto.getUsername(), dto.getNombre());
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO getById(Long id) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO getByUsuarioId(Long usuarioId) {
        Proveedor proveedor = proveedorMapper.findByUsuarioId(usuarioId);
        if (proveedor == null) {
            throw new EntityNotFoundException("El proveedor no fue encontrado para el usuario: " + usuarioId);
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO setRequiereCertificado(Long proveedorId, Long servicioId, Boolean requiere) {
        Proveedor proveedor = proveedorMapper.selectById(proveedorId);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", proveedorId);
        }
        boolean requiereFlag = Boolean.TRUE.equals(requiere);
        ProveedorEspecialidad existing = proveedorEspecialidadMapper.selectByProveedorYServicio(proveedorId, servicioId);
        if (existing == null) {
            ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                    .proveedorId(proveedorId)
                    .servicioId(servicioId)
                    .requiereCertificado(requiereFlag)
                    .build();
            proveedorEspecialidadMapper.insert(pe);
        } else {
            proveedorEspecialidadMapper.updateRequiereCertificado(proveedorId, servicioId, requiereFlag);
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProveedorSummaryDTO> getAll(String nombre, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Proveedor> proveedores = proveedorMapper.selectAll(nombre, null, offset, size);
        long total = proveedorMapper.countAll(nombre, null);
        List<ProveedorSummaryDTO> content = proveedores.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<ProveedorSummaryDTO>builder()
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
    public ProveedorResponseDTO update(Long id, ProveedorRequestDTO dto) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        proveedor.setPersonaId(dto.getPersonaId());
        proveedor.setUsuarioId(dto.getUsuarioId());
        proveedor.setEmpresa(dto.getEmpresa());
        proveedor.setLatitud(dto.getLatitud());
        proveedor.setLongitud(dto.getLongitud());
        proveedor.setRadioCoberturaKm(dto.getRadioCoberturaKm());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedorMapper.update(proveedor);
        if (dto.getServicioIds() != null) {
            proveedorEspecialidadMapper.deleteByProveedorId(id);
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .requiereCertificado(false)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }
        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO updateFull(Long id, ProveedorFullUpdateDTO dto) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }

        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        if (persona == null) {
            throw new EntityNotFoundException("Persona", proveedor.getPersonaId());
        }

        if (!persona.getCi().equals(dto.getCi())) {
            Persona existing = personaMapper.selectByCi(dto.getCi());
            if (existing != null) {
                throw new BusinessException("El CI ya esta registrado");
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(persona.getEmail())) {
            Persona existing = personaMapper.selectByEmail(dto.getEmail());
            if (existing != null) {
                throw new BusinessException("El email ya esta registrado");
            }
        }

        if (persona.getDireccionId() != null) {
            Direccion direccion = direccionMapper.selectById(persona.getDireccionId());
            if (direccion != null) {
                direccion.setCalle(dto.getCalle());
                direccion.setNumero(dto.getNumero());
                direccion.setReferencia(dto.getReferencia());
                direccion.setLatitud(dto.getLatitud());
                direccion.setLongitud(dto.getLongitud());
                direccion.setCiudadId(dto.getCiudadId());
                direccionMapper.update(direccion);
            }
        } else {
            Direccion direccion = Direccion.builder()
                    .calle(dto.getCalle())
                    .numero(dto.getNumero())
                    .referencia(dto.getReferencia())
                    .latitud(dto.getLatitud())
                    .longitud(dto.getLongitud())
                    .ciudadId(dto.getCiudadId())
                    .build();
            direccionMapper.insert(direccion);
            persona.setDireccionId(direccion.getId());
        }

        persona.setNombre(dto.getNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setCi(dto.getCi());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());
        personaMapper.update(persona);

        proveedor.setEmpresa(dto.getEmpresa());
        proveedor.setLatitud(dto.getLatitud());
        proveedor.setLongitud(dto.getLongitud());
        proveedor.setRadioCoberturaKm(dto.getRadioCoberturaKm());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedorMapper.update(proveedor);

        if (dto.getServicioIds() != null) {
            proveedorEspecialidadMapper.deleteByProveedorId(id);
            for (Long servicioId : dto.getServicioIds()) {
                ProveedorEspecialidad pe = ProveedorEspecialidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(servicioId)
                        .requiereCertificado(false)
                        .build();
                proveedorEspecialidadMapper.insert(pe);
            }
        }

        if (dto.getDisponibilidades() != null) {
            disponibilidadMapper.deleteByProveedorId(id);
            for (ProveedorFullCreateDTO.DisponibilidadItem item : dto.getDisponibilidades()) {
                Disponibilidad disp = Disponibilidad.builder()
                        .proveedorId(proveedor.getId())
                        .servicioId(item.getServicioId())
                        .diaSemana(item.getDiaSemana())
                        .horaInicio(item.getHoraInicio())
                        .horaFin(item.getHoraFin())
                        .activo(true)
                        .build();
                disponibilidadMapper.insert(disp);
            }
        }

        return toResponseDTO(proveedor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Proveedor proveedor = proveedorMapper.selectById(id);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", id);
        }
        proveedorEspecialidadMapper.deleteByProveedorId(id);
        disponibilidadMapper.deleteByProveedorId(id);
        proveedorMapper.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReservas(Long proveedorId) {
        try {
            return reservationClient.countReservasByProveedor(proveedorId) > 0;
        } catch (Exception e) {
            log.warn("No se pudo consultar las reservas del proveedor {}: {}", proveedorId, e.getMessage());
            return false;
        }
    }

    private ProveedorResponseDTO toResponseDTO(Proveedor proveedor) {
        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        String personaNombre = "";
        String personaTelefono = "";
        String personaEmail = "";
        String ci = "";
        String nombre = "";
        String primerApellido = "";
        String segundoApellido = "";
        java.time.LocalDate fechaNacimiento = null;
        String genero = "O";
        Long direccionId = null;
        String calle = "";
        String numero = "";
        String referencia = "";
        Long ciudadId = null;

        if (persona != null) {
            nombre = persona.getNombre() != null ? persona.getNombre() : "";
            primerApellido = persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "";
            segundoApellido = persona.getSegundoApellido() != null ? persona.getSegundoApellido() : "";
            personaNombre = (nombre + " " + primerApellido).trim();
            personaTelefono = persona.getTelefono() != null ? persona.getTelefono() : "";
            personaEmail = persona.getEmail() != null ? persona.getEmail() : "";
            ci = persona.getCi() != null ? persona.getCi() : "";
            fechaNacimiento = persona.getFechaNacimiento();
            genero = persona.getGenero() != null ? persona.getGenero() : "O";
            direccionId = persona.getDireccionId();

            if (direccionId != null) {
                Direccion direccion = direccionMapper.selectById(direccionId);
                if (direccion != null) {
                    calle = direccion.getCalle() != null ? direccion.getCalle() : "";
                    numero = direccion.getNumero() != null ? direccion.getNumero() : "";
                    referencia = direccion.getReferencia() != null ? direccion.getReferencia() : "";
                    ciudadId = direccion.getCiudadId();
                }
            }
        }

        List<ProveedorEspecialidad> especialidades = proveedorEspecialidadMapper.selectByProveedorId(proveedor.getId());
        List<String> especialidadNombres = new ArrayList<>();
        List<Long> servicioIds = new ArrayList<>();
        List<Long> servicioIdsRequeridos = new ArrayList<>();
        for (ProveedorEspecialidad pe : especialidades) {
            servicioIds.add(pe.getServicioId());
            if (Boolean.TRUE.equals(pe.getRequiereCertificado())) {
                servicioIdsRequeridos.add(pe.getServicioId());
            }
            Servicio servicio = servicioMapper.selectById(pe.getServicioId());
            if (servicio != null) {
                especialidadNombres.add(servicio.getNombre());
            }
        }

        List<Disponibilidad> dispList = disponibilidadMapper.selectByProveedorYServicio(proveedor.getId(), null);
        List<ProveedorResponseDTO.DisponibilidadDTO> dispDTOs = new ArrayList<>();
        for (Disponibilidad d : dispList) {
            String servicioNombre = "";
            Servicio svc = servicioMapper.selectById(d.getServicioId());
            if (svc != null) {
                servicioNombre = svc.getNombre();
            }
            String diaNombre = "";
            if (d.getDiaSemana() != null && d.getDiaSemana() >= 0 && d.getDiaSemana() <= 6) {
                diaNombre = DIAS_SEMANA[d.getDiaSemana()];
            }
            dispDTOs.add(ProveedorResponseDTO.DisponibilidadDTO.builder()
                    .id(d.getId())
                    .servicioId(d.getServicioId())
                    .servicioNombre(servicioNombre)
                    .diaSemana(d.getDiaSemana())
                    .diaSemanaNombre(diaNombre)
                    .horaInicio(d.getHoraInicio() != null ? d.getHoraInicio().toString() : "")
                    .horaFin(d.getHoraFin() != null ? d.getHoraFin().toString() : "")
                    .build());
        }

        return ProveedorResponseDTO.builder()
                .id(proveedor.getId())
                .personaId(proveedor.getPersonaId())
                .usuarioId(proveedor.getUsuarioId())
                .personaNombre(personaNombre)
                .personaTelefono(personaTelefono)
                .personaEmail(personaEmail)
                .ci(ci)
                .nombre(nombre)
                .primerApellido(primerApellido)
                .segundoApellido(segundoApellido)
                .fechaNacimiento(fechaNacimiento)
                .genero(genero)
                .direccionId(direccionId)
                .calle(calle)
                .numero(numero)
                .referencia(referencia)
                .ciudadId(ciudadId)
                .empresa(proveedor.getEmpresa())
                .latitud(proveedor.getLatitud())
                .longitud(proveedor.getLongitud())
                .radioCoberturaKm(proveedor.getRadioCoberturaKm())
                .descripcion(proveedor.getDescripcion())
                .verificado(proveedor.getVerificado())
                .calificacion(proveedor.getCalificacion())
                .especialidades(especialidadNombres)
                .servicioIds(servicioIds)
                .servicioIdsRequeridos(servicioIdsRequeridos)
                .servicios(proveedorServicioService.listActivosByProveedor(proveedor.getId()))
                .disponibilidades(dispDTOs)
                .createdAt(proveedor.getCreatedAt())
                .build();
    }

    private ProveedorSummaryDTO toSummaryDTO(Proveedor proveedor) {
        Persona persona = personaMapper.selectById(proveedor.getPersonaId());
        String nombre = "";
        String telefono = "";
        String email = "";
        if (persona != null) {
            nombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                    (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
            telefono = persona.getTelefono() != null ? persona.getTelefono() : "";
            email = persona.getEmail() != null ? persona.getEmail() : "";
        }
        List<ProveedorEspecialidad> especialidades = proveedorEspecialidadMapper.selectByProveedorId(proveedor.getId());
        List<String> especialidadNombres = new ArrayList<>();
        for (ProveedorEspecialidad pe : especialidades) {
            Servicio servicio = servicioMapper.selectById(pe.getServicioId());
            if (servicio != null) {
                especialidadNombres.add(servicio.getNombre());
            }
        }
        return ProveedorSummaryDTO.builder()
                .id(proveedor.getId())
                .nombre(nombre.trim())
                .empresa(proveedor.getEmpresa())
                .personaTelefono(telefono)
                .personaEmail(email)
                .descripcion(proveedor.getDescripcion())
                .radioCoberturaKm(proveedor.getRadioCoberturaKm())
                .calificacion(proveedor.getCalificacion())
                .verificado(proveedor.getVerificado())
                .especialidades(especialidadNombres)
                .build();
    }
}
