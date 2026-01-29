package br.com.hackathon.sus.prenatal_auth.infrastructure.config.security.custom;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	private static final long serialVersionUID = 1L;
	
	private final String username;
	private final String password;
	private final Set<String> scopes;
	
	public CustomPasswordAuthenticationToken(Authentication clientPrincipal,
			@Nullable Set<String> scopes, @Nullable Map<String, Object> additionalParameters) {
		
		super(new AuthorizationGrantType("password"), clientPrincipal,
				additionalParameters != null ? additionalParameters : Collections.emptyMap());

		Map<String, Object> params = additionalParameters != null ? additionalParameters : Collections.emptyMap();
		this.username = (String) params.get("username");
		this.password = (String) params.get("password");
		this.scopes = Collections.unmodifiableSet(
				scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
	}

	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}

	public Set<String> getScopes() {
		return this.scopes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CustomPasswordAuthenticationToken that)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		return Objects.equals(username, that.username)
				&& Objects.equals(password, that.password)
				&& Objects.equals(scopes, that.scopes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), username, password, scopes);
	}
}
