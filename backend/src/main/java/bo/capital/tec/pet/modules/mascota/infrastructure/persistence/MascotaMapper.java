package bo.capital.tec.pet.modules.mascota.infrastructure.persistence;

import org.apache.ibatis.annotations.Mapper;
import bo.capital.tec.pet.modules.mascota.domain.port.MascotaRepository;

@Mapper
public interface MascotaMapper extends MascotaRepository {
}
