package com.cellphone.reports.dto;

public class CellPhoneUageByMonth{
	private Integer employeeId;
	private Integer year;
	private Integer month;
	private Integer totalMinutes;
	private Double totalData;
	
	public CellPhoneUageByMonth() {
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getTotalMinutes() {
		return totalMinutes;
	}

	public void setTotalMinutes(Integer totalMinutes) {
		this.totalMinutes = totalMinutes;
	}

	public Double getTotalData() {
		return totalData;
	}

	public void setTotalData(Double totalData) {
		this.totalData = totalData;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}
	
}
