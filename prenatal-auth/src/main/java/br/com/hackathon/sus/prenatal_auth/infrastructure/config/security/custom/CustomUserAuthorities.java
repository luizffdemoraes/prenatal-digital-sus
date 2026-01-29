package br.com.hackathon.sus.prenatal_auth.infrastructure.config.security.custom;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CustomUserAuthorities {

	private final String username;
    private final Long userId;
	private final String cpf;
	private final Collection<GrantedAuthority> authorities;
	
	public CustomUserAuthorities(String username, Long userId, String cpf, Collection<? extends GrantedAuthority> authorities) {
		this.username = username;
        this.userId = userId;
        this.cpf = cpf;
        this.authorities = List.copyOf(authorities);
	}
	
	public String getUsername() {
		return username;
	}
	
    public Long getUserId() {
        return userId;
    }
	
	public String getCpf() {
		return cpf;
	}
	
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
