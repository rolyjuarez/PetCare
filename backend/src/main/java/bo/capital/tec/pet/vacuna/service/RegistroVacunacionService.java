package bo.capital.tec.pet.vacuna.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionSummaryDTO;

import java.time.LocalDate;

public interface RegistroVacunacionService {
    RegistroVacunacionResponseDTO create(RegistroVacunacionRequestDTO dto);
    RegistroVacunacionResponseDTO getById(Long id);
    PagedResponse<RegistroVacunacionSummaryDTO> getAll(Long mascotaId, Long vacunaId,
            LocalDate fechaDesde, LocalDate fechaHasta, int page, int size);
    RegistroVacunacionResponseDTO update(Long id, RegistroVacunacionRequestDTO dto);
    void delete(Long id);
}
