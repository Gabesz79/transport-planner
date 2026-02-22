package hu.webuni.transport.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionDto {

	private Long id;
	
	//0-tól számozott sorrend a plan-en belül:
	private Integer sectionOrder;
	
	//id-kat küldök a, hogy elkerüljem a körkörös (végtelen ciklusú) Json-t:
	private Long transportPlanId;
	
	private Long fromMilestoneId;
	
	private Long toMilestoneId;
	
	//full eset: milestone-k részletezéséhez:
	private MilestoneDto fromMilestone;
	
	private MilestoneDto toMilestone;

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

	public Long getTransportPlanId() {
		return transportPlanId;
	}

	public void setTransportPlanId(Long transportPlanId) {
		this.transportPlanId = transportPlanId;
	}

	public Long getFromMilestoneId() {
		return fromMilestoneId;
	}

	public void setFromMilestoneId(Long fromMilestoneId) {
		this.fromMilestoneId = fromMilestoneId;
	}

	public Long getToMilestoneId() {
		return toMilestoneId;
	}

	public void setToMilestoneId(Long toMilestoneId) {
		this.toMilestoneId = toMilestoneId;
	}

	public MilestoneDto getFromMilestone() {
		return fromMilestone;
	}

	public void setFromMilestone(MilestoneDto fromMilestone) {
		this.fromMilestone = fromMilestone;
	}

	public MilestoneDto getToMilestone() {
		return toMilestone;
	}

	public void setToMilestone(MilestoneDto toMilestone) {
		this.toMilestone = toMilestone;
	}
	
	
}
