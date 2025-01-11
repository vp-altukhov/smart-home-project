package ru.newvasuki.smarthome.controller;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RedirectToIndexFilter implements Filter {
    private static final String[] NO_FILTERED = new String[] { "/static", "/ws-message", "/api", "/app", "/manifest.json", "/favicon.ico", "/logo192.png" };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();

        for (String filter: NO_FILTERED) {
            if (requestURI.startsWith(filter)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // all other requests will be forwarded to index page.
        request.getRequestDispatcher("/").forward(request, response);
    }
}
