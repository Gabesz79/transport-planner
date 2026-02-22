package hu.webuni.transport.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import hu.webuni.transport.dto.AddressDto;
import hu.webuni.transport.dto.DelayRequestDto;
import hu.webuni.transport.dto.TransportPlanDto;
import hu.webuni.transport.dto.TransportStopDto;
import hu.webuni.transport.repository.AddressRepository;
import hu.webuni.transport.repository.TransportPlanRepository;


//VÁLTOZÁS: Ez az osztály a RÉGI, TransportStop-alapú delay logikához készült.
//A tanári spec szerint a domain átépült Section/Milestone-ra, ezért ez a teszt már nem használt.
//HELYETTE EZT HASZNÁLJUK: TransportPlanDelayTeacherIT (milestone/section-alapú tanári delay)

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebTestClient
//@ActiveProfiles("test")
@Disabled("Legacy stop-based delay IT; replaced by milestone/section-based TransportPlanDelayTeacherIT")
public class TransportPlanDelayIT {

	
	@Test
	void legacy_delay_test_replaced() {
		//a régi stop-alapú teszteket nem futtatom többé.
		// Az új tesztet lásd: TransportPlanDelayTeacherIT
	}
//	@Autowired
//	WebTestClient webTestClient;
//	
//	@Autowired
//	TransportPlanRepository transportPlanRepository;
//	
//	@Autowired
//	AddressRepository addressRepository;
//	
//	//DB takarítása az egyes tesztek között:
//	@BeforeEach
//	void cleanup() {
//		transportPlanRepository.deleteAll();
//		addressRepository.deleteAll();
//	}
//	
//	//TransportPlan 1 stop-al valid értékkel:
//	@Test
//	void delay_shouldShiftTimes_andReduceRevenue_oneStop() {
//	
//		//address, stop, plan példányok létrehozása és transportPlan példány tesztelése:
//		AddressDto address = createAddress();
//
//		TransportStopDto stop = buildStop(1, address.getId(), 
//				LocalDateTime.of(2026, 2, 11, 10, 30), 
//				LocalDateTime.of(2026, 2, 11, 11, 0));
//		
//		TransportPlanDto plan = buildPlan(
//				100000,
//				LocalDateTime.of(2026, 2, 11, 10, 0),
//				LocalDateTime.of(2026, 2, 11, 18, 0),
//				List.of(stop));
//		
//		TransportPlanDto createdPlan = createPlan(plan);
//		assertThat(createdPlan.getId()).isNotNull();
//		
//		//delay példány létrehozása és transportPlan példány delay-el futtatása:
//		TransportPlanDto delayedPlan1stop = delay(createdPlan.getId(), buildDelayRequest(30)) 
//				.expectStatus().isOk()
//				.expectBody(TransportPlanDto.class)
//				.returnResult()
//				.getResponseBody();
//		
//		//delayedPlan eredmény (időeltolás) ellenőrzése:
//		assertThat(delayedPlan1stop).isNotNull();
//		assertThat(delayedPlan1stop.getPlannedStart()).isEqualTo(LocalDateTime.of(2026, 2, 11, 10, 30));
//		assertThat(delayedPlan1stop.getPlannedEnd()).isEqualTo(LocalDateTime.of(2026, 2, 11, 18, 30));
//		assertThat(delayedPlan1stop.getExpectedRevenue()).isEqualTo(90000);
//		
//		//delayedPlan-> 1 Stops eredmény (időeltolás) ellenőrzése:
//		assertThat(delayedPlan1stop.getStops()).hasSize(1);
//		
//		TransportStopDto delayedPlanStop1Result = delayedPlan1stop.getStops().get(0);
//		assertThat(delayedPlanStop1Result.getPlannedArrival()).isEqualTo(LocalDateTime.of(2026, 2, 11, 11, 0));
//		assertThat(delayedPlanStop1Result.getPlannedDeparture()).isEqualTo(LocalDateTime.of(2026, 2, 11, 11, 30));	
//	}
//	
//	TransportPlan 2 stop-al valid érték:
//	@Test
//	void delay_shouldShiftTimes_andReduceRevenue_twoStops() {
//	
//		//address, stop1, stop2, plan példányok létrehozása és transportPlan példány tesztelése:
//		AddressDto address = createAddress();
//
//		TransportStopDto stop1 = buildStop(1, address.getId(), 
//				LocalDateTime.of(2026, 2, 11, 10, 30), 
//				LocalDateTime.of(2026, 2, 11, 11, 0));
//		
//		TransportStopDto stop2 = buildStop(2, address.getId(), 
//				LocalDateTime.of(2026, 2, 11, 12, 0), 
//				LocalDateTime.of(2026, 2, 11, 12, 30));
//		
//		TransportPlanDto plan = buildPlan(
//				100000,
//				LocalDateTime.of(2026, 2, 11, 10, 0),
//				LocalDateTime.of(2026, 2, 11, 18, 0),
//				List.of(stop1, stop2));
//		
//		TransportPlanDto createdPlan = createPlan(plan);
//		assertThat(createdPlan.getId()).isNotNull();
//		
//		//delay példány létrehozása és transportPlan példány delay-el futtatása:
//		TransportPlanDto delayedPlan2stops = delay(createdPlan.getId(), buildDelayRequest(30)) 
//				.expectStatus().isOk()
//				.expectBody(TransportPlanDto.class)
//				.returnResult()
//				.getResponseBody();
//		
//		//delayedPlan eredmény (időeltolás) ellenőrzése:
//		assertThat(delayedPlan2stops).isNotNull();
//		assertThat(delayedPlan2stops.getPlannedStart()).isEqualTo(LocalDateTime.of(2026, 2, 11, 10, 30));
//		assertThat(delayedPlan2stops.getPlannedEnd()).isEqualTo(LocalDateTime.of(2026, 2, 11, 18, 30));
//		assertThat(delayedPlan2stops.getExpectedRevenue()).isEqualTo(90000);
//		
//		//delayedPlan-> 2 Stops eredmény (időeltolás) ellenőrzése:
//		assertThat(delayedPlan2stops.getStops()).hasSize(2);
//		
//		TransportStopDto delayedPlanStop1Result = delayedPlan2stops.getStops().get(0);
//		assertThat(delayedPlanStop1Result.getPlannedArrival()).isEqualTo(LocalDateTime.of(2026, 2, 11, 11, 0));
//		assertThat(delayedPlanStop1Result.getPlannedDeparture()).isEqualTo(LocalDateTime.of(2026, 2, 11, 11, 30));
//		
//		TransportStopDto delayedPlanStop2Result = delayedPlan2stops.getStops().get(1);
//		assertThat(delayedPlanStop2Result.getPlannedArrival()).isEqualTo(LocalDateTime.of(2026, 2, 11, 12, 30));
//		assertThat(delayedPlanStop2Result.getPlannedDeparture()).isEqualTo(LocalDateTime.of(2026, 2, 11, 13, 0));
//	
//	}		
//	
//	@Test
//	void delay_shouldReturn404_whenPlanNotFound() {
//		Long nonExistingId = 9999L;
//		
//		delay(nonExistingId, buildDelayRequest(30))
//			.expectStatus().isNotFound()
//			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
//			.expectBody()
//			.jsonPath("$.errorCode").isEqualTo("ERROR")
//			.jsonPath("$.errorMessage").isEqualTo("Transport plan not found: " + nonExistingId);
//	}
//	
//	@Test
//	void delay_shouldReturn400_whenMinutesNonPositive() {
//		
//		AddressDto address = createAddress();
//		
//		TransportStopDto stop = buildStop(
//				1, address.getId(),
//				LocalDateTime.of(2026, 2, 11, 10, 30), 
//				LocalDateTime.of(2026, 2, 11, 11, 0));
//		
//		TransportPlanDto plan = buildPlan(
//				100000,
//				LocalDateTime.of(2026, 2, 11, 10, 0),
//				LocalDateTime.of(2026, 2, 11, 18, 0),
//				List.of(stop));
//				
//		TransportPlanDto createdPlan = createPlan(plan); 
//		DelayRequestDto delayRequestDto = buildDelayRequest(0);
//		
//		delay(createdPlan.getId(), delayRequestDto)
//			.expectStatus().isBadRequest()
//			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
//			.expectBody()
//			.jsonPath("$.errorCode").isEqualTo("ERROR")
//			.jsonPath("$.errorMessage").isEqualTo("Minutes must be positive");
//	}
//	
//	@Test
//	void delay_shouldReturn400_whenMinutesMissingInBody() {
//		AddressDto address = createAddress();
//		
//		TransportStopDto stop = buildStop(
//				1, address.getId(),
//				LocalDateTime.of(2026, 2, 11, 10, 30), 
//				LocalDateTime.of(2026, 2, 11, 11, 0));
//		
//		TransportPlanDto plan = buildPlan(
//				100000,
//				LocalDateTime.of(2026, 2, 11, 10, 0),
//				LocalDateTime.of(2026, 2, 11, 18, 0),
//				List.of(stop));
//				
//		TransportPlanDto createdPlan = createPlan(plan);
//		
//		//üres JSON body -> minutes hiányzik:
//		webTestClient.post()
//			.uri("/api/transport-plans/{id}/delay", createdPlan.getId())
//			.contentType(MediaType.APPLICATION_JSON)
//			.bodyValue("{}")
//			.exchange()
//			.expectStatus().isBadRequest()
//			.expectBody()
//			.jsonPath("$.errorCode").isEqualTo("ERROR")
//			.jsonPath("$.errorMessage").isEqualTo("Minutes is required");
//	}
//	
//	
//	//Helper metódusok:
//	private AddressDto buildAddress() {
//		AddressDto address = new AddressDto();
//		address.setCountry("HU");
//		address.setZip("2132");
//		address.setCity("Veresegyház");
//		address.setStreet("Csengeri utca");
//		address.setHouseNumber("10");
//		return address;
//	}
//	
//	private AddressDto createAddress() {
//		AddressDto createdAddress = webTestClient
//		.post()
//		.uri("/api/addresses")
//		.bodyValue(buildAddress())
//		.exchange()
//		.expectStatus().isCreated()
//		.expectBody(AddressDto.class)
//		.returnResult()
//		.getResponseBody();
//		
//		assertThat(createdAddress).isNotNull();
//		assertThat(createdAddress.getId()).isNotNull();
//		return createdAddress;
//	}
//	
//	private TransportStopDto buildStop(Integer stopOrder, Long addressId, LocalDateTime plannedArrival, LocalDateTime plannedDeparture) {
//		TransportStopDto stop = new TransportStopDto();
//		stop.setStopOrder(stopOrder);
//		stop.setAddressId(addressId);
//		stop.setPlannedArrival(plannedArrival);
//		stop.setPlannedDeparture(plannedDeparture);
//		return stop;
//	}
//	
//	private TransportPlanDto buildPlan(Integer expectedRevenue, LocalDateTime plannedStart, LocalDateTime plannedEnd, List<TransportStopDto> stops) {
//		TransportPlanDto plan = new TransportPlanDto();
//		plan.setExpectedRevenue(expectedRevenue);
//		plan.setPlannedStart(plannedStart);
//		plan.setPlannedEnd(plannedEnd);
//		plan.setStops(stops);
//		return plan;
//	}
//	
//	private TransportPlanDto createPlan(TransportPlanDto plan) {
//		TransportPlanDto createdPlan = webTestClient
//				.post()
//				.uri("/api/transport-plans")
//				.bodyValue(plan)
//				.exchange()
//				.expectStatus().isCreated()
//				.expectBody(TransportPlanDto.class)
//				.returnResult()
//				.getResponseBody();
//		
//		assertThat(createdPlan).isNotNull();
//		assertThat(createdPlan.getId()).isNotNull();
//		return createdPlan;
//	}
//	
//	private DelayRequestDto buildDelayRequest(Integer minutes) {
//		DelayRequestDto delayReq = new DelayRequestDto();
//		delayReq.setMinutes(minutes);
//		return delayReq;
//	}
//	
//	private WebTestClient.ResponseSpec delay(Long planId, DelayRequestDto delayReq) {
//		return webTestClient
//				.post()
//				.uri("/api/transport-plans/{id}/delay", planId)
//				.contentType(MediaType.APPLICATION_JSON)
//				.bodyValue(delayReq)
//				.exchange();
//	}
	
}
