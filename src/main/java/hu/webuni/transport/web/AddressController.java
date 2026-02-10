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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hu.webuni.transport.dto.AddressDto;
import hu.webuni.transport.mapper.AddressMapper;
import hu.webuni.transport.model.Address;
import hu.webuni.transport.service.AddressService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	private final AddressService addressService;
	private final AddressMapper addressMapper;
	
	public AddressController(AddressService addressService, AddressMapper addressMapper) {
		this.addressService = addressService;
		this.addressMapper = addressMapper;
	}
	
	@GetMapping
	public List<AddressDto> getAll() {
		return addressService.findAll().stream()
				.map(addressMapper::addressToDto)
				.toList();
	}
	
	@GetMapping("/{id}")
	public AddressDto getById(@PathVariable Long id) {
		return addressMapper.addressToDto(addressService.findById(id));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) //új példány létrejötte
	public AddressDto create(@RequestBody @Valid AddressDto dto) {
		Address saved = addressService.save(addressMapper.dtoToAddress(dto));
		return addressMapper.addressToDto(saved);
	}
	
	@PutMapping("/{id}")
	public AddressDto update(@PathVariable Long id, @RequestBody @Valid AddressDto dto) {
		addressService.findById(id); //404 Not Found ellenőrzése
		
		Address address = addressMapper.dtoToAddress(dto);
		address.setId(id);
		Address saved = addressService.save(address);
		return addressMapper.addressToDto(saved);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) //Sikeres törlésnél: 204 - nincs body válasz
	public void delete(@PathVariable Long id) {
		addressService.findById(id); //404 Not Found ellenőrzése
		
		addressService.deleteById(id);
	}
	
	
	
	
	
	
	
}
