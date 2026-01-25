package br.com.hackathon.sus.prenatal_auth.infrastructure.config.security.custom;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserAuthorities {

	private String username;
    private Long userId;
	private String cpf;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserAuthorities(String username, Long userId, String cpf, Collection<? extends GrantedAuthority> authorities) {
		this.username = username;
        this.userId = userId;
        this.cpf = cpf;
        this.authorities = authorities;
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

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
