package ecotrack.backend.repositories;

import ecotrack.backend.models.entitys.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {

    Optional<DailyActivity> findByUserIdAndFecha(Long userId, String fecha);
    
    // Nuevo: Obtener todas las actividades de un usuario ordenadas por fecha
    List<DailyActivity> findByUserIdOrderByFechaDesc(Long userId);
    
    // Nuevo: Obtener actividades por rango de fechas
    @Query("SELECT d FROM DailyActivity d WHERE d.user.id = :userId AND d.fecha BETWEEN :startDate AND :endDate ORDER BY d.fecha DESC")
    List<DailyActivity> findByUserIdAndFechaBetween(
        @Param("userId") Long userId, 
        @Param("startDate") String startDate, 
        @Param("endDate") String endDate
    );
    
    // Nuevo: Contar actividades de un usuario
    Long countByUserId(Long userId);
}