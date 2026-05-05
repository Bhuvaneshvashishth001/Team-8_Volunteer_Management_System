package com.volunteerhub.controller;

import com.volunteerhub.dto.ApiDtos.CheckInRequest;
import com.volunteerhub.model.Attendance;
import com.volunteerhub.service.AttendanceService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/attendance/{applicationId}/check-in")
    public Attendance checkIn(@PathVariable String applicationId, @RequestBody CheckInRequest request) {
        return attendanceService.checkIn(applicationId, request.latitude(), request.longitude(), request.code());
    }

    @PostMapping("/attendance/{applicationId}/check-out")
    public Attendance checkOut(@PathVariable String applicationId) {
        return attendanceService.checkOut(applicationId);
    }

    @PostMapping("/admin/attendance/{attendanceId}/verify")
    public Attendance verify(@PathVariable String attendanceId) {
        return attendanceService.verify(attendanceId);
    }
}
