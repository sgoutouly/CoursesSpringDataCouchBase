package com.sylvaingoutouly.datacouch.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import com.couchbase.client.java.Bucket;

@Configuration
public class MultiCouchBaseConfig extends AbstractCouchbaseConfiguration {
	
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
   
   @Bean
   public Bucket contextBucket() throws Exception {
     return couchbaseCluster().openBucket("context", "");
   }

   @Bean
   public Bucket cacheBucket() throws Exception {
     return couchbaseCluster().openBucket("cache", "");
   }

   @Bean
   public Bucket securityBucket() throws Exception {
     return couchbaseCluster().openBucket("security", "");
   }

   @Bean(name = "contextTemplate")
   public CouchbaseTemplate contextTemplate() throws Exception {
     CouchbaseTemplate template = new CouchbaseTemplate(couchbaseClusterInfo(), //reuse the default bean
         contextBucket(), mappingCouchbaseConverter(), translationService() //default beans here as well
     );
     template.setDefaultConsistency(getDefaultConsistency());
     return template;
   }
    
   @Bean(name = "cacheTemplate")
   public CouchbaseTemplate cacheTemplate() throws Exception {
     CouchbaseTemplate template = new CouchbaseTemplate(couchbaseClusterInfo(), //reuse the default bean
         securityBucket(), mappingCouchbaseConverter(), translationService() //default beans here as well
     );
     template.setDefaultConsistency(getDefaultConsistency());
     return template;
   }
   
   @Bean(name = "securityTemplate")
   public CouchbaseTemplate securityTemplate() throws Exception {
     CouchbaseTemplate template = new CouchbaseTemplate(couchbaseClusterInfo(), //reuse the default bean
         securityBucket(), mappingCouchbaseConverter(), translationService() //default beans here as well
     );
     template.setDefaultConsistency(getDefaultConsistency());
     return template;
   }

   
   
}