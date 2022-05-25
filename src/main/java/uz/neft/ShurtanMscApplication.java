package uz.neft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
//		(exclude = {DataSourceAutoConfiguration.class})
//@EnableScheduling
public class ShurtanMscApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShurtanMscApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(2);
		httpRequestFactory.setConnectTimeout(2);
		httpRequestFactory.setReadTimeout(2);

		return new RestTemplate(httpRequestFactory);
	}
}

