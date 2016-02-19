package com.sylvaingoutouly.datacouch.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.query.Consistency;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.view.ViewQuery;

@Configuration
public class CouchBaseConfig extends AbstractCouchbaseConfiguration {
	
    @Override
    protected List<String> getBootstrapHosts() {
      // return Collections.singletonList("192.168.99.100:8191");
    	return Arrays.asList("172.17.0.2, 172.17.0.3, 172.17.0.4");
    }

    @Override
    protected String getBucketName() {
        return "travel-sample";
    }

   @Override
    protected String getBucketPassword() {
        return "";
    }
    
    /**
     * Configures the default consistency for generated {@link ViewQuery view queries}
     * and {@link N1qlQuery N1QL queries} in repositories.
     *
     * @return the {@link Consistency consistency} to apply by default on generated queries.
     */
   @Override
    protected Consistency getDefaultConsistency() {
      return Consistency.STRONGLY_CONSISTENT;
    }
    
    
}