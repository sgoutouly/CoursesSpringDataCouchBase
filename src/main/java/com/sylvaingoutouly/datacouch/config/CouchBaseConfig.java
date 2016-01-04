package com.sylvaingoutouly.datacouch.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.query.Consistency;

@Configuration
public class CouchBaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    protected List<String> getBootstrapHosts() {
        return Collections.singletonList("127.0.0.1");
    }

    @Override
    protected String getBucketName() {
        return "shopping-list";
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