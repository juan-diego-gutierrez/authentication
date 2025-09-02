package co.com.pragma.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

  @Override
  @NonNull
  public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().value();
    if (path.contains("/login")) {
      return chain.filter(exchange);
    }
    String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (auth == null) {
      return Mono.error(new Throwable("no token was found"));
    }
    if (!auth.startsWith("Bearer ")) {
      return Mono.error(new Throwable("invalid auth"));
    }
    String token = auth.replace("Bearer ", "");
    exchange.getAttributes().put("token", token);
    return chain.filter(exchange);
  }
}
