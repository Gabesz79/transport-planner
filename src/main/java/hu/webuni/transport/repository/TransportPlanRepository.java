package hu.webuni.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.transport.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long>{

}
