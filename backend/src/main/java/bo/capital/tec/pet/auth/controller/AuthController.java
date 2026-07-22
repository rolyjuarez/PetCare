package bo.capital.tec.pet.auth.controller;

import bo.capital.tec.pet.auth.dto.*;
import bo.capital.tec.pet.auth.service.AuthService;
import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "API de autenticacion y autorizacion")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Login exitoso"));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo cliente")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Registro exitoso. Revise su correo para mas informacion."));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperacion de contrasena")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null, "Se envio un enlace de recuperacion a su correo."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contrasena")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Contrasena restablecida exitosamente."));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        LoginResponseDTO response = authService.refresh(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Token renovado"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion")
    public ResponseEntity<ApiResponse<Void>> logout() {
        String username = SecurityUtil.getCurrentUsername();
        authService.logout(username);
        return ResponseEntity.ok(ApiResponse.success(null, "Sesion cerrada"));
    }
}
