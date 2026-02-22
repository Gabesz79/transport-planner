package hu.webuni.transport.service;

import org.springframework.data.jpa.domain.Specification;

import hu.webuni.transport.model.Address;
import hu.webuni.transport.model.Address_;

public class AddressSpecifications {
	
	public static Specification<Address> hasCity(String city) {
		//Itt tanulás célzattal részletesen megfogalmazva magamnak:
		//DB-ben az Address tábla city mezőit azonosítva azokat kisbetűsként kezelve részleges 
		//egyezést keressen a bejövő paraméterű kisbetűsített keresőszóval, és azokat az
		//Address rekordokat adja vissza találatnak a DB-ből, amelynek city mezői tartalmazzák a 
		//keresőszót (bármi lehet előtte és utána is)
		//Részleges egyezőség a keresésben case-insenitive módon:
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.city)), (city + "%").toLowerCase() + "%");
	}
	
	public static Specification<Address> hasZip(String zip) {
		//Teljes egyezőség a keresésben:
		return (root, cq, cb) -> cb.equal(root.get(Address_.zip), zip);
	}
	
	public static Specification<Address> hasStreet(String street) {
		//Részleges egyezőség a keresésben case-insenitive módon:
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.street)), (street + "%").toLowerCase() + "%");
	}
	
	public static Specification<Address> hasCountry(String country) {
		//Teljes egyezőség a keresésben case-insenitive módon:
		return (root, cq, cb) -> cb.equal(cb.lower(root.get(Address_.country)), country.toLowerCase());
	}

}
