package hu.webuni.transport.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hu.webuni.transport.dto.TransportPlanDto;
import hu.webuni.transport.dto.TransportPlanSummaryDto;
import hu.webuni.transport.mapper.TransportPlanMapper;
import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.service.TransportPlanService;

@RestController
@RequestMapping("/api/transport-plans")
public class TransportPlanController {
	
	private final TransportPlanService transportPlanService;
	private final TransportPlanMapper transportPlanMapper; 
	
	public TransportPlanController(TransportPlanService transportPlanService,
			TransportPlanMapper transportPlanMapper) {
		this.transportPlanService = transportPlanService;
		this.transportPlanMapper = transportPlanMapper;
	}

//Dto megoldás:	
//	@GetMapping
//	public List<TransportPlanDto> getAll(@RequestParam(required = false) Boolean full) {
//		
//		Boolean notFull = (full == null || !full);
//		//Ha full = false vagy full == null, akkor findAll metódus paramétere false -> transportPlanRepository.findAll() lefutása TransportPlanService-ben
//		//Ha full = true (vagyis !notFull), akkor  findAll metódus paramétere true -> transportPlanRepository.findAllWithStops() lefutása TransportPlanService-ben
//		return transportPlanService.findAll(!notFull).stream()
//				.map(transportPlanMapper::planToDto)
//				.toList(); //ezzel kiváltom a plansToDtos-t a TransportPlanMapper-ben
//	}
//	
//	@GetMapping("/{id}")
//	public TransportPlanDto getById(@PathVariable Long id, @RequestParam(required = false) Boolean full) {
//		boolean notFull = (full == null || !full);
//		return transportPlanMapper.planToDto(transportPlanService.findById(id, !notFull));
//	}
	
//SummaryDto megoldás:
	//Egyértelműen elválasztottam a Summary-t (full=false) a teljes lekérdezéstől (full=true)
	//nem jelenik meg a stops lista egyáltalán a lekérdezésben:
	@GetMapping
	public List<TransportPlanSummaryDto> getAllSummary() {
		return transportPlanMapper.plansToSummaryDtos(transportPlanService.findAll(false));
	}
	
	@GetMapping(params = "full=true") //?full=true URL esetén fut le ez a metódus:
	public List<TransportPlanDto> getAllFull() {
		return transportPlanMapper.plansToDtos(transportPlanService.findAll(true));
	}
	
	@GetMapping(params = {"full=true", "includeAddress=true"})
	public List<TransportPlanDto> getAllFullWithAddresses() {
		return transportPlanMapper.plansToDtosWithAddress(transportPlanService.findAllFullWithAddresses());
	}
	
	//Egyértelmű elválasztás id lekérdezés esetén is (számomra jobban átlátható így a kód, mint a Mapper-ben való külön beállítás, ezért választottam ezt a megoldást):
	@GetMapping("/{id}")
	public TransportPlanSummaryDto getByIdSummary(@PathVariable Long id) {
		return transportPlanMapper.planToSummaryDto(transportPlanService.findById(id, false));
	}
	
	@GetMapping(value = "/{id}", params = "full=true") //?full=true URL esetén fut le ez a metódus:
	public TransportPlanDto getByIdFull(@PathVariable Long id) {
		return transportPlanMapper.planToDto(transportPlanService.findById(id, true));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TransportPlanDto create(@RequestBody TransportPlanDto transportPlanDto) {
		TransportPlan saved = transportPlanService.save(transportPlanMapper.dtoToPlan(transportPlanDto));
		return transportPlanMapper.planToDto(saved);
	}
	
	@PutMapping("/{id}")
	public TransportPlanDto update(@PathVariable Long id, @RequestBody TransportPlanDto transportPlanDto) {
		TransportPlan updated = transportPlanService.update(id, transportPlanMapper.dtoToPlan(transportPlanDto));
		return transportPlanMapper.planToDto(updated);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		transportPlanService.delete(id);
	}
	
	
}
