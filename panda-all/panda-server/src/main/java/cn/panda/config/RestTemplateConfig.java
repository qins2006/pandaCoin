package cn.panda.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Bean
    public RestTemplate restTemplate(){
		RestTemplate RestTemplate = new RestTemplate(simpleClientHttpRequestFactory());
		RestTemplate.setErrorHandler(new ResponseErrorHandler() {
			
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				if(response != null)
					response.close();
				return false;
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if(response != null)
					response.close();
			}
		});
        return RestTemplate;
    }
 
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(15000);//单位为ms
        factory.setConnectTimeout(15000);//单位为ms
        return factory;
    }
}
