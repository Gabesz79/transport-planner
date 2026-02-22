package hu.webuni.transport.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

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
	@Test
	void delay_shouldShiftTimes_andReduceRevenue_milestoneBasedRules() {
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
		
		//Delay hívás: milestone=B, minutes=30
		webTestClient.post()
			.uri("api/transportplans/{id}/delay", plan.getId())
			.bodyValue(Map.of(
					"milestoneId", milestoneB.getId(),
					"minutes", 30
					))
			.exchange()
			.expectStatus().isOk();
			
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
		
		//Bevétel: 30 perc esetén 10 % csökkenés: 1000 -> 900:
		assertEquals(900, plan2.getExpectedRevenue());
		
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
	
	
	
	
	
	
}
