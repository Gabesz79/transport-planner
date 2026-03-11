package hu.webuni.transport.web;


import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.transport.dto.LoginRequestDto;
import hu.webuni.transport.dto.LoginResponseDto;
import hu.webuni.transport.security.JwtService;

@RestController
@RequestMapping("/api")
public class LoginController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public LoginController(AuthenticationManager authenticationManager, JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/login")
	public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
					
			UserDetails uesrDetails = (UserDetails) auth.getPrincipal();		
			String token = jwtService.createJwt(uesrDetails);
			
			return new LoginResponseDto(token);			
		}
		catch(AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
		}
		
	}
	
	
	
	
	
}
