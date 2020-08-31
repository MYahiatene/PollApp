package gpse.umfrato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class UmfratoApplication {
    /**
     * This method starts the program.
     * @param args the program arguments from the console
     */
    public static void main(final String... args) {
        SpringApplication.run(UmfratoApplication.class, args);
    }
    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Add a simple re-route from: /get to: http://httpbin.org:80
            // Add a simple "Hello:World" HTTP Header
            .route(p -> p
                .path("/get") // intercept calls to the /get path
                .filters(f -> f.addRequestHeader("Hello", "World")) // add header
                .uri("http://httpbin.org:80")) // forward to httpbin
            .build();
    }


}
