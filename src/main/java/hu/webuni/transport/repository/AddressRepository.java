package hu.webuni.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import hu.webuni.transport.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

	//POST előtt ellenőrzi, hogy létezik-e egyezőség mind az 5 mező esetén:
	boolean existsByCountryAndZipAndCityAndStreetAndHouseNumber(
			String country, String zip, String city, String street, String houseNumber);
	
	//UPDATE előtt ellenőrzi, hogy létezik-e egyezőség mind az 5 mező esetén úgy, hogy a vizsgált objektum nem lehet ugyanaz az id-jű objektum, hiszen a többi objektumot kell összehasonlítani a vizsgált id-jű objektummal, így a vizsgált id-jű objektumot ki kell zárni az ellenőrzésből (ezért IdNot):
	boolean existsByCountryAndZipAndCityAndStreetAndHouseNumberAndIdNot(
			String country, String zip, String city, String street, String houseNumber, Long id);
}
