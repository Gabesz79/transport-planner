package hu.webuni.transport.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.dto.MilestoneDto;
import hu.webuni.transport.mapper.MilestoneMapper;
import hu.webuni.transport.model.Address;
import hu.webuni.transport.model.Milestone;
import hu.webuni.transport.repository.AddressRepository;
import hu.webuni.transport.repository.MilestoneRepository;


@Service
@Transactional
public class MilestoneService {
	
	private final MilestoneRepository milestoneRepository;
	
	private final AddressRepository addressRepository;
	
	private final MilestoneMapper milestoneMapper ;

	public MilestoneService(MilestoneRepository milestoneRepository, AddressRepository addressRepository,
			MilestoneMapper milestoneMapper) {
		super();
		this.milestoneRepository = milestoneRepository;
		this.addressRepository = addressRepository;
		this.milestoneMapper = milestoneMapper;
	}
	
	public MilestoneDto create(MilestoneDto milestoneDto) {
		
		//Hibaellenőrzések:
		//Üres body:
		if (milestoneDto == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty request body");
		}
		
		if (milestoneDto.getAddressId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AddressId is required");
		}
		
		if (milestoneDto.getPlannedTime() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PlannedTime is required");
		}
		
		//Itt validáljuk az addressId tényleges létezését:
		Address address = addressRepository.findById(milestoneDto.getAddressId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found: " + milestoneDto.getAddressId()));
		
		Milestone milestone = milestoneMapper.dtoTomilestone(milestoneDto);
		milestone.setAddress(address); //address objektum hozzárendelése
		
		Milestone saved = milestoneRepository.save(milestone);
		
		return milestoneMapper.milestoneToDto(saved);
		
	}
	
	@Transactional(readOnly = true)
	public MilestoneDto getById(Long id) {
		Milestone milestone = milestoneRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + id));
		
		return milestoneMapper.milestoneToDto(milestone);		
	}
	
	@Transactional(readOnly = true)
	public List<MilestoneDto> getAll() {
		return milestoneMapper.milestonesToDtos(milestoneRepository.findAll());
	}
	
	
	
	
	
}
