package hu.webuni.transport.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//orderIndex (sorrend) 0-tól:
	private Integer sectionOrder;
	
	//egy milestone újrahasználható több section-ben, 1. út: A->B, 2. út: B->C, 
	//tehát B (id=3 esetén) 1. út vége és a 2. út kezdete, ezért 1. út (section 0) toMilestoneId=3, és 2. út (section 1) fromMilestoneId=3 
	//Many Section -> One milestone (sok section hivatkozhat ugyanarra a milestone-ra), ezért:
	@ManyToOne
	private Milestone fromMilestone;
	
	@ManyToOne
	private Milestone toMilestone;
	
	//egy section egy transportPlan-hez tartozik: (több section-je lehet egy TarnsportPlan-nek):
	@ManyToOne
	private TransportPlan transportPlan;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSectionOrder() {
		return sectionOrder;
	}

	public void setSectionOrder(Integer sectionOrder) {
		this.sectionOrder = sectionOrder;
	}

	public Milestone getFromMilestone() {
		return fromMilestone;
	}

	public void setFromMilestone(Milestone fromMilestone) {
		this.fromMilestone = fromMilestone;
	}

	public Milestone getToMilestone() {
		return toMilestone;
	}

	public void setToMilestone(Milestone toMilestone) {
		this.toMilestone = toMilestone;
	}

	public TransportPlan getTransportPlan() {
		return transportPlan;
	}

	public void setTransportPlan(TransportPlan transportPlan) {
		this.transportPlan = transportPlan;
	}
	
}
