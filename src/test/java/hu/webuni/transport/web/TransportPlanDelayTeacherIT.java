package hu.webuni.transport.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import hu.webuni.transport.dto.LoginResponseDto;
import hu.webuni.transport.model.Address;
import hu.webuni.transport.model.Milestone;
import hu.webuni.transport.model.Section;
import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.repository.AddressRepository;
import hu.webuni.transport.repository.MilestoneRepository;
import hu.webuni.transport.repository.SectionRepository;
import hu.webuni.transport.repository.TransportPlanRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class TransportPlanDelayTeacherIT {

	@Autowired
	private WebTestClient webTestClient;
	
	@Autowired
	TransportPlanRepository transportPlanRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	MilestoneRepository milestoneRepository;
	
	@Autowired 
	AddressRepository addressRepository;
	
	String transportJwt;
	
	@BeforeEach
	void initJwt() {
		transportJwt = login("transportmgr", "pass123");
 	}
	
	@BeforeEach
	void cleanDb() {
		//Teljes DB takarítás:
		sectionRepository.deleteAll();
		transportPlanRepository.deleteAll();
		milestoneRepository.deleteAll();
		addressRepository.deleteAll();
	}
	
	//Teszt cél: - Delay kérés: planId URL-ben, milestoneId + minutes body-ban
	// Időeltolás milestone alapján:
	// - a késéses milestone mindig tolódik
	// - ha a milestone egy section START-ja -> a section END milestone-ja is tolódik
	// - ha a milestone egy section END-je -> a következő section START-ja tolódik
	// - revenue csökkentés: 30/60/120 perc küszöb szerint properties-ből (feltételezzük: 30 -> 10%, 60 -> 20%, 120 -> 30%)
	//@Test
	@ParameterizedTest
	@CsvSource({
		"1000, 900",
		"1, 1"
	})
	void delay_shouldShiftTimes_andReduceRevenue_milestoneBasedRules(int startRevenue, int expectedRevenue) {
		Address address1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address address2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		Address address3 = saveAddress("Hungary", "Budapest", "1042", "Árpád út", "56");
		
		LocalDateTime time1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime time2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		LocalDateTime time3 = LocalDateTime.of(2026, 2, 22, 12, 0);
		
		Milestone milestoneA = saveMilestone(address1, time1);
		Milestone milestoneB = saveMilestone(address2, time2);
		Milestone milestoneC = saveMilestone(address3, time3);
		
		//TransportPlan + expectedRevenue:
		TransportPlan plan = new TransportPlan();
		
		//1. eset:
		//plan.setExpectedRevenue(1000); //30 perc késés esetén (10%) -> eredmény: 900
		
//		//2. eset:
//		plan.setExpectedRevenue(1); //30 perc késés esetén (10%) -> eredmény: 1
		
		//Mindkét teszteset futtatása:
		plan.setExpectedRevenue(startRevenue);
		
		plan = transportPlanRepository.save(plan);
		
		//Sections létrehozása:
		Section section0 = new Section();
		section0.setSectionOrder(0);
		section0.setFromMilestone(milestoneA);
		section0.setToMilestone(milestoneB);
		section0.setTransportPlan(plan);
		
		Section section1 = new Section();
		section1.setSectionOrder(1);
		section1.setFromMilestone(milestoneB);
		section1.setToMilestone(milestoneC);
		section1.setTransportPlan(plan);
		
		sectionRepository.save(section0);
		sectionRepository.save(section1);
		
		//Delay hívás: milestone=B, minutes=30 (TOKENNEL)
		delay(plan.getId(), milestoneB.getId(), 30)
			.expectStatus().isOk();
		
//TOKEN NÉLKÜL:
//		webTestClient.post()
//			.uri("api/transportplans/{id}/delay", plan.getId())
//			.bodyValue(Map.of(
//					"milestoneId", milestoneB.getId(),
//					"minutes", 30
//					))
//			.exchange()
//			.expectStatus().isOk();
			
		//Ellenőrzések DB-ből:
		Milestone milestoneA2 = milestoneRepository.findById(milestoneA.getId()).orElseThrow();
		Milestone milestoneB2 = milestoneRepository.findById(milestoneB.getId()).orElseThrow();
		Milestone milestoneC2 = milestoneRepository.findById(milestoneC.getId()).orElseThrow();
		TransportPlan plan2 = transportPlanRepository.findById(plan.getId()).orElseThrow();
		
		//Szabály: B mindíg tolódik:
		assertEquals(time2.plusMinutes(30), milestoneB2.getPlannedTime());
		
		//B startja a section1-nek -> C is tolódik:
		assertEquals(time3.plusMinutes(30), milestoneC2.getPlannedTime());
		
		//A nem éritett, nem változik:
		assertEquals(time1, milestoneA2.getPlannedTime());
		
//		//1. eset: Bevétel: 30 perc esetén 10 % csökkenés: 1000 -> 900:
//		assertEquals(900, plan2.getExpectedRevenue());
		
//		//2. eset Bevétel: 30 perc esetén 10 % csökkenés: 1 -> 1:
//		assertEquals(1, plan2.getExpectedRevenue());
		
		//Mindkét teszteset futtatása:
		assertEquals(expectedRevenue, plan2.getExpectedRevenue());
		
	}
	
	@Test
	void delay_shouldReturn404_whenPlanNotFound() {

		delay(99999L, 1L, 30)
			.expectStatus().isNotFound();
		
//TOKEN NÉLKÜL:		
//		webTestClient.post()
//			.uri("/api/transportplans/{id}/delay", 99999L)
//			.bodyValue(Map.of("milestoneId", 1L, "minutes", 30))
//			.exchange()
//			.expectStatus().isNotFound();
	}
	
	@Test
	void delay_shouldReturn404_whenMilestoneNotFound() {
		TransportPlan plan = new TransportPlan();
		
		plan.setExpectedRevenue(1000);
		plan = transportPlanRepository.save(plan);

		delay(plan.getId(), 99999L, 30)
			.expectStatus().isNotFound();
		
//TOKEN NÉLKÜL:	
//		webTestClient.post()
//		.uri("/api/transportplans/{id}/delay", plan.getId())
//		.bodyValue(Map.of("milestoneId", 99999L, "minutes", 30))
//		.exchange()
//		.expectStatus().isNotFound();
	}
	
	@Test
	void delay_shouldReturn400_whenMilestoneNotPartOfPlan() {
		Address address1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address address2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		Address address3 = saveAddress("Hungary", "Budapest", "1042", "Árpád út", "56");
		
		LocalDateTime time1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime time2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		LocalDateTime time3 = LocalDateTime.of(2026, 2, 22, 12, 0);
		
		Milestone milestoneA = saveMilestone(address1, time1);
		Milestone milestoneB = saveMilestone(address2, time2);
		Milestone notInPlan = saveMilestone(address3, time3);
		
		TransportPlan plan = new TransportPlan();
		plan.setExpectedRevenue(1000); //30 perc késés esetén (10%) -> eredmény: 900
		plan = transportPlanRepository.save(plan);
		
		Section section0 = new Section();
		section0.setSectionOrder(0);
		section0.setFromMilestone(milestoneA);
		section0.setToMilestone(milestoneB);
		section0.setTransportPlan(plan);
		
		sectionRepository.save(section0);

		delay(plan.getId(), notInPlan.getId(), 30)
			.expectStatus().isBadRequest();
		
//TOKEN NÉLKÜL:			
//		webTestClient.post()
//			.uri("/api/transportplans/{id}/delay", plan.getId())
//			.bodyValue(Map.of("milestoneId", notInPlan.getId(), "minutes", 30))
//			.exchange()
//			.expectStatus().isBadRequest();
	}
	
	@Test
	void delay_startMilestone_shouldShiftStartAndEnd_only() {
		Address address1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address address2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		Address address3 = saveAddress("Hungary", "Budapest", "1042", "Árpád út", "56");
		
		LocalDateTime time1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime time2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		LocalDateTime time3 = LocalDateTime.of(2026, 2, 22, 12, 0);
		
		Milestone milestoneA = saveMilestone(address1, time1);
		Milestone milestoneB = saveMilestone(address2, time2);
		Milestone milestoneC = saveMilestone(address3, time3);
		
		//TransportPlan + expectedRevenue:
		TransportPlan plan = new TransportPlan();
		plan.setExpectedRevenue(1000); //30 perc késés esetén (10%) -> eredmény: 900
		plan = transportPlanRepository.save(plan);
		
		//Sections létrehozása:
		Section section0 = new Section();
		section0.setSectionOrder(0);
		section0.setFromMilestone(milestoneA);
		section0.setToMilestone(milestoneB);
		section0.setTransportPlan(plan);
		
		Section section1 = new Section();
		section1.setSectionOrder(1);
		section1.setFromMilestone(milestoneB);
		section1.setToMilestone(milestoneC);
		section1.setTransportPlan(plan);
		
		sectionRepository.save(section0);
		sectionRepository.save(section1);
		
		//30 perces késleltetés (A -> A, B-re lépés, C marad, mert nem kell rekurzívan folytatni, vagyis a következő milestone idejét növelni):
		//TOKENNEL:
		delay(plan.getId(), milestoneA.getId(), 30)
			.expectStatus().isOk();
		
//TOKEN NÉLKÜL:	
//		webTestClient.post()
//			.uri("/api/transportplans/{id}/delay", plan.getId())
//			.bodyValue(Map.of("milestoneId", milestoneA.getId(), "minutes", 30))
//			.exchange()
//			.expectStatus().isOk();
		
		//Ellenőrzések DB-ből:
		Milestone milestoneA2 = milestoneRepository.findById(milestoneA.getId()).orElseThrow();
		Milestone milestoneB2 = milestoneRepository.findById(milestoneB.getId()).orElseThrow();
		Milestone milestoneC2 = milestoneRepository.findById(milestoneC.getId()).orElseThrow();
		
		//Szabály: milestone A2 mindíg tolódik 30 perccel:
		assertEquals(time1.plusMinutes(30), milestoneA2.getPlannedTime());
				
		//Szabály: milestone B2 is mindíg tolódik 30 perccel:
		assertEquals(time2.plusMinutes(30), milestoneB2.getPlannedTime());
		
		//Szabály: milestone C2 nem tolódik:
		assertEquals(time3, milestoneC2.getPlannedTime());
	}
	
	@Test
	void delay_endMilestone_shouldShiftEndAndNextSectionStart_nonSharedMilestones() {
		Address address1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address address2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		Address address3 = saveAddress("Hungary", "Budapest", "1042", "Árpád út", "56");
		Address address4 = saveAddress("Hungary", "Budapest", "1044", "Árpád utca", "45");
		
		LocalDateTime time1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime time2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		LocalDateTime time3 = LocalDateTime.of(2026, 2, 22, 12, 0);
		LocalDateTime time4 = LocalDateTime.of(2026, 2, 22, 13, 0);
		
		Milestone milestoneA = saveMilestone(address1, time1);
		Milestone milestoneB = saveMilestone(address2, time2);
		Milestone milestoneC = saveMilestone(address3, time3);
		Milestone milestoneD = saveMilestone(address4, time4);
		
		//TransportPlan + expectedRevenue:
		TransportPlan plan = new TransportPlan();
		plan.setExpectedRevenue(1000); //30 perc késés esetén (10%) -> eredmény: 900
		plan = transportPlanRepository.save(plan);
		
		//Sections létrehozása:
		//section0: A -> B, section1: C -> D, nincs közös milestone a két szakasz között
		Section section0 = new Section();
		section0.setSectionOrder(0);
		section0.setFromMilestone(milestoneA);
		section0.setToMilestone(milestoneB);
		section0.setTransportPlan(plan);
		
		Section section1 = new Section();
		section1.setSectionOrder(1);
		section1.setFromMilestone(milestoneC);
		section1.setToMilestone(milestoneD);
		section1.setTransportPlan(plan);
		
		sectionRepository.save(section0);
		sectionRepository.save(section1);
		
		delay(plan.getId(), milestoneB.getId(), 30)
			.expectStatus().isOk();
		
//TOKEN NÉLKÜL:
//		webTestClient.post()
//		.uri("/api/transportplans/{id}/delay", plan.getId())
//		.bodyValue(Map.of("milestoneId", milestoneB.getId(), "minutes", 30))
//		.exchange()
//		.expectStatus().isOk();
		
		//Ellenőrzések DB-ből:
		Milestone milestoneA2 = milestoneRepository.findById(milestoneA.getId()).orElseThrow();
		Milestone milestoneB2 = milestoneRepository.findById(milestoneB.getId()).orElseThrow();
		Milestone milestoneC2 = milestoneRepository.findById(milestoneC.getId()).orElseThrow();
		Milestone milestoneD2 = milestoneRepository.findById(milestoneD.getId()).orElseThrow();
		
		//milestoneA nem változik:
		assertEquals(time1, milestoneA2.getPlannedTime());
		
		//milestoneB tolódik 30 perccel:
		assertEquals(time2.plusMinutes(30), milestoneB2.getPlannedTime());
		
		//milestoneC is tolódik 30 perccel, mert a következő szakasz start-ja nem ugyanaz a milestone:
		assertEquals(time3.plusMinutes(30), milestoneC2.getPlannedTime());
		
		//milestoneD marad ugyanaz, mert nem kell rekurzívan folytatni, csak a következő milestone idejét növeljük
		assertEquals(time4, milestoneD2.getPlannedTime());
		
	}
	
	@Test
	void delay_shouldReduceRevenue_at60_and120_thresolds() {
		Address address1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address address2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		
		LocalDateTime time1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime time2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		
		Milestone milestoneA = saveMilestone(address1, time1);
		Milestone milestoneB = saveMilestone(address2, time2);
		
		//TransportPlan + expectedRevenue:
		TransportPlan plan60 = new TransportPlan();
		plan60.setExpectedRevenue(1000); //60 perc késés esetén (20%) -> eredmény: 800
		plan60 = transportPlanRepository.save(plan60);
		
		//Sections létrehozása:
		Section section60 = new Section();
		section60.setSectionOrder(0);
		section60.setFromMilestone(milestoneA);
		section60.setToMilestone(milestoneB);
		section60.setTransportPlan(plan60);
		
		sectionRepository.save(section60);

		delay(plan60.getId(), milestoneA.getId(), 60)
			.expectStatus().isOk();
		
//TOKEN NÉLKÜL:		
//		webTestClient.post()
//			.uri("/api/transportplans/{id}/delay", plan60.getId())
//			.bodyValue(Map.of("milestoneId", milestoneA.getId(), "minutes", 60))
//			.exchange()
//			.expectStatus().isOk();
		
		TransportPlan plan60After = transportPlanRepository.findById(plan60.getId()).orElseThrow();
		
		assertEquals(800, plan60After.getExpectedRevenue());
		
		//120 perces késés szimulálása:
		
		//Kell egy tisztítás:
		cleanDb();
		
		Address addressb1 = saveAddress("Hungary", "Budapest", "1138", "Váci út", "91");
		Address addressb2 = saveAddress("Hungary", "Budapest", "1037", "Bécsi út", "12");
		
		LocalDateTime timeb1 = LocalDateTime.of(2026, 2, 22, 10, 0);
		LocalDateTime timeb2 = LocalDateTime.of(2026, 2, 22, 11, 0);
		
		Milestone milestoneAb = saveMilestone(addressb1, timeb1);
		Milestone milestoneBb = saveMilestone(addressb2, timeb2);
		
		//TransportPlan + expectedRevenue:
		TransportPlan plan120 = new TransportPlan();
		plan120.setExpectedRevenue(1000); //120 perc késés esetén (30%) -> eredmény: 700
		plan120 = transportPlanRepository.save(plan120);
		
		//Sections létrehozása:
		Section section120 = new Section();
		section120.setSectionOrder(0);
		section120.setFromMilestone(milestoneAb);
		section120.setToMilestone(milestoneBb);
		section120.setTransportPlan(plan120);
		
		sectionRepository.save(section120);

		delay(plan120.getId(), milestoneAb.getId(), 120)
			.expectStatus().isOk();
		
//TOKEN NÉLKÜL:		
//		webTestClient.post()
//			.uri("/api/transportplans/{id}/delay", plan120.getId())
//			.bodyValue(Map.of("milestoneId", milestoneAb.getId(), "minutes", 120))
//			.exchange()
//			.expectStatus().isOk();
		
		TransportPlan plan120After = transportPlanRepository.findById(plan120.getId()).orElseThrow();
		
		assertEquals(700, plan120After.getExpectedRevenue());
		
	}
	
	
	//Helper metódusok:
	private Address saveAddress(String country, String city, String zip, String street, String houseNumber) {
		Address address = new Address();
		address.setCountry(country);
		address.setCity(city);
		address.setZip(zip);
		address.setStreet(street);
		address.setHouseNumber(houseNumber);
		return addressRepository.save(address);
	}
	
	private Milestone saveMilestone(Address address, LocalDateTime plannedTime ) {
		Milestone milestone = new Milestone();
		milestone.setAddress(address);
		milestone.setPlannedTime(plannedTime);
		return milestoneRepository.save(milestone);
	}
	
	private String login(String username, String password) {
		return webTestClient.post()
				.uri("/api/login")
				.bodyValue(Map.of("username", username, "password", password))
				.exchange()
				.expectStatus().isOk()
				.expectBody(LoginResponseDto.class)
				.returnResult()
				.getResponseBody()
				.getToken();
	}
	
	private ResponseSpec delay(Long planId, Long milestoneId, Integer minutes) {
		return webTestClient.post()
				.uri("/api/transportplans/{id}/delay", planId)
				.headers(headers -> headers.setBearerAuth(transportJwt))
				.bodyValue(Map.of("milestoneId", milestoneId, "minutes", minutes))
				.exchange();
	}
}
