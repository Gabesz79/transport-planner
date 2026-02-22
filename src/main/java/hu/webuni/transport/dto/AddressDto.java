package hu.webuni.transport.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressDto {

	private Long id;
	
	@NotBlank
	private String country;
	
	@NotBlank
	private String zip;
	
	@NotBlank
	private String city;
	
	@NotBlank
	private String street;
	
	@NotBlank
	private String houseNumber;
	
	private Double latitude;
	
	private Double longitude;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public AddressDto(String country, String zip, String city, String street, String houseNumber) {
		super();
		this.country = country;
		this.zip = zip;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
	}
	
	public AddressDto() {
		
	}
	
}
