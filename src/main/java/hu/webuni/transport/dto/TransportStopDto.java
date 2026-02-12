package hu.webuni.transport.dto;

import java.time.LocalDateTime;

public class TransportStopDto {

	private Long id;
	
	private int stopOrder;
	
	private LocalDateTime plannedArrival;
	private LocalDateTime plannedDeparture;
	
	//azért használok csak id-t, mert nem akarok körkörös JSON találatot (végtelen ciklust) 
	//és egyszerűbb küldeni a kliensnek, így nem kell a TransportStopMapper-be ignorálás (@Mapping(target = "transportPlan.stops", ignore = true))
	private Long transportPlanId;
	
	//nincs szükség teljes Address objektum küldésére POST vagy PUT-nál, és a TransportStop-nál nem kell teljes cím adatokat visszaküldeni 
	//és nem kell Address-ből a TransportStop listát (stops) visszahívni, vagyis nincs szükségem arra, hogy a cím melyik stop-okban szerepel, és azon keresztül hogy melyik tervben szerepel nem kell 
	//full paraméternél a teljes address kiiratást az AddressDto-val oldom meg
	private Long addressId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getStopOrder() {
		return stopOrder;
	}

	public void setStopOrder(int stopOrder) {
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

	public Long getTransportPlanId() {
		return transportPlanId;
	}

	public void setTransportPlanId(Long transportPlanId) {
		this.transportPlanId = transportPlanId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	
}
