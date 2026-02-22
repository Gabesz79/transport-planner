package hu.webuni.transport.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.dto.AddressDto;
import hu.webuni.transport.dto.AddressFilterDto;
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
	
//	@GetMapping
//	public List<AddressDto> getAll(
//			@RequestParam(required = false) String city,
//			@RequestParam(required = false) String zip,
//			@RequestParam(required = false) String street,
//			@RequestParam(required = false) String country,
//			Pageable pageable) {
//		return addressService.search(city, zip, street, country, pageable).stream()
//				.map(addressMapper::addressToDto)
//				.toList();
//	}
	
	@PostMapping("/search")
	public ResponseEntity<List<AddressDto>> search(@RequestBody AddressFilterDto filter,@RequestParam(required = false) Integer size, Pageable pageable) {
		
		//Request body üres esetén (nincs egyáltalán filter mező), akkor 400-as hiba:
		if (filter == null || (filter.getCountry() == null && filter.getCity() == null && filter.getZip() == null && filter.getStreet() == null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty filter body is not allowed");
		}
		
		//Ha nincs megadva size, akkor minden találat egy oldalon (MAX_VALUE):
		int effectiveSize = (size == null) ? Integer.MAX_VALUE : pageable.getPageSize();

		//Default rendezés (ha nincs sort, akkor id asc):
		Sort effectiveSort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by("id").ascending();
				
		//Felülírom a size-ot és a defaultSort-t:
		Pageable effectivePageable = PageRequest.of(pageable.getPageNumber(), effectiveSize, effectiveSort); 

		//filter body-ban jön be, és pageable:
		Page<Address> page = addressService.search(filter.getCity(), filter.getZip(), filter.getStreet(), filter.getCountry(), effectivePageable);
		
		//Így is meg lehet oldani:
//		List<AddressDto> dtos = page.getContent().stream()
//				.map(addressMapper::addressToDto)
//				.toList();
		
		//De felvettem az addressMapper-ben az addressesToDtos-t:
		List<AddressDto> dtos = addressMapper.addressesToDtos(page.getContent());
		
		
		//Response-ban beteszem a találat számát a Header X-Total-Count-ba: 
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(page.getTotalElements()))
				.body(dtos);
	}
	
	@GetMapping("/{id}")
	public AddressDto getById(@PathVariable Long id) {
		return addressMapper.addressToDto(addressService.findById(id));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) //új példány létrejötte, ha oké, akkor 201-at ad vissza
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
		return addressMapper.addressToDto(saved); //ha oké, akkor 200-at ad vissza
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) //Sikeres törlésnél: 204 - nincs body válasz
	public void delete(@PathVariable Long id) {
		addressService.findById(id); //404 Not Found ellenőrzése
		
		addressService.deleteById(id);
	}
	
	
	
	
	
	
	
}
