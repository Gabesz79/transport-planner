package hu.webuni.transport.dto;

public class DelayRequestDto {

	//melyik milestone-n keletkezett a késés:
	private Long milestoneId;
	
	//késés hossza percben
	private Integer minutes;

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Long getMilestoneId() {
		return milestoneId;
	}

	public void setMilestoneId(Long milestoneId) {
		this.milestoneId = milestoneId;
	}
	
}
