package bo.capital.tec.pet.modules.persona.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonaResponseDTO {
    private Long id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String ci;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String genero;
    private Long direccionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
