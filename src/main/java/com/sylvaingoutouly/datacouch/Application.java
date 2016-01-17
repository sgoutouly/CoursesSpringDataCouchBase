package com.sylvaingoutouly.datacouch;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.sylvaingoutouly.datacouch.observable.ObservableReturnValueHandler;

import rx.Observable;

@SpringBootApplication
@EnableAsync
@EnableHypermediaSupport(type = HAL)
@EnableCouchbaseRepositories(basePackages = { "com.sylvaingoutouly.datacouch.repository" })
public class Application {

	@Bean public Filter etagFilter() {
		return new ShallowEtagHeaderFilter();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Configuration
	@ConditionalOnClass(Observable.class)
	public static class WebConfig extends WebMvcConfigurerAdapter {
		@Autowired
		private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

		@Bean
		public ObservableReturnValueHandler observableReturnValueHandler() {
			return new ObservableReturnValueHandler();
		}

		@PostConstruct
		public void init() {
			LogFactory.getLog(Application.class).info("Configuration de l'ObservableReturnValueHandler ...");
			
			final List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<>(requestMappingHandlerAdapter.getReturnValueHandlers());
			originalHandlers.add(0, observableReturnValueHandler());
			requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
			
			LogFactory.getLog(Application.class).info("ObservableReturnValueHandler configuré !");
		}
	}
	
}
