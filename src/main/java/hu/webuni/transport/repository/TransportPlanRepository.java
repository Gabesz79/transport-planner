package hu.webuni.transport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hu.webuni.transport.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long>{

	//egyetlen JOIN-os lekérdezéssel betölti az összes plan stops kapcsolatait
	@EntityGraph(attributePaths = "stops")
	@Query("SELECT tp FROM TransportPlan tp") //metódusnév nem illik bele a Repository értelmezési mintába, ezért kell a query
	List<TransportPlan> findAllWithStops();
	
	List<TransportPlan> findAll();
	
	@EntityGraph(attributePaths = "stops")
	@Query("SELECT tp FROM TransportPlan tp WHERE tp.id = :id")
	Optional<TransportPlan> findByIdWithStops(@Param("id") Long id);
	
	
	
	
}
