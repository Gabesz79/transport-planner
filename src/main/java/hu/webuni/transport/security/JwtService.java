package hu.webuni.transport.security;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import hu.webuni.transport.config.JwtProperties;

@Service
public class JwtService {

	private static String AUTH = "auth";
	
	private final JwtProperties props;
	private final Algorithm algorithm;
	
	public JwtService(JwtProperties props) {
		super();
		this.props = props;
		this.algorithm = Algorithm.HMAC256(props.getSecret());
	}
	
	public String createJwt(UserDetails userDetails) {
		Instant now = Instant.now();
		
		String[] authorities = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toArray(String[]::new);
		
		return JWT.create()
				.withSubject(userDetails.getUsername())
				.withArrayClaim(AUTH, authorities)
				.withIssuedAt(Date.from(now))
				.withExpiresAt(Date.from(now.plusSeconds(props.getDurationSeconds())))
				.withIssuer(props.getIssuer())
				.sign(algorithm);
	}
	
	public UserDetails parseJwt(String jwtToken) {
		DecodedJWT decodedJwt = JWT.require(algorithm)
				.withIssuer(props.getIssuer())
				.build()
				.verify(jwtToken);
		
		List<String> authList = decodedJwt.getClaim(AUTH).asList(String.class);
		
		var granted = (authList == null ? List.<SimpleGrantedAuthority>of() : 
			authList.stream().map(SimpleGrantedAuthority::new).toList());
		
		return new User(decodedJwt.getSubject(), "dumy", granted);
		
	}
	
	
}
