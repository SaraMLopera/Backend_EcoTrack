package ecotrack.backend.repositories;

import ecotrack.backend.models.entitys.CarbonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarbonRecordRepository extends JpaRepository<CarbonRecord, Long> {

    // Nuevo: Obtener registros por usuario
    @Query("SELECT cr FROM CarbonRecord cr WHERE cr.activity.user.id = :userId")
    List<CarbonRecord> findByUserId(@Param("userId") Long userId);
}