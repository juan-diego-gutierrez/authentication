package co.com.pragma.security.jwt;

import javax.naming.AuthenticationException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  private final JwtProvider jwtProvider;

  public JwtAuthenticationManager(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    return Mono.just(authentication)
        .map(auth -> jwtProvider.getClaims(auth.getCredentials().toString()))
        .onErrorResume(e -> Mono.error(new AuthenticationException("Bad token")))
        .map(claims -> new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
            ((List<?>) claims.getOrDefault("roles", List.of())).stream()
                .filter(Map.class::isInstance).map(Map.class::cast)
                .map(roleMap -> roleMap.get("authority")).filter(String.class::isInstance)
                .map(String.class::cast).map(SimpleGrantedAuthority::new).toList()));
  }
}
