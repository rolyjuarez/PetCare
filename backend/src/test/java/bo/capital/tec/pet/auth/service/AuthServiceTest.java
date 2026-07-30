package bo.capital.tec.pet.auth.service;

import bo.capital.tec.pet.modules.auth.dto.*;
import bo.capital.tec.pet.modules.auth.service.impl.AuthServiceImpl;
import bo.capital.tec.pet.common.exceptions.BusinessException;
import bo.capital.tec.pet.security.JwtTokenProvider;
import bo.capital.tec.pet.modules.usuario.entity.Usuario;
import bo.capital.tec.pet.modules.usuario.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .username("admin")
                .password("$2a$encoded")
                .personaId(1L)
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .build();
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin").password("123456").build();
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);
        when(passwordEncoder.matches("123456", "$2a$encoded")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken("admin")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken("admin")).thenReturn("refresh-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(3600000L);
        when(usuarioMapper.findRolesByUsuarioId(1L)).thenReturn(List.of("ADMIN"));
        when(usuarioMapper.findPermissionsByUsuarioId(1L)).thenReturn(List.of("READ", "WRITE"));

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(usuarioMapper).resetFailedAttempts(1L);
        verify(usuarioMapper).updateLastAccess(1L);
        verify(usuarioMapper).updateTokenRefresh(eq(1L), eq("refresh-token"));
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("unknown").password("123456").build();
        when(usuarioMapper.findByUsername("unknown")).thenReturn(null);

        assertThrows(BusinessException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenUserIsInactive() {
        usuario.setActivo(false);
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin").password("123456").build();
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);

        assertThrows(BusinessException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenUserIsBlocked() {
        usuario.setBloqueado(true);
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin").password("123456").build();
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);

        assertThrows(BusinessException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowAndIncrementAttempts_WhenWrongPassword() {
        usuario.setIntentosFallidos(3);
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin").password("wrong").build();
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(request));
        verify(usuarioMapper).incrementFailedAttempts(1L);
    }

    @Test
    void login_ShouldBlockUser_WhenMaxAttemptsReached() {
        usuario.setIntentosFallidos(4);
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin").password("wrong").build();
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(request));
        verify(usuarioMapper).incrementFailedAttempts(1L);
        verify(usuarioMapper).blockUser(1L);
    }

    @Test
    void refresh_ShouldReturnNewTokens() {
        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                .refreshToken("valid-refresh-token").build();
        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid-refresh-token")).thenReturn("admin");
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);
        when(jwtTokenProvider.generateAccessToken("admin")).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken("admin")).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(3600000L);
        when(usuarioMapper.findRolesByUsuarioId(1L)).thenReturn(List.of("ADMIN"));
        when(usuarioMapper.findPermissionsByUsuarioId(1L)).thenReturn(List.of("READ"));

        LoginResponseDTO response = authService.refresh(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(usuarioMapper).updateTokenRefresh(eq(1L), eq("new-refresh-token"));
    }

    @Test
    void refresh_ShouldThrow_WhenTokenIsInvalid() {
        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                .refreshToken("invalid-token").build();
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.refresh(request));
    }

    @Test
    void logout_ShouldClearRefreshToken() {
        when(usuarioMapper.findByUsername("admin")).thenReturn(usuario);
        authService.logout("admin");
        verify(usuarioMapper).updateTokenRefresh(1L, null);
    }

    @Test
    void logout_ShouldHandleNullUser() {
        when(usuarioMapper.findByUsername("unknown")).thenReturn(null);
        assertDoesNotThrow(() -> authService.logout("unknown"));
    }
}
