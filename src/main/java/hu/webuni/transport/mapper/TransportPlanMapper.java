package hu.webuni.transport.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import hu.webuni.transport.dto.TransportPlanDto;
import hu.webuni.transport.dto.TransportPlanSummaryDto;
import hu.webuni.transport.model.TransportPlan;

//Mivel a TransportPlan -> TransportPlanDto átalakításnál a 
//TransportPlan-hez tartozó List<TransportStop> stops-ot (Entity-t) is 
//List<TransportStopDto> stops-ra (Dto-ra) kell átalakítani, 
//ezért stops lista map-peléséhez a uses = TransportStopMapper.class-re van szükség!
@Mapper(componentModel = "spring", uses = TransportStopMapper.class)
public interface TransportPlanMapper {

	TransportPlanDto planToDto(TransportPlan transportPlan);
	
	List<TransportPlanDto> plansToDtos(List<TransportPlan> TransportPlans);
	
	//Amíg nem írom meg a Security-t, addig ignorálom az ownerUserId mező küldését:
	@Mapping(target ="ownerUserId", ignore = true)
	TransportPlan dtoToPlan(TransportPlanDto transportPlanDto);
	
	List<TransportPlan> dtosToPlans(List<TransportPlanDto> transportPlanDtos);
	
	@Named("summary")
	TransportPlanSummaryDto planToSummaryDto(TransportPlan transportPlan);
	
	List<TransportPlanSummaryDto> plansToSummaryDtos(List<TransportPlan> transportPlans);
	
	
	
}
