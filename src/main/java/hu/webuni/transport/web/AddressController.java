package hu.webuni.transport.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
		return addressMapper.addressesToDtos(addressService.findAll());
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
	
	@GetMapping("/{id}")
	public AddressDto getById(@PathVariable Long id) {
		return addressMapper.addressToDto(addressService.findById(id));
	}
	
	@PostMapping
	public ResponseEntity<AddressDto> create(@RequestBody @Valid AddressDto dto) {
		//Ha "id" ki van töltve -> 400-as hiba
		if (dto.getId() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be null on create");
		}
		
		Address saved = addressService.save(addressMapper.dtoToAddress(dto));
		return ResponseEntity.ok(addressMapper.addressToDto(saved)); //200 OK
	}
	
	@PutMapping("/{id}")
	public AddressDto update(@PathVariable Long id, @RequestBody @Valid AddressDto dto) {
		//Ha a body id-ja ki van töltve és eltér a path id-tól -> 400-as hiba
		if (dto.getId() != null && !dto.getId().equals(id)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body id differs from path id");
		}
		
		addressService.findById(id); //404 Not Found ellenőrzése
		
		Address address = addressMapper.dtoToAddress(dto);
		address.setId(id);
		Address saved = addressService.save(address);
		return addressMapper.addressToDto(saved); //ha oké, akkor 200-at ad vissza
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		//A kérés szerint akkor is sikerül a törlés, ha nem volt ilyen id:
		addressService.deleteByIdIfExists(id); //404 Not Found ellenőrzése
		
		//Ha nem megy be a feltételbe, akkor is lefut sikeresen a Controller miatt: return ResponseEntity.ok().build();
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/search")
	public ResponseEntity<List<AddressDto>> search(@RequestBody AddressFilterDto filter, @RequestParam(required = false) Integer size, Pageable pageable) {
		
		//"  " - Üresstring space-ekkel esetet is vizsgálja:
		String country = (StringUtils.hasText(filter.getCountry())) ? filter.getCountry().trim() : null;
		String city = (StringUtils.hasText(filter.getCity())) ? filter.getCity().trim() : null;
		String zip = (StringUtils.hasText(filter.getZip())) ? filter.getZip().trim() : null;
		String street = (StringUtils.hasText(filter.getStreet())) ? filter.getStreet().trim() : null;
		
		//Request body üres esetén (nincs egyáltalán filter mező), akkor 400-as hiba:
		if (filter == null || (country == null && city == null && zip == null && street == null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty filter body is not allowed");
		}
		
		//Ha nincs megadva size, akkor page legyen 0:
		int effectivePage = (size == null) ? 0 : pageable.getPageNumber();
		
		//Ha nincs megadva size, akkor minden találat egy oldalon (MAX_VALUE):
		int effectiveSize = (size == null) ? Integer.MAX_VALUE : size;

		//Default rendezés (ha nincs sort, akkor id asc):
		Sort effectiveSort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by("id").ascending();
				
		//Felülírom a size-ot és a defaultSort-t:
		int effectivePageNumber = (size == null) ? 0 : pageable.getPageNumber();
		Pageable effectivePageable = PageRequest.of(effectivePageNumber, effectiveSize, effectiveSort); 

		//filter body-ban jön be, és pageable:
		Page<Address> page = addressService.search(city, zip, street, country, effectivePageable);
		
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
	
	
	
	
	
	
	
	
	
}
