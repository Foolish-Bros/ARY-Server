package site.foolish.ary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class AryApplication {

	public static void main(String[] args) {
		SpringApplication.run(AryApplication.class, args);
	}

}
