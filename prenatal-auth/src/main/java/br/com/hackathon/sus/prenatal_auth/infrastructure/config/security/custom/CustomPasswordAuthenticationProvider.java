package br.com.hackathon.sus.prenatal_auth.infrastructure.config.security.custom;


import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.UserEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomPasswordAuthenticationProvider implements AuthenticationProvider {

	private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
	private final OAuth2AuthorizationService authorizationService;
	private final UserDetailsService userDetailsService;
	private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
	private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
	private String username = "";
	private String password = "";
	private Set<String> authorizedScopes = new HashSet<>();


	public CustomPasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                                OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                                UserDetailsService userDetailsService,
                                                PasswordEncoder passwordEncoder,
                                                UserRepository userRepository) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
		Assert.notNull(tokenGenerator, "TokenGenerator cannot be null");
		Assert.notNull(userDetailsService, "UserDetailsService cannot be null");
		Assert.notNull(passwordEncoder, "PasswordEncoder cannot be null");
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		CustomPasswordAuthenticationToken customPasswordAuthenticationToken = (CustomPasswordAuthenticationToken) authentication;
		OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(customPasswordAuthenticationToken);
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
		username = customPasswordAuthenticationToken.getUsername();
		password = customPasswordAuthenticationToken.getPassword();	
		
		UserDetails user = null;
		try {
			user = userDetailsService.loadUserByUsername(username);
		} catch (UsernameNotFoundException e) {
			throw new OAuth2AuthenticationException("Credenciais inválidas");
		}
				
		if (!passwordEncoder.matches(password, user.getPassword()) || !user.getUsername().equals(username)) {
			throw new OAuth2AuthenticationException("Credenciais inválidas");
		}
		
		authorizedScopes = user.getAuthorities().stream()
				.map(scope -> scope.getAuthority())
				.filter(scope -> registeredClient.getScopes().contains(scope))
				.collect(Collectors.toSet());

        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new OAuth2AuthenticationException("Credenciais inválidas"));
        Long userId = userEntity.getId().longValue();
		
		//-----------Create a new Security Context Holder Context----------
		OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		CustomUserAuthorities customPasswordUser = new CustomUserAuthorities(username, userId, user.getAuthorities());
		oAuth2ClientAuthenticationToken.setDetails(customPasswordUser);
		
		var newcontext = SecurityContextHolder.createEmptyContext();
		newcontext.setAuthentication(oAuth2ClientAuthenticationToken);
		SecurityContextHolder.setContext(newcontext);		
		
		//-----------TOKEN BUILDERS----------
		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient)
				.principal(clientPrincipal)
				.authorizationServerContext(AuthorizationServerContextHolder.getContext())
				.authorizedScopes(authorizedScopes)
				.authorizationGrantType(new AuthorizationGrantType("password"))
				.authorizationGrant(customPasswordAuthenticationToken);
		
		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
				.attribute(Principal.class.getName(), clientPrincipal)
				.principalName(clientPrincipal.getName())
				.authorizationGrantType(new AuthorizationGrantType("password"))
				.authorizedScopes(authorizedScopes);
		
		//-----------ACCESS TOKEN----------
		OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
		OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
		if (generatedAccessToken == null) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
					"O gerador de token falhou ao gerar o token de acesso.", ERROR_URI);
			throw new OAuth2AuthenticationException(error);
		}

		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
		if (generatedAccessToken instanceof ClaimAccessor) {
			authorizationBuilder.token(accessToken, (metadata) ->
					metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
		} else {
			authorizationBuilder.accessToken(accessToken);
		}
				
		OAuth2Authorization authorization = authorizationBuilder.build();
		this.authorizationService.save(authorization);
		
		return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CustomPasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private static OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
		
		OAuth2ClientAuthenticationToken clientPrincipal = null;
		if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
			clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
		}
		if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
			return clientPrincipal;
		}
		throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
	}	
}
