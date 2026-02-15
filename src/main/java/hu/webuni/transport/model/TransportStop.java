package hu.webuni.transport.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TransportStop {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//Fuvarterv valahányadik megállója
	//Kell a megfelelő megálló sorrend miatt, mivel másképp nem garantálható a helyes TransportPlan 
	//megálló sorrendjeinek visszaadása -> List<TransportStop> stops -ra rátesszük az 
	//OrderBy("stopOrder ASC") :ez garantálja a megfelelő sorrend visszaadását 
	//- ez validálásra szorul, hogy ne legyen egy tervben két stop ugyanazzal a stopOrder-rel 
	//elírás, rossz feltöltés miatt (transportPlan_id, stopOrder) 
	//-> 1 transportPlan_id-hoz 1 stopOrder tartozhat
	private Integer stopOrder;
	
	private LocalDateTime plannedArrival;
	private LocalDateTime plannedDeparture;
	
	//Many TransportStops -> One TransportPlan 
	@ManyToOne
	private TransportPlan transportPlan;
	
	//Many TransportStops -> One Address: egy megálló (TransportStop) egy címhez (Address) tartozik
	//de ugyanaz a cím több megállóban is szerepelhet (egy transportPlan-hez kapcsolódó megállók között ismétlődhet egy cím pl. indulás és visszaérkezés miatt)
	@ManyToOne
	private Address address;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStopOrder() {
		return stopOrder;
	}

	public void setStopOrder(Integer stopOrder) {
		this.stopOrder = stopOrder;
	}

	public LocalDateTime getPlannedArrival() {
		return plannedArrival;
	}

	public void setPlannedArrival(LocalDateTime plannedArrival) {
		this.plannedArrival = plannedArrival;
	}

	public LocalDateTime getPlannedDeparture() {
		return plannedDeparture;
	}

	public void setPlannedDeparture(LocalDateTime plannedDeparture) {
		this.plannedDeparture = plannedDeparture;
	}

	public TransportPlan getTransportPlan() {
		return transportPlan;
	}

	public void setTransportPlan(TransportPlan transportPlan) {
		this.transportPlan = transportPlan;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}
