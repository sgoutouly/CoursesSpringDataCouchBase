package com.sylvaingoutouly.datacouch;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

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

}
