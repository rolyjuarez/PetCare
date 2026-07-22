package bo.capital.tec.pet.auth.service;

import bo.capital.tec.pet.auth.dto.*;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    LoginResponseDTO refresh(RefreshTokenRequestDTO dto);
    void logout(String username);
    void register(RegisterRequestDTO dto);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
