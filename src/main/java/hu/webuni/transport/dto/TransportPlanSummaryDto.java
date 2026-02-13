package hu.webuni.transport.dto;

import java.time.LocalDateTime;

public class TransportPlanSummaryDto {
	
	private Long id;
	
	private LocalDateTime plannedStart;
	private LocalDateTime plannedEnd;
	
	private Integer expectedRevenue;

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
	
	
	
}
