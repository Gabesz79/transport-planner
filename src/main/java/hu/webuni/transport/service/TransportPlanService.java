package hu.webuni.transport.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.h2.table.Plan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.model.TransportStop;
import hu.webuni.transport.repository.AddressRepository;
import hu.webuni.transport.repository.TransportPlanRepository;

@Service
public class TransportPlanService {

	@Value("${transport.delay.revenueReductionPercent}")
	private Integer revenueReductionPercent;
	
	private final TransportPlanRepository transportPlanRepository;
	private final AddressRepository addressRepository;

	public TransportPlanService(TransportPlanRepository transportPlanRepository, AddressRepository addressRepository) {
		this.transportPlanRepository = transportPlanRepository;
		this.addressRepository = addressRepository;
	}
	
	@Transactional(readOnly = true)
	public List<TransportPlan> findAll(Boolean full) {
		return full ? transportPlanRepository.findAllWithStops() 
				: transportPlanRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public List<TransportPlan> findAllFullWithAddresses() {
		return transportPlanRepository.findAllWithStopsAndAddresses();
	}
	
	@Transactional(readOnly = true)
	public TransportPlan findById(Long id, Boolean full) {
		if(full) {
			return transportPlanRepository.findByIdWithStops(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TransportPlan not found: " + id));
		}
		return transportPlanRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TransportPlan not found: " + id));
	}
	
	@Transactional
	public TransportPlan save(TransportPlan plan) {
		//StopOrder validálása: sorszáma csak egyszer szerepelhet a TransportPlan-ben:
		validateUniqueStopOrder(plan);
		
		//Ha vannak megállók a plan-ben (stops lista nem üres), akkor:
		if (plan.getStops() != null) {
			for (TransportStop stop : plan.getStops()) {
				
				//Ha nincs address objektum a stop-ban, akkor 400-as hiba, ha van, akkor feltöltjük az addressId-t az objektum id-jával  
				Long addressId = (stop.getAddress() == null) ? null : stop.getAddress().getId();
				if (addressId == null) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stop addressId is required!");
				}
				
				//FONTOS: NullPointerException hiba elkerülése érdekében: 
				//a kód ezen részénél jelen pillanatban a stop objektumban az address-ben csak addressId-t 
				//töltjük fel, és így hozza létre a kizárólag id-ból álló objektumot, ezért ezt
				//ellenőrizni kell, hogy ilyen id-val valóban létezik-e a DB-ben ilyen megadott id-val Address objektum
				//Így ha van a stop-ban address objektum, tehát kapott address id-t, akkor meg kell nézni, hogy létezik-e 
				//az address objektum a DB-ben, ha nem akkor 404-es hiba: 
				if (!addressRepository.existsById(addressId)) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found: " + addressId);
				}
				
				//Ha létezik a megadott id-val Address objektum az adatbázisban, akkor a teljes 
				//objektum mezőit feltöltjük a DB Address-ből:
				stop.setAddress(addressRepository.getReferenceById(addressId));
				
				//Visszafelé kapcsolat beállítása: (hozzákapcsoljuk a megállót a tervhez)
				stop.setTransportPlan(plan);
			}
		}
		//és elmentjük a tervet:
		//Utánanéztem: a plan save esetén csak a tranzakció végén szúrja be 
		//a TransportStop gyerek rekordot, ezért a transportPlan azonnali Dto-zásával
		//a stops listák stop id-ja még null, ezt megoldja TransportPlan stops mezőjénél: 
		//", cascade = CascadeType.ALL" és a saveAndFlush
		return transportPlanRepository.saveAndFlush(plan);
	}
	
	@Transactional
	public TransportPlan update(Long id, TransportPlan plan) {
		TransportPlan existing = transportPlanRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport plan not found: " + id));
		
		plan.setId(id);
		//TransportPlan felhasználó átadása fontos 
		//Security - felhasználó itt már nem változtatható, és nem adható át még véletlenül sem null érték, ezért itt kell lekezelni:
		plan.setOwnerUserId(existing.getOwnerUserId());
		
		return save(plan);
	}
	
	@Transactional
	public void delete(Long id) {
		if (!transportPlanRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport plan not found: " + id);
		}
		transportPlanRepository.deleteById(id);
	}

	private void validateUniqueStopOrder(TransportPlan plan) {
		
		if (plan.getStops() == null) {
			return;
		}
		
		//Ha van legalább egy megálló ellenőrzés:
		//Set, mert egy érték csak egyszer kerülhet bele:
		Set<Integer> used = new HashSet<>();
		//Végigmegyünk az összes megálló stopOrder-én (sorszámán)...
		for (TransportStop stop : plan.getStops()) {
			int stopOrder = stop.getStopOrder();
			//...és ellenőrizzük, hogy a TransportPlan-ben egy stopOrder sorszám csak egyszer szerepel-e. Ha nem csak egyszer szerepel, akkor kivételdobás:
			if (!used.add(stopOrder)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate stopOrder in transport plan: " + stopOrder);
			}
		}
	}
	
	@Transactional
	public TransportPlan delay(Long id, Integer minutes) {
		TransportPlan transportPlan = transportPlanRepository.findByIdWithStops(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport plan not found: " + id));
		
		if (transportPlan.getPlannedStart() != null) {
			transportPlan.setPlannedStart(transportPlan.getPlannedStart().plusMinutes(minutes));
		}
		
		if (transportPlan.getPlannedEnd() != null) {
			transportPlan.setPlannedEnd(transportPlan.getPlannedEnd().plusMinutes(minutes));
		}
		
		if (transportPlan.getStops() != null) {
			for (TransportStop stop : transportPlan.getStops()) {
				if (stop.getPlannedArrival() != null) {
					stop.setPlannedArrival(stop.getPlannedArrival().plusMinutes(minutes));
				}
				
				if (stop.getPlannedDeparture() != null) {
					stop.setPlannedDeparture(stop.getPlannedDeparture().plusMinutes(minutes));
				}
			}
		}
		
		//Késés esetén bevétel csökkentés is legyen:
		if (minutes > 0) {
			Integer newRevenue = transportPlan.getExpectedRevenue() * (100 - revenueReductionPercent) /100;
			transportPlan.setExpectedRevenue(newRevenue);
		}
		
		return transportPlan;
	}
}
