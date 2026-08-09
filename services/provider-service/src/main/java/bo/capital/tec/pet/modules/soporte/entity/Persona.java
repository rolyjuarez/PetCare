package bo.capital.tec.pet.modules.soporte.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Persona {
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
    private Boolean deleted;
    private Integer version;
}
