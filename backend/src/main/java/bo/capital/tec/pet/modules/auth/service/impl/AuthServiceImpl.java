package bo.capital.tec.pet.modules.auth.service.impl;

import bo.capital.tec.pet.modules.auth.dto.*;
import bo.capital.tec.pet.modules.auth.event.UsuarioRegistradoEvent;
import bo.capital.tec.pet.modules.auth.service.AuthService;
import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.entity.Cliente;
import bo.capital.tec.pet.common.email.EmailService;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exceptions.BusinessException;
import bo.capital.tec.pet.modules.direccion.api.DireccionApi;
import bo.capital.tec.pet.modules.direccion.entity.Direccion;
import bo.capital.tec.pet.modules.notificacion.api.NotificacionApi;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.entity.Persona;
import bo.capital.tec.pet.modules.rol.api.RolApi;
import bo.capital.tec.pet.modules.rol.entity.Rol;
import bo.capital.tec.pet.security.JwtTokenProvider;
import bo.capital.tec.pet.modules.usuario.api.UsuarioApi;
import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.modules.usuario.entity.UsuarioRol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioApi usuarioApi;
    private final PersonaApi personaApi;
    private final DireccionApi direccionApi;
    private final ClienteApi clienteApi;
    private final RolApi rolApi;
    private final NotificacionApi notificacionApi;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioApi.findByUsername(dto.getUsername());
        if (usuario == null) {
            throw new BusinessException("Credenciales incorrectas");
        }
        if (!usuario.getActivo()) {
            throw new BusinessException("Usuario desactivado");
        }
        if (usuario.getBloqueado()) {
            throw new BusinessException("Usuario bloqueado");
        }
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            usuarioApi.incrementFailedAttempts(usuario.getId());
            if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() + 1 >= 5) {
                usuarioApi.blockUser(usuario.getId());
            }
            throw new BusinessException("Credenciales incorrectas");
        }
        usuarioApi.resetFailedAttempts(usuario.getId());
        usuarioApi.updateLastAccess(usuario.getId());

        List<String> roles = usuarioApi.findRolesByUsuarioId(usuario.getId());
        String accessToken = jwtTokenProvider.generateAccessToken(usuario.getUsername(), usuario.getId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario.getUsername());
        usuarioApi.updateTokenRefresh(usuario.getId(), refreshToken);

        List<String> permissions = usuarioApi.findPermissionsByUsuarioId(usuario.getId());

        String nombre = "";
        if (usuario.getPersona() != null) {
            nombre = (usuario.getPersona().getNombre() != null ? usuario.getPersona().getNombre() : "") + " " +
                     (usuario.getPersona().getPrimerApellido() != null ? usuario.getPersona().getPrimerApellido() : "");
            nombre = nombre.trim();
        }

        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombre)
                .roles(roles)
                .permissions(permissions)
                .build();

        log.info("Login exitoso: {}", usuario.getUsername());

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .userInfo(userInfo)
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO refresh(RefreshTokenRequestDTO dto) {
        if (!jwtTokenProvider.validateToken(dto.getRefreshToken())) {
            throw new BusinessException("Refresh token invalido o expirado");
        }
        String username = jwtTokenProvider.getUsernameFromToken(dto.getRefreshToken());
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario == null || !usuario.getActivo()) {
            throw new BusinessException("Usuario no valido");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username, usuario.getId(), usuarioApi.findRolesByUsuarioId(usuario.getId()));
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        usuarioApi.updateTokenRefresh(usuario.getId(), newRefreshToken);

        List<String> roles = usuarioApi.findRolesByUsuarioId(usuario.getId());
        List<String> permissions = usuarioApi.findPermissionsByUsuarioId(usuario.getId());

        String nombre = "";
        if (usuario.getPersona() != null) {
            nombre = (usuario.getPersona().getNombre() != null ? usuario.getPersona().getNombre() : "") + " " +
                     (usuario.getPersona().getPrimerApellido() != null ? usuario.getPersona().getPrimerApellido() : "");
            nombre = nombre.trim();
        }

        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombre)
                .roles(roles)
                .permissions(permissions)
                .build();

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .userInfo(userInfo)
                .build();
    }

    @Override
    @Transactional
    public void logout(String username) {
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario != null) {
            usuarioApi.updateTokenRefresh(usuario.getId(), null);
            log.info("Logout exitoso: {}", username);
        }
    }

    @Override
    @Transactional
    public void register(RegisterRequestDTO dto) {
        if (usuarioApi.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("El nombre de usuario ya esta en uso");
        }
        if (personaApi.selectByCi(dto.getCi()) != null) {
            throw new BusinessException("El CI ya esta registrado");
        }
        if (personaApi.selectByEmail(dto.getEmail()) != null) {
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
        direccionApi.insert(direccion);

        Persona persona = Persona.builder()
                .nombre(dto.getNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .ci(dto.getCi())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .fechaNacimiento(dto.getFechaNacimiento())
                .genero(dto.getGenero())
                .direccionId(direccion.getId())
                .build();
        personaApi.insert(persona);

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .personaId(persona.getId())
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .build();
        usuarioApi.insert(usuario);

        Rol clienteRol = rolApi.selectByNombre("CLIENTE");
        if (clienteRol != null) {
            UsuarioRol usuarioRol = UsuarioRol.builder()
                    .usuarioId(usuario.getId())
                    .rolId(clienteRol.getId())
                    .build();
            usuarioApi.insertUsuarioRol(usuarioRol);
        }

        Cliente cliente = Cliente.builder()
                .personaId(persona.getId())
                .usuarioId(usuario.getId())
                .build();
        clienteApi.insert(cliente);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", dto.getNombre() + " " + dto.getPrimerApellido());
            variables.put("username", dto.getUsername());
            variables.put("email", dto.getEmail());
            emailService.sendEmail(dto.getEmail(), "Bienvenido a PETCare", "email/bienvenida", variables);
        } catch (Exception e) {
            log.warn("Error enviando email de bienvenida: {}", e.getMessage());
        }

        try {
            Notificacion notificacion = Notificacion.builder()
                    .usuarioId(usuario.getId())
                    .titulo("Bienvenido a PETCare")
                    .mensaje("Su cuenta ha sido creada exitosamente. Ya puede acceder al sistema.")
                    .tipo("EXITO")
                    .leida(false)
                    .build();
            notificacionApi.insert(notificacion);
        } catch (Exception e) {
            log.warn("Error creando notificacion de bienvenida: {}", e.getMessage());
        }

        try {
            String nombreCompleto = (dto.getNombre() != null ? dto.getNombre() : "") + " "
                    + (dto.getPrimerApellido() != null ? dto.getPrimerApellido() : "");
            eventPublisher.publish(new UsuarioRegistradoEvent(
                    usuario.getId(), dto.getUsername(), persona.getId(),
                    nombreCompleto.trim(), dto.getEmail(), cliente.getId()
            ));
        } catch (Exception e) {
            log.warn("Error publishing UsuarioRegistradoEvent: {}", e.getMessage());
        }

        log.info("Registro exitoso: {} - CLIENTE", dto.getUsername());
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        Persona persona = personaApi.selectByEmail(email);
        if (persona == null) {
            log.warn("Solicitud de recuperacion para email no registrado: {}", email);
            return;
        }
        Usuario usuario = usuarioApi.findByUsername(
                findUsernameByPersonaId(persona.getId()));
        if (usuario == null || !usuario.getActivo()) {
            log.warn("Usuario inactivo para email: {}", email);
            return;
        }

        String resetToken = jwtTokenProvider.generateResetToken(email);
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        Map<String, Object> variables = new HashMap<>();
        variables.put("nombre", persona.getNombre() + " " + persona.getPrimerApellido());
        variables.put("resetLink", resetLink);
        emailService.sendEmailSync(email, "Recuperar contrasena - PETCare", "email/recuperar-password", variables);

        log.info("Email de recuperacion enviado a: {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (!jwtTokenProvider.isResetToken(token)) {
            throw new BusinessException("Token invalido");
        }
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException("Token expirado o invalido");
        }

        String email = jwtTokenProvider.getEmailFromResetToken(token);
        Persona persona = personaApi.selectByEmail(email);
        if (persona == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        String username = findUsernameByPersonaId(persona.getId());
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioApi.update(usuario);

        log.info("Contrasena restablecida para: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfile(String username) {
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }
        Persona persona = usuario.getPersona() != null ? usuario.getPersona() : personaApi.selectById(usuario.getPersonaId());
        List<String> roles = usuarioApi.findRolesByUsuarioId(usuario.getId());
        return buildProfileResponse(usuario, persona, roles);
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(String username, UpdateProfileRequestDTO dto) {
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }
        Persona persona = usuario.getPersona() != null ? usuario.getPersona() : personaApi.selectById(usuario.getPersonaId());
        if (persona == null) {
            throw new BusinessException("Persona no encontrada");
        }
        if (dto.getNombre() != null) persona.setNombre(dto.getNombre());
        if (dto.getPrimerApellido() != null) persona.setPrimerApellido(dto.getPrimerApellido());
        if (dto.getSegundoApellido() != null) persona.setSegundoApellido(dto.getSegundoApellido());
        if (dto.getTelefono() != null) persona.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) persona.setEmail(dto.getEmail());
        personaApi.update(persona);
        List<String> roles = usuarioApi.findRolesByUsuarioId(usuario.getId());
        log.info("Perfil actualizado para: {}", username);
        return buildProfileResponse(usuario, persona, roles);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDTO dto) {
        Usuario usuario = usuarioApi.findByUsername(username);
        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getPassword())) {
            throw new BusinessException("La contrasena actual no es correcta");
        }
        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioApi.update(usuario);
        log.info("Contrasena cambiada para: {}", username);
    }

    private ProfileResponseDTO buildProfileResponse(Usuario usuario, Persona persona, List<String> roles) {
        ProfileResponseDTO.ProfileResponseDTOBuilder builder = ProfileResponseDTO.builder()
                .usuarioId(usuario.getId())
                .username(usuario.getUsername())
                .roles(roles);
        if (persona != null) {
            builder.nombre(persona.getNombre())
                    .primerApellido(persona.getPrimerApellido())
                    .segundoApellido(persona.getSegundoApellido())
                    .ci(persona.getCi())
                    .telefono(persona.getTelefono())
                    .email(persona.getEmail())
                    .fechaNacimiento(persona.getFechaNacimiento())
                    .genero(persona.getGenero());
        }
        return builder.build();
    }

    private String findUsernameByPersonaId(Long personaId) {
        Usuario usuario = usuarioApi.selectByPersonaId(personaId);
        return usuario != null ? usuario.getUsername() : null;
    }
}
