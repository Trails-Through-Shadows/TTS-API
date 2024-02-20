//package cz.trailsthroughshadows.algorithm.session;
//
//import cz.trailsthroughshadows.api.rest.exception.RestException;
//import cz.trailsthroughshadows.api.rest.model.MessageResponse;
//import cz.trailsthroughshadows.api.rest.model.error.RestError;
//import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
//import io.swagger.v3.oas.annotations.info.Contact;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.info.License;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.constraints.Null;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.UUID;
//
//@Component
//public class TokenFilter extends GenericFilterBean {
//
//    @Autowired
//    private SessionHandler sessionHandler;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        if (request.getRequestURI().contains("/session/login") || request.getRequestURI().contains("/swagger") || request.getRequestURI().contains("/api-docs")) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        try {
//            UUID auth = UUID.fromString(request.getHeader("Authorization"));
//
//            if (sessionHandler.isSessionValid(auth)) {
//                filterChain.doFilter(servletRequest, servletResponse);
//            } else {
//                throw new RestException(RestError.of(HttpStatus.UNAUTHORIZED, "Invalid session token!"));
//            }
//        } catch (Exception e) {
//            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            PrintWriter writer = httpResponse.getWriter();
//            writer.print("{\"error\": \"Invalid session token!\"}");
//            writer.flush();
//            writer.close();
//        }
//    }
//
//    private SecurityScheme createAPIKeyScheme() {
//        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
//            .bearerFormat("JWT")
//            .scheme("bearer");
//    }
//
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
//                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
//                .info(new Info().title("My REST API")
//                        .description("Some custom description of API.")
//                        .version("1.0").contact(new Contact().name("Sallo Szrajbman").email("www.baeldung.com").url("salloszraj@gmail.com"))
//                        .license(new License().name("License of API").url("API license URL")));
//    }
//}
