package bo.capital.tec.pet.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonaDetailDTO {
    private Long id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private String ci;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
