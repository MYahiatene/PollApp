package gpse.umfrato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
}
