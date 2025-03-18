package de.faktorzehn.batch.sample.job.bill.model;

public class Usage {

	private Long id;

	private String firstName;

	private String lastName;

	private Long minutes;

	private Long dataUsage;

	public Usage() {
	}

	public Usage(Long id, String firstName, String lastName, Long minutes, Long dataUsage) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.minutes = minutes;
		this.dataUsage = dataUsage;
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Usage{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", minutes=" + minutes +
				", dataUsage=" + dataUsage +
				'}';
	}
}