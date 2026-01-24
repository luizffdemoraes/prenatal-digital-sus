package br.com.hackathon.sus.prenatal_auth.infrastructure.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que intercepta GET /actuator/health e retorna o JSON no formato esperado:
 * {"groups":["liveness","readiness"],"status":"UP"}
 * Os subcaminhos /actuator/health/liveness e /actuator/health/readiness seguem
 * sendo atendidos pelo Actuator padrão.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HealthCheckResponseFilter implements Filter {

    private static final String HEALTH_PATH = "/actuator/health";

    private final HealthEndpoint healthEndpoint;

    public HealthCheckResponseFilter(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if ("GET".equals(req.getMethod()) && HEALTH_PATH.equals(req.getRequestURI())) {
            HealthComponent health = healthEndpoint.health();
            String status = health.getStatus().getCode();

            String json = "{\"groups\":[\"liveness\",\"readiness\"],\"status\":\"" + status + "\"}";
            res.setStatus("UP".equals(status) ? 200 : 503);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(json);
            return;
        }

        chain.doFilter(request, response);
    }
}
