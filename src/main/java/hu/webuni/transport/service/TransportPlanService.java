package hu.webuni.transport.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.model.TransportStop;
import hu.webuni.transport.repository.TransportPlanRepository;

@Service
public class TransportPlanService {

	private final TransportPlanRepository transportPlanRepository;

	public TransportPlanService(TransportPlanRepository transportPlanRepository) {
		this.transportPlanRepository = transportPlanRepository;
	}
	
	@Transactional
	public TransportPlan save(TransportPlan plan) {
		validateUniqueStopOrder(plan);
		
		//visszafelé kapcsolat beállítása:
		if (plan.getStops() != null) {
			for (TransportStop stop : plan.getStops()) {
				stop.setTransportPlan(plan);
			}
		}
		return transportPlanRepository.save(plan);
	}

	private void validateUniqueStopOrder(TransportPlan plan) {
		
		if (plan.getStops() == null) {
			return;
		}
		
		Set<Integer> used = new HashSet<>();
		for (TransportStop stop : plan.getStops()) {
			int stopOrder = stop.getStopOrder();
			if (!used.add(stopOrder)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate stopOrder in transport plan: " + stopOrder);
			}
		}
	}
}
