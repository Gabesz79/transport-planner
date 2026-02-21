package hu.webuni.transport.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hu.webuni.transport.dto.AddressDto;
import hu.webuni.transport.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

	AddressDto addressToDto(Address address);
	
	Address dtoToAddress(AddressDto addressDto);
	
	List<AddressDto> addressesToDtos(List<Address> addresses);
	
	
}
