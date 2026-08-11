package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.dto.RegistroVacunacionSummaryDTO;

import java.time.LocalDate;

public interface RegistroVacunacionService {
    RegistroVacunacionResponseDTO create(RegistroVacunacionRequestDTO dto);
    RegistroVacunacionResponseDTO getById(Long id);
    PagedResponse<RegistroVacunacionSummaryDTO> getAll(Long mascotaId, Long vacunaId,
            LocalDate fechaDesde, LocalDate fechaHasta, int page, int size);
    RegistroVacunacionResponseDTO update(Long id, RegistroVacunacionRequestDTO dto);
    void delete(Long id);
}
