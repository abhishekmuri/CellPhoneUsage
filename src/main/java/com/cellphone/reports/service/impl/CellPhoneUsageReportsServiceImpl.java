package com.cellphone.reports.service.impl;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cellphone.reports.dto.CellPhone;
import com.cellphone.reports.dto.CellPhoneUageByMonth;
import com.cellphone.reports.service.CellPhoneUsageReportsService;
import com.cellphone.reports.util.CommonUtil;
import com.cellphone.reports.util.PrintOutput;

@Service
public class CellPhoneUsageReportsServiceImpl implements CellPhoneUsageReportsService{
	private static final Logger logger = LogManager.getLogger(CellPhoneUsageReportsServiceImpl.class);
	@Override
	public void printReport(Integer year) throws Exception {
		List<CellPhone> cellPhones = getCellPhoneData();
		List<CellPhoneUageByMonth> usageData = getUsageDetails(year);
		
		Map<Integer, List<CellPhoneUageByMonth>> groupByEmployeeId = usageData.stream()
				.collect(Collectors.groupingBy(CellPhoneUageByMonth::getEmployeeId));
		
		String reportData =  generateReport(cellPhones, usageData, groupByEmployeeId);
		printToPrinter(reportData);
		
	}
	
	private List<CellPhoneUageByMonth> getUsageDetails(Integer year) throws Exception{
		InputStream is = new FileInputStream(new File("CellPhoneUsageByMonth.csv"));
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		List<CellPhoneUageByMonth> usageData = br.lines()
			    .skip(1)
			    .map(mapToUsageData)
			    .filter(y -> y.getYear().equals(year))
			    .sorted(Comparator.comparing(CellPhoneUageByMonth::getMonth))
			    .collect(Collectors.toList());
		br.close();
		return usageData;
	}
	
	private List<CellPhone> getCellPhoneData() throws Exception{
		InputStream is = new FileInputStream(new File("CellPhone.csv"));
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		 
		List<CellPhone> cellPhones = br.lines()
		    .skip(1)
		    .map(mapToCellPhone)
		    .collect(Collectors.toList());
		
		br.close();
		return cellPhones;
	}

	private void printToPrinter(String reportData) {
	    PrinterJob job = PrinterJob.getPrinterJob();
	    job.setPrintable(new PrintOutput(reportData));
	    boolean doPrint = job.printDialog();
	    if (doPrint){ 
	        try {
	            job.print();
	        }catch (PrinterException e) {
	            logger.error("Error while printing ",e);
	        }
	    }
	}
	private String generateReport(List<CellPhone> cellPhones,List<CellPhoneUageByMonth> usageData,  Map<Integer, List<CellPhoneUageByMonth>> groupByEmployeeId) {
		String message = String.join(",","Report Run Date","Number of Phones","Total Minutes","Total Data","Average Minutes","Average Data");
		int totalMinutes = usageData.stream().mapToInt(o -> o.getTotalMinutes()).sum();
		Double totalData = usageData.stream().mapToDouble(o -> o.getTotalMinutes()).sum();
		message += "\n";
		message += String.join(",", CommonUtil.dateToString("dd/MM/yyyy", new Date()), String.valueOf(cellPhones.size()), String.valueOf(totalMinutes), String.valueOf(totalData),
				String.valueOf(totalMinutes/cellPhones.size()), String.valueOf(totalData/cellPhones.size()));
		message += "\n";
		//details
		for(CellPhone phone : cellPhones) {
			message += String.join(",", "Employee Id","Employee Name","Model","Purchase Date","Minutes Usage","","","","","","","","","","","","","Data Usage");
			message += "\n";
			message += String.join(",",String.valueOf(phone.getEmployeeId()),phone.getEmployeeName(),phone.getModel(),CommonUtil.dateToString("yyyyMMdd",phone.getPurchaseDate()));
			List<CellPhoneUageByMonth> usage = groupByEmployeeId.get(phone.getEmployeeId());
			if(usage !=null && usage.size()>0) {
				int m = 1;
				int mUsage = 0;
				double dUsage = 0;
				StringBuilder data = new StringBuilder();
				for(CellPhoneUageByMonth u : usage) {
					if(m==u.getMonth()) {
						mUsage += u.getTotalMinutes();
						dUsage += u.getTotalData();
					}else {
						message += ","+mUsage;
						data.append(dUsage).append(",");
						m++;
					}
				}
				message += ","+mUsage;
				data.append(dUsage).append(",");
				message += ","+data.toString()+"\n";
			}
		}
		return message;
	}
	

	public static Function<String, CellPhoneUageByMonth> mapToUsageData = (line) -> {
	  String[] p = line.split(",");
	  CellPhoneUageByMonth usageDetails = new CellPhoneUageByMonth();
	  usageDetails.setEmployeeId(Integer.parseInt(p[0]));
	  String s[]= p[1].split("/");
	  usageDetails.setMonth(Integer.valueOf(s[0]));
	  usageDetails.setYear(Integer.valueOf(s[2]));
	  usageDetails.setTotalMinutes(Integer.valueOf(p[2]));
	  usageDetails.setTotalData(Double.valueOf(p[3]));
	  return usageDetails;
	};
	
	public static Function<String, CellPhone> mapToCellPhone = (line) -> {
		  String[] p = line.split(",");
		  CellPhone phone = new CellPhone();
		  phone.setEmployeeId(Integer.parseInt(p[0]));
		  phone.setEmployeeName(p[1]);
		  phone.setPurchaseDate(CommonUtil.stringToDate("yyyyMMdd",p[2]));
		  phone.setModel(p[3]);
		  return phone;
	};
		
	
}
