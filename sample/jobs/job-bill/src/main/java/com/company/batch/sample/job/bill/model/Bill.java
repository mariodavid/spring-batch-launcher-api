package com.company.batch.sample.job.bill.model;

public class Bill {

	private Long id;

	private String firstName;

	private String lastName;

	private Long dataUsage;

	private Long minutes;

	private Double billAmount;

	public Bill(Long id, String firstName, String lastName, Long dataUsage, Long minutes, Double billAmount) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dataUsage = dataUsage;
		this.minutes = minutes;
		this.billAmount = billAmount;
		this.id = id;
	}

	public Bill() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getDataUsage() {
		return dataUsage;
	}

	public void setDataUsage(Long dataUsage) {
		this.dataUsage = dataUsage;
	}

	public Long getMinutes() {
		return minutes;
	}

	public void setMinutes(Long minutes) {
		this.minutes = minutes;
	}

	public Double getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(Double billAmount) {
		this.billAmount = billAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}