package bo.capital.tec.pet.common.messaging.outbox;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OutboxMapper {
    void insert(Outbox outbox);
    Outbox selectById(@Param("id") Long id);
    List<Outbox> selectPending(@Param("limit") int limit,
                               @Param("now") LocalDateTime now);
    List<Outbox> selectStale(@Param("limit") int limit,
                             @Param("threshold") LocalDateTime threshold,
                             @Param("maxRetries") int maxRetries);
    void markProcessing(@Param("id") Long id);
    void markProcessed(@Param("id") Long id, @Param("processedAt") LocalDateTime processedAt);
    void markFailed(@Param("id") Long id,
                    @Param("lastError") String lastError,
                    @Param("nextRetryAt") LocalDateTime nextRetryAt);
    void incrementRetry(@Param("id") Long id,
                        @Param("lastError") String lastError,
                        @Param("nextRetryAt") LocalDateTime nextRetryAt);
    long countPending();
    long countByStatus(@Param("status") String status);
    long deleteProcessedOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
