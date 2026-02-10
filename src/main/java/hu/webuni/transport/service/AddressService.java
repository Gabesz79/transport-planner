package hu.webuni.transport.service;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	
	public Address save(Address address) {
		return addressRepository.save(address);
	}
	
	public void deleteById(Long id) {
		addressRepository.deleteById(id);
	}
	
	public Page<Address> findAll(Specification<Address> spec, Pageable pageable) {
		return addressRepository.findAll(spec, pageable);
	}
	
}
