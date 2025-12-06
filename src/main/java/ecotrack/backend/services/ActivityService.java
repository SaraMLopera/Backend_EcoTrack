package ecotrack.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecotrack.backend.carbonApi.services.ClimatiqService;
import ecotrack.backend.dto.ActivityResponse;
import ecotrack.backend.dto.RegisterActivityRequest;
import ecotrack.backend.models.entitys.*;
import ecotrack.backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    private final UserRepository userRepository;
    private final DailyActivityRepository activityRepository;
    private final CarbonRecordRepository recordRepository;
    private final CarbonTotalRepository totalRepository;
    private final ClimatiqService climatiqService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActivityService(UserRepository userRepository,
                           DailyActivityRepository activityRepository,
                           CarbonRecordRepository recordRepository,
                           CarbonTotalRepository totalRepository,
                           ClimatiqService climatiqService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.recordRepository = recordRepository;
        this.totalRepository = totalRepository;
        this.climatiqService = climatiqService;
    }

    @Transactional
    public ActivityResponse registerActivity(RegisterActivityRequest req) {

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String region = req.getRegion() != null ? req.getRegion() : "CO";
        Double emisiones = climatiqService.calculateEmission(
                req.getActivityId(),
                region,
                req.getParameters()
        );

        String parametersJson = "";
        try {
            parametersJson = objectMapper.writeValueAsString(req.getParameters());
        } catch (Exception e) {
            System.err.println("Error serializando parámetros: " + e.getMessage());
        }

        DailyActivity activity = DailyActivity.builder()
                .fecha(req.getFecha())
                .tipo_actividad(req.getTipoActividad())
                .descripcion(req.getDescripcion())
                .activityId(req.getActivityId())
                .parameters(parametersJson)
                .region(region)
                .user(user)
                .build();

        activity = activityRepository.save(activity);

        CarbonRecord record = CarbonRecord.builder()
                .activity(activity)
                .fuente("climatiq")
                .emisiones_calculadas(emisiones)
                .build();

        recordRepository.save(record);

        CarbonTotal total = totalRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CarbonTotal t = new CarbonTotal();
                    t.setUser(user);
                    t.setTotal_historico(0.0);
                    t.setTotal_mensual(0.0);
                    t.setTotal_semanal(0.0);
                    return t;
                });

        total.setTotal_historico(total.getTotal_historico() + emisiones);
        total.setTotal_mensual(total.getTotal_mensual() + emisiones);
        total.setTotal_semanal(total.getTotal_semanal() + emisiones);

        totalRepository.save(total);

        return ActivityResponse.builder()
                .activityId(activity.getId())
                .fecha(activity.getFecha())
                .tipoActividad(activity.getTipo_actividad())
                .descripcion(activity.getDescripcion())
                .emisiones(emisiones)
                .fuente("climatiq")
                .build();
    }
}