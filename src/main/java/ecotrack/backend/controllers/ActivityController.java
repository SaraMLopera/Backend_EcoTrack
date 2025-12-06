package ecotrack.backend.controllers;

import ecotrack.backend.dto.ActivityResponse;
import ecotrack.backend.dto.RegisterActivityRequest;
import ecotrack.backend.services.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin("*")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> register(@RequestBody RegisterActivityRequest req) {
        return ResponseEntity.ok(activityService.registerActivity(req));
    }
}