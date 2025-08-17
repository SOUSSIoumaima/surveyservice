package horizon.surveyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SurveyserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyserviceApplication.class, args);
	}

}
