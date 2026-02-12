package hu.webuni.transport.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hu.webuni.transport.dto.TransportPlanDto;
import hu.webuni.transport.mapper.TransportPlanMapper;
import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.service.TransportPlanService;

@RestController
@RequestMapping("/api/transport-plans")
public class TransportPlanController {
	
	private final TransportPlanService TransportPlanService;
	private final TransportPlanMapper transportPlanMapper; 
	
	public TransportPlanController(TransportPlanService transportPlanService,
			TransportPlanMapper transportPlanMapper) {
		this.TransportPlanService = transportPlanService;
		this.transportPlanMapper = transportPlanMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TransportPlanDto create(@RequestBody TransportPlanDto transportPlanDto) {
		TransportPlan saved = TransportPlanService.save(transportPlanMapper.dtoToPlan(transportPlanDto));
		return transportPlanMapper.planToDto(saved);
	}
	

}
