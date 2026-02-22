package hu.webuni.transport.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hu.webuni.transport.dto.MilestoneDto;
import hu.webuni.transport.service.MilestoneService;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

	private final MilestoneService milestoneService;

	public MilestoneController(MilestoneService milestoneService) {
		this.milestoneService = milestoneService;
	}
	
	@GetMapping
	public List<MilestoneDto> getAll() {
		return milestoneService.getAll();
	}
	
	@GetMapping("/{id}")
	public MilestoneDto getById(@PathVariable Long id) {
		return milestoneService.getById(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MilestoneDto create(@RequestBody MilestoneDto milestoneDto) {
		return milestoneService.create(milestoneDto);
	}
	
	
	
	
	
	
	
	
	
	
}
