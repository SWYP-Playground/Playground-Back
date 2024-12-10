package com.swyp.playground.domain.report.controller;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.playground.domain.report.domain.Report;
import com.swyp.playground.domain.report.dto.AddReportDto;
import com.swyp.playground.domain.report.service.ReportService;
import com.swyp.playground.domain.report.dto.AddReportDto;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/all")
    public ResponseEntity<List<Report>> getAllReport() {
        return new ResponseEntity<List<Report>>(reportService.getAllReports(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Optional<Report>> getReport(@RequestParam Long id) {
        return new ResponseEntity<Optional<Report>>(reportService.getReport(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Report> addReport (@RequestBody AddReportDto addReportDto) {
        Report targetReport = new Report();

        targetReport.setTargetNickname(addReportDto.getTargetNickname());
        targetReport.setCause(addReportDto.getCause());
        targetReport.setFindFriendId(addReportDto.getFindFriendId());
        targetReport.setWrittenBy(addReportDto.getWrittenBy());
        targetReport.setSentAt(getCurrentDateTime());

        targetReport = reportService.addReport(targetReport);
        
        return new ResponseEntity<>(targetReport, HttpStatus.CREATED);
    }
    
    public Date getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return calendar.getTime();
    }
    
}
