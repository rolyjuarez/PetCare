package bo.capital.tec.pet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@MapperScan({"bo.capital.tec.pet.modules.*.mapper", "bo.capital.tec.pet.common.messaging.outbox"})
public class PetCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetCareApplication.class, args);
    }
}
