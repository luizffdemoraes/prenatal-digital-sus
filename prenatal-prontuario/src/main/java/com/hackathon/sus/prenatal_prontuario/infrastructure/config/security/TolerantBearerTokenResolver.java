package com.hackathon.sus.prenatal_prontuario.infrastructure.config.security;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolver que aceita o header Authorization em formatos alternativos enviados pelo cliente:
 * <ul>
 *   <li>Padrão: {@code Bearer &lt;jwt&gt;}
 *   <li>Tolerado: {@code Bearer "access_token": "&lt;jwt&gt;",} (fragmento JSON copiado para o header)
 * </ul>
 * Extrai o JWT de {@code "access_token": "..."} quando presente.
 */
public class TolerantBearerTokenResolver implements BearerTokenResolver {

    private static final Pattern ACCESS_TOKEN_JSON = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JWT_THREE_PARTS = Pattern.compile("[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");

    @Override
    public String resolve(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String value = auth.substring(7).trim();
        if (value.isEmpty()) {
            return null;
        }

        // 1) Fragmento tipo "access_token": "eyJ..."
        Matcher m = ACCESS_TOKEN_JSON.matcher(value);
        if (m.find()) {
            return m.group(1).trim();
        }

        // 2) JWT puro (três partes separadas por ponto)
        if (JWT_THREE_PARTS.matcher(value).matches()) {
            return value;
        }

        // 3) JWT embutido em texto (ex.: mais JSON)
        m = JWT_THREE_PARTS.matcher(value);
        if (m.find()) {
            return m.group(0);
        }

        // 4) Fallback: repassa o valor (decoder pode rejeitar)
        return value;
    }
}
