package hu.webuni.transport.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.webuni.transport.dto.MilestoneDto;
import hu.webuni.transport.model.Milestone;

@Mapper(componentModel = "spring")
public interface MilestoneMapper {

	@Mapping(source = "address.id", target = "addressId")
	MilestoneDto milestoneToDto(Milestone milestone);
	
	//Service-re bízom a valódi address objektum betöltését az addressId alapján:
	@Mapping(source = "addressId", target = "address.id")
	Milestone dtoTomilestone(MilestoneDto milestoneDto);
	
	List<MilestoneDto> milestonesToDtos(List<Milestone> milestones);
	
	
	
}
