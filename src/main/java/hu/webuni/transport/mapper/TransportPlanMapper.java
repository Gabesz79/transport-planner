package hu.webuni.transport.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import hu.webuni.transport.dto.TransportPlanDto;
import hu.webuni.transport.dto.TransportPlanSummaryDto;
import hu.webuni.transport.dto.TransportStopDto;
import hu.webuni.transport.model.TransportPlan;
import hu.webuni.transport.model.TransportStop;

//Mivel a TransportPlan -> TransportPlanDto átalakításnál a 
//TransportPlan-hez tartozó List<TransportStop> stops-ot (Entity-t) is 
//List<TransportStopDto> stops-ra (Dto-ra) kell átalakítani, 
//ezért stops lista map-peléséhez a uses = TransportStopMapper.class-re van szükség!
@Mapper(componentModel = "spring", uses = {TransportStopMapper.class, SectionMapper.class})
public interface TransportPlanMapper {

//	//TransportPlan - Full (stops, de address nélkül)
//	@Named("planNoAddress")
//	@Mapping(target = "stops", qualifiedByName = "stopsToDtosNoAddress")
	
	//TransportPlan - Full (sections, de address nélkül)
	@Named("planNoAddress")
	TransportPlanDto planToDto(TransportPlan transportPlan);
	
//	//TransportPlan - Full + includeAddress (stops + address részletezve)
//	@Named("planWithAddress")
//	@Mapping(target = "stops", qualifiedByName = "stopsToDtosWithAddress")
//	TransportPlanDto planToDtoWithAddress(TransportPlan transportPlan);
	
	//TransportPlan - Full + includeAddress (sections + address részletezve)
	@Named("planWithAddress")
	TransportPlanDto planToDtoWithAddress(TransportPlan transportPlan);
	
	//Amíg nem írom meg a Security-t, addig ignorálom az ownerUserId mező küldését:
	@Mapping(target ="ownerUserId", ignore = true)
	TransportPlan dtoToPlan(TransportPlanDto transportPlanDto);
	
	//Summary:
	@Named("summary")
	TransportPlanSummaryDto planToSummaryDto(TransportPlan transportPlan);
	
	//Summary lista:
	List<TransportPlanSummaryDto> plansToSummaryDtos(List<TransportPlan> transportPlans);
	
	//TransportPlan - Full lista - Address nélkül
	@IterableMapping(qualifiedByName = "planNoAddress")
	List<TransportPlanDto> plansToDtos(List<TransportPlan> transportPlans);
	
	//TransportPlan - Full lista + includeAddress
	@IterableMapping(qualifiedByName = "planWithAddress")
	List<TransportPlanDto> plansToDtosWithAddress(List<TransportPlan> transportPlans);
	
	//TransportStops - Address nélkül
	@Named("stopsToDtosNoAddress")
	@IterableMapping(qualifiedByName = "stopNoAddress")
	List<TransportStopDto> stopsToDtosNoAddress(List<TransportStop> transportStops);
	
	//TransportStops + includeAddress
	@Named("stopsToDtosWithAddress")
	@IterableMapping(qualifiedByName = "stopWithAddress")
	List<TransportStopDto> stopsToDtosWithAddress(List<TransportStop> transportStops);
	
}
