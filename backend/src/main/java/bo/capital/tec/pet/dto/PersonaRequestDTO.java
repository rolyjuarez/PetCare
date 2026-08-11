package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonaRequestDTO {
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
    @Email
    private String email;
    @NotNull
    private LocalDate fechaNacimiento;
    @NotBlank
    @Pattern(regexp = "^[MFO]$", message = "Genero debe ser M, F o O")
    private String genero;
    private Long direccionId;
}
