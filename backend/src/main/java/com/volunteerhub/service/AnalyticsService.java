package com.volunteerhub.service;

import com.volunteerhub.dto.ApiDtos.DepartmentCoverage;
import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    private final VolunteerTaskRepository tasks;
    private final ShiftRepository shifts;
    private final TaskApplicationRepository applications;

    public AnalyticsService(VolunteerTaskRepository tasks, ShiftRepository shifts, TaskApplicationRepository applications) {
        this.tasks = tasks;
        this.shifts = shifts;
        this.applications = applications;
    }

    public List<DepartmentCoverage> coverage() {
        Map<String, int[]> totals = new LinkedHashMap<>();
        for (VolunteerTask task : tasks.findAll()) {
            int[] values = totals.computeIfAbsent(task.getDepartment(), ignored -> new int[3]);
            for (Shift shift : shifts.findByTaskId(task.getId())) {
                values[1] += shift.getCapacity();
                applications.findByShiftId(shift.getId()).forEach(application -> {
                    if (application.getStatus() == ApplicationStatus.APPROVED) {
                        values[0]++;
                    }
                    if (application.getStatus() == ApplicationStatus.PENDING) {
                        values[2]++;
                    }
                });
            }
        }
        return totals.entrySet().stream()
                .map(entry -> new DepartmentCoverage(entry.getKey(), entry.getValue()[0], entry.getValue()[1], entry.getValue()[2]))
                .toList();
    }
}
