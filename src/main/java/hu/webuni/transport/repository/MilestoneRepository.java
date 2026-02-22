package hu.webuni.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.transport.model.Milestone;

public interface MilestoneRepository extends JpaRepository<Milestone, Long>{
	
}
