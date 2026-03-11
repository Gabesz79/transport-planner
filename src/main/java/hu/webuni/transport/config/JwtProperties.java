package hu.webuni.transport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "transport.jwt")
public class JwtProperties {
	
	private String secret;
	
	private String issuer = "transport";
	
	private long durationSeconds = 600;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Long getDurationSeconds() {
		return durationSeconds;
	}

	public void setDurationSeconds(Long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
	
	

}
