package hu.webuni.transport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "transport.delay")
public class DelayProperties {

	//a bevétel csökkentés küszöbértékekhez kötött, amit a config-ból olvasok ki:
	private Integer revenueReductionPercent30;
	private Integer revenueReductionPercent60;
	private Integer revenueReductionPercent120;
	public Integer getRevenueReductionPercent30() {
		return revenueReductionPercent30;
	}
	public void setRevenueReductionPercent30(Integer revenueReductionPercent30) {
		this.revenueReductionPercent30 = revenueReductionPercent30;
	}
	public Integer getRevenueReductionPercent60() {
		return revenueReductionPercent60;
	}
	public void setRevenueReductionPercent60(Integer revenueReductionPercent60) {
		this.revenueReductionPercent60 = revenueReductionPercent60;
	}
	public Integer getRevenueReductionPercent120() {
		return revenueReductionPercent120;
	}
	public void setRevenueReductionPercent120(Integer revenueReductionPercent120) {
		this.revenueReductionPercent120 = revenueReductionPercent120;
	}
	
	
	
	
}
