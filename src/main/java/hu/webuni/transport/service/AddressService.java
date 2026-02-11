package hu.webuni.transport.service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.model.Address;
import hu.webuni.transport.repository.AddressRepository;


//ReadOnly-t teszek rá, mert egyrészt látom, hogy a Service metódusok lekérdezések, 
//amelyik pedig nem az, az a metódusban kap egy Transactional-t, másrészt ha véletlenül író 
//művelet kerül egy olvasó metódusba, akkor hamarabb kiderülhet (hibaazonosítás könnyebb)
@Service
@Transactional(readOnly = true)
public class AddressService {
	
	private final AddressRepository addressRepository;

	public AddressService(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}
	
	public List<Address> findAll() {
		return addressRepository.findAll();
	}
	
	public Address findById(Long id) {
		return addressRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@Transactional
	public Address save(Address address) {
		//Végre kell hajtani a mező azonosság ellenőrzést POST esetén, és mező azonosság + id nem azonosság ellenörzést PUT esetén: 
		if (address.getId() == null) {
			//POST create eset:
			if (addressRepository.existsByCountryAndZipAndCityAndStreetAndHouseNumber(address.getCountry(), address.getZip(), address.getCity(), address.getStreet(), address.getHouseNumber())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Address already exists!");
			}
		}
		//PUT update eset:
		else {
			if (addressRepository.existsByCountryAndZipAndCityAndStreetAndHouseNumberAndIdNot(address.getCountry(), address.getZip(), address.getCity(), address.getStreet(), address.getHouseNumber(), address.getId())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Address already exists!");
			}
		}
		return addressRepository.save(address);
	}
	
	@Transactional
	public void deleteById(Long id) {
		addressRepository.deleteById(id);
	}
	
//	public Page<Address> findAll(Specification<Address> spec, Pageable pageable) {
//		return addressRepository.findAll(spec, pageable);
//	}
	
	public Page<Address> search(String city, String zip, String street, String country, Pageable pageable) {
		
		Specification<Address> spec = (root, cq, cb) -> cb.conjunction(); //where(null) helyett
		
		if(StringUtils.hasText(city)) {
			spec = spec.and(AddressSpecifications.hasCity(city));
		}
		
		if(StringUtils.hasText(zip)) {
			spec = spec.and(AddressSpecifications.hasZip(zip));
		}
		
		if(StringUtils.hasText(street)) {
			spec = spec.and(AddressSpecifications.hasStreet(street));
		}
		
		if(StringUtils.hasText(country)) {
			spec = spec.and(AddressSpecifications.hasCountry(country));
		}
		
		return addressRepository.findAll(spec, pageable);
	}
	
}
