package hu.webuni.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.transport.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
	
	//Visszakapom adott TransportPlan összes section-ét növekvő sorrendben:
	List<Section> findByTransportPlanIdOrderBySectionOrderAsc(Long TransportPlanId);
}
