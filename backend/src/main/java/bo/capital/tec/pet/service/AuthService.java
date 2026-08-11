package bo.capital.tec.pet.service;

import bo.capital.tec.pet.dto.*;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    LoginResponseDTO refresh(RefreshTokenRequestDTO dto);
    void logout(String username);
    void register(RegisterRequestDTO dto);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    ProfileResponseDTO getProfile(String username);
    ProfileResponseDTO updateProfile(String username, UpdateProfileRequestDTO dto);
    void changePassword(String username, ChangePasswordRequestDTO dto);
}
