package hu.webuni.transport.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.model.TransportStop;
import hu.webuni.transport.repository.AddressRepository;
import hu.webuni.transport.repository.TransportPlanRepository;
import jakarta.persistence.CascadeType;

@Service
public class TransportPlanService {

	private final TransportPlanRepository transportPlanRepository;
	private final AddressRepository addressRepository; 

	public TransportPlanService(TransportPlanRepository transportPlanRepository, AddressRepository addressRepository) {
		this.transportPlanRepository = transportPlanRepository;
		this.addressRepository = addressRepository;
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
}
