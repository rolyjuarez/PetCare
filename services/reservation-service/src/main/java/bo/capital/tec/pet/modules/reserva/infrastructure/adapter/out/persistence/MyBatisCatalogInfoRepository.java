package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.modules.reserva.domain.port.out.CatalogInfoRepository;
import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Implementación MyBatis del puerto de datos de catálogo.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisCatalogInfoRepository implements CatalogInfoRepository {

    private final ReservaCatalogMapper reservaCatalogMapper;
    private final VacunaCatalogMapper vacunaCatalogMapper;

    @Override
    public ClienteInfoDTO findCliente(Long clienteId) {
        return reservaCatalogMapper.selectCliente(clienteId);
    }

    @Override
    public MascotaInfoDTO findMascota(Long mascotaId) {
        return reservaCatalogMapper.selectMascota(mascotaId);
    }

    @Override
    public RegistroVacunacionInfoDTO findRegistroVacunacion(Long registroVacunacionId) {
        return vacunaCatalogMapper.selectRegistroVacunacion(registroVacunacionId);
    }
}
