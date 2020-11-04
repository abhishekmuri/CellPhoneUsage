package com.cellphone.reports.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cellphone.reports.service.CellPhoneUsageReportsService;

@RestController
public class CellPhoneUsageReportsController {
	private static final Logger logger = LogManager.getLogger(CellPhoneUsageReportsController.class);
	
	@Autowired
	private CellPhoneUsageReportsService zipCoderService;
	
	@GetMapping("/printReport/{year}")
	public String printReport(@PathVariable Integer year) {
		try {
			zipCoderService.printReport(year);
		}catch(Exception e) {
			logger.error("Unable to print report",e);
			return "Unable to print report";
		}
		return "Report is printed successfully";
	}
	
	
}
