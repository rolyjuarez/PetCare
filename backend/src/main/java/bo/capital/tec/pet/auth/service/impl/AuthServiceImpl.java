package bo.capital.tec.pet.auth.service.impl;

import bo.capital.tec.pet.auth.dto.*;
import bo.capital.tec.pet.auth.service.AuthService;
import bo.capital.tec.pet.cliente.entity.Cliente;
import bo.capital.tec.pet.cliente.mapper.ClienteMapper;
import bo.capital.tec.pet.common.email.EmailService;
import bo.capital.tec.pet.common.exceptions.BusinessException;
import bo.capital.tec.pet.direccion.entity.Direccion;
import bo.capital.tec.pet.direccion.mapper.DireccionMapper;
import bo.capital.tec.pet.notificacion.entity.Notificacion;
import bo.capital.tec.pet.notificacion.mapper.NotificacionMapper;
import bo.capital.tec.pet.persona.entity.Persona;
import bo.capital.tec.pet.persona.mapper.PersonaMapper;
import bo.capital.tec.pet.rol.entity.Rol;
import bo.capital.tec.pet.rol.mapper.RolMapper;
import bo.capital.tec.pet.security.JwtTokenProvider;
import bo.capital.tec.pet.usuario.entity.Usuario;
import bo.capital.tec.pet.usuario.entity.UsuarioRol;
import bo.capital.tec.pet.usuario.mapper.UsuarioMapper;
import bo.capital.tec.pet.usuario.mapper.UsuarioRolMapper;
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

    private final UsuarioMapper usuarioMapper;
    private final PersonaMapper personaMapper;
    private final DireccionMapper direccionMapper;
    private final ClienteMapper clienteMapper;
    private final RolMapper rolMapper;
    private final UsuarioRolMapper usuarioRolMapper;
    private final NotificacionMapper notificacionMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioMapper.findByUsername(dto.getUsername());
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
            usuarioMapper.incrementFailedAttempts(usuario.getId());
            if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() + 1 >= 5) {
                usuarioMapper.blockUser(usuario.getId());
            }
            throw new BusinessException("Credenciales incorrectas");
        }
        usuarioMapper.resetFailedAttempts(usuario.getId());
        usuarioMapper.updateLastAccess(usuario.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(usuario.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario.getUsername());
        usuarioMapper.updateTokenRefresh(usuario.getId(), refreshToken);

        List<String> roles = usuarioMapper.findRolesByUsuarioId(usuario.getId());
        List<String> permissions = usuarioMapper.findPermissionsByUsuarioId(usuario.getId());

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
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null || !usuario.getActivo()) {
            throw new BusinessException("Usuario no valido");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        usuarioMapper.updateTokenRefresh(usuario.getId(), newRefreshToken);

        List<String> roles = usuarioMapper.findRolesByUsuarioId(usuario.getId());
        List<String> permissions = usuarioMapper.findPermissionsByUsuarioId(usuario.getId());

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
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario != null) {
            usuarioMapper.updateTokenRefresh(usuario.getId(), null);
            log.info("Logout exitoso: {}", username);
        }
    }

    @Override
    @Transactional
    public void register(RegisterRequestDTO dto) {
        if (usuarioMapper.findByUsername(dto.getUsername()) != null) {
            throw new BusinessException("El nombre de usuario ya esta en uso");
        }
        if (personaMapper.selectByCi(dto.getCi()) != null) {
            throw new BusinessException("El CI ya esta registrado");
        }
        if (personaMapper.selectByEmail(dto.getEmail()) != null) {
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
                .genero(dto.getGenero())
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

        Rol clienteRol = rolMapper.selectByNombre("CLIENTE");
        if (clienteRol != null) {
            UsuarioRol usuarioRol = UsuarioRol.builder()
                    .usuarioId(usuario.getId())
                    .rolId(clienteRol.getId())
                    .build();
            usuarioRolMapper.insert(usuarioRol);
        }

        Cliente cliente = Cliente.builder()
                .personaId(persona.getId())
                .usuarioId(usuario.getId())
                .build();
        clienteMapper.insert(cliente);

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
            notificacionMapper.insert(notificacion);
        } catch (Exception e) {
            log.warn("Error creando notificacion de bienvenida: {}", e.getMessage());
        }

        log.info("Registro exitoso: {} - CLIENTE", dto.getUsername());
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        Persona persona = personaMapper.selectByEmail(email);
        if (persona == null) {
            log.warn("Solicitud de recuperacion para email no registrado: {}", email);
            return;
        }
        Usuario usuario = usuarioMapper.findByUsername(
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
        Persona persona = personaMapper.selectByEmail(email);
        if (persona == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        String username = findUsernameByPersonaId(persona.getId());
        Usuario usuario = usuarioMapper.findByUsername(username);
        if (usuario == null) {
            throw new BusinessException("Usuario no encontrado");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioMapper.update(usuario);

        log.info("Contrasena restablecida para: {}", username);
    }

    private String findUsernameByPersonaId(Long personaId) {
        Usuario usuario = usuarioMapper.selectByPersonaId(personaId);
        return usuario != null ? usuario.getUsername() : null;
    }
}
