package hu.webuni.transport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hu.webuni.transport.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long>{

	List<TransportPlan> findAll();
	
	//egyetlen JOIN-os lekérdezéssel betölti az összes plan stops kapcsolatait
	@EntityGraph(attributePaths = "stops")
	@Query("SELECT tp FROM TransportPlan tp") //metódusnév nem illik bele a Repository értelmezési mintába, ezért kell a query
	List<TransportPlan> findAllWithStops();
	
	@EntityGraph(attributePaths = "stops")
	@Query("SELECT tp FROM TransportPlan tp WHERE tp.id = :id")
	Optional<TransportPlan> findByIdWithStops(@Param("id") Long id);
	
	//Megcsinálom, hogy lássuk az Address-t is a lekérdezésben:
	//EntityGraph helyett JOIN FETCH - szerintem sok kapcsolat előtöltésére kifejezőbb
	@Query(""" 
			SELECT DISTINCT tp
			FROM TransportPlan tp
			LEFT JOIN FETCH tp.stops s
			LEFT JOIN FETCH s.address
			""")
	List<TransportPlan> findAllWithStopsAndAddresses();
	
}
