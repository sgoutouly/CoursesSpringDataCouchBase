package com.sylvaingoutouly;

import static com.couchbase.client.java.query.N1qlQuery.simple;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.Index;
import com.couchbase.client.java.query.N1qlQueryResult;


public class N1QL {

	private Bucket beerSample;
	private CouchbaseCluster cluster;
	
	
	@Test public void selectOnBeerSync() {
		
		// Needed to use N1QL
		beerSample.query(simple(Index.createPrimaryIndex().on("beer-sample")));
		
		// Simple select on sample beer bucket
		N1qlQueryResult beers = beerSample.query(simple(select("*").from(i("beer-sample")).limit(10)));
		
		assertEquals(10, beers.info().resultCount());
		
		out.println(beers.errors().toString());
		out.println(beers.info().elapsedTime());
		out.println(beers.info().resultCount());
	}
	
	
	@Before public void before() {
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment.create();
		cluster = CouchbaseCluster.create(env);
		beerSample = cluster.openBucket("beer-sample");
	}
	
	@After public void after() {
		cluster.disconnect();
	}

}
