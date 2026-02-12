package hu.webuni.transport.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.webuni.transport.dto.TransportStopDto;
import hu.webuni.transport.model.TransportStop;

@Mapper(componentModel = "spring")
public interface TransportStopMapper {

	@Mapping(source = "transportPlan.id", target = "transportPlanId")
	@Mapping(source = "address.id", target = "addressId")
	TransportStopDto stopToDto(TransportStop transportStop);
	
	//csak alapmezők map-pelése, az address-t a Service állítja be az addressId alapján 
	//(ha létezik az addressId), ha az addressId nem létezik, akkor 404 hiba dobás
	//a TransportPlan-t csak a plan mentésekor állítjuk be, azzal foglalkozni nem kell itt:
	@Mapping(source = "addressId", target = "address.id")
	@Mapping(target = "transportPlan", ignore = true)
	TransportStop dtoToStop(TransportStopDto transportStopdto);
	
	
}
