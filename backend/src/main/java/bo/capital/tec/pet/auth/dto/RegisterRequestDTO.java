package bo.capital.tec.pet.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Username es requerido")
    @Size(min = 4, max = 50, message = "Username debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, max = 100, message = "Password debe tener entre 6 y 100 caracteres")
    private String password;

    @NotBlank(message = "Nombre es requerido")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "Primer apellido es requerido")
    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    @NotBlank(message = "CI es requerido")
    @Size(max = 20)
    private String ci;

    @NotBlank(message = "Telefono es requerido")
    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser valido")
    private String email;

    @NotNull(message = "Fecha de nacimiento es requerida")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Genero es requerido")
    @Pattern(regexp = "^[MFO]$", message = "Genero debe ser M, F u O")
    private String genero;

    @NotBlank(message = "Calle es requerida")
    @Size(max = 200)
    private String calle;

    @Size(max = 20)
    private String numero;

    @Size(max = 300)
    private String referencia;

    @NotNull(message = "Ciudad es requerida")
    private Long ciudadId;

    private BigDecimal latitud;
    private BigDecimal longitud;
}
