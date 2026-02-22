package hu.webuni.transport.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.webuni.transport.dto.SectionDto;
import hu.webuni.transport.model.Section;

@Mapper(componentModel = "spring")
public interface SectionMapper {

	//Service-re bízom az objektum valódi ellenőrzését:
	@Mapping(source = "transportPlan.id", target = "transportPlanId")
	@Mapping(source = "fromMilestone.id", target = "fromMilestoneId")
	@Mapping(source = "toMilestone.id", target = "toMilestoneId")
	SectionDto sectionToDto(Section section);
	
	//Itt is a Service-re bízom az objektum valódi ellenőrzését:
	@Mapping(source = "transportPlanId", target = "transportPlan.id")
	@Mapping(source = "fromMilestoneId", target = "fromMilestone.id")
	@Mapping(source = "toMilestoneId", target = "toMilestone.id")
	Section dtoToSection(SectionDto sectionDto);
	
	List<SectionDto> sectionsToDtos(List<Section> sections);
	
	
}
