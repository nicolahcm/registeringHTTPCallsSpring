package com.learning.registeringHttpCalls.networkSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

    // Cerca di fare una copia di request
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(req);
        String requestURI = cachedBodyHttpServletRequest.getRequestURI();

        if (!requestURI.contains("swagger")) { //Ignora le http requests che contengono swagger nel path. Così non fa il loro log
            // --- Record Entry --- Da mettere qua una funzione custom.
            LOGGER.info("Incoming Request: {} {} from {} with protocol {}.\n Query Parameters: {}.\n Headers: {}.\n Payload: {}.", cachedBodyHttpServletRequest.getMethod(), cachedBodyHttpServletRequest.getRequestURI(), cachedBodyHttpServletRequest.getRemoteAddr(), cachedBodyHttpServletRequest.getProtocol(), getQueryParam(cachedBodyHttpServletRequest), getRequestHeaders(cachedBodyHttpServletRequest), getRequestBody(cachedBodyHttpServletRequest));

        }

        chain.doFilter(cachedBodyHttpServletRequest, response);
    }


    private String getQueryParam(HttpServletRequest request) {
        Map<String, String[]> queryParams = request.getParameterMap();

        StringBuilder sb = new StringBuilder();
        for (String paramName : queryParams.keySet()) {
            String[] paramValues = queryParams.get(paramName);
            sb.append(" ").append(paramName).append(" = ").append(Arrays.toString(paramValues)).append(",");
        }
        return sb.toString();
    }

    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder("{ ");
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> headers.append(headerName).append("=").append(request.getHeader(headerName)).append(", "));
        headers.append("}");
        return headers.toString();
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder payload = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            payload.append(line);
        }
        return payload.toString();
    }
}
