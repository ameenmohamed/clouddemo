package guru.amin.dataapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataapiApplication {

	private static final Logger log = LoggerFactory.getLogger(DataapiApplication.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext appContext  = SpringApplication.run(DataapiApplication.class, args);
		log.info("Application Started ..");
	}

}
