package com.sylvaingoutouly.datacouch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@SpringBootApplication
@EnableCouchbaseRepositories(basePackages = {"com.sylvaingoutouly.datacouch.repository"})
public class Application {

	 public static void main(String[] args) {
	     SpringApplication.run(Application.class, args);
	   }

}
