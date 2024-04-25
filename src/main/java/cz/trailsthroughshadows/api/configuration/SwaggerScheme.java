package cz.trailsthroughshadows.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SwaggerScheme {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("BasicAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                )
                .info(new Info().title("TTS API")
                        .description("Our great TTS API!")
                        .version("1.0").contact(new Contact().name("Administrator").email("admin@tts-game.fun").url("admin@tts-game.fun"))
                )
                .addSecurityItem(new SecurityRequirement().addList("BasicAuth", Arrays.asList("read", "write")));
    }
}
