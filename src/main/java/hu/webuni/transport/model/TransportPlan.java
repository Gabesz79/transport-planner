package hu.webuni.transport.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
public class TransportPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime plannedStart;
	private LocalDateTime plannedEnd;
	
	private Integer expectedRevenue;
	
	private Long ownerUserId;
	
	@OneToMany(mappedBy = "transportPlan", cascade = CascadeType.ALL) //One TransportPlan -> Many TransportStops
	@OrderBy("stopOrder ASC")
	private List<TransportStop> stops = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getPlannedStart() {
		return plannedStart;
	}

	public void setPlannedStart(LocalDateTime plannedStart) {
		this.plannedStart = plannedStart;
	}

	public LocalDateTime getPlannedEnd() {
		return plannedEnd;
	}

	public void setPlannedEnd(LocalDateTime plannedEnd) {
		this.plannedEnd = plannedEnd;
	}

	public Integer getExpectedRevenue() {
		return expectedRevenue;
	}

	public void setExpectedRevenue(Integer expectedRevenue) {
		this.expectedRevenue = expectedRevenue;
	}

	public Long getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(Long ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public List<TransportStop> getStops() {
		return stops;
	}

	public void setStops(List<TransportStop> stops) {
		this.stops = stops;
	}
	
	
	
}
