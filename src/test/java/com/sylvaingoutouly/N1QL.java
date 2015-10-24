package com.sylvaingoutouly;

import static com.couchbase.client.java.query.N1qlQuery.simple;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.Index;
import com.couchbase.client.java.query.N1qlQueryResult;


public class N1QL {

	private Bucket beerSample;
	private CouchbaseCluster cluster;
	
	
	@Test public void selectOnBeerSync() {		
		// Simple select on sample beer bucket
		N1qlQueryResult beers = beerSample.query(simple(select("*").from(i("beer-sample")).limit(10)));
		
		assertEquals(10, beers.info().resultCount());
		
		out.println(beers.errors().toString());
		out.println(beers.info().elapsedTime());
		out.println(beers.info().resultCount());
	}
	
	// TODO Need to block to force the test waiting the async response or somthing like this ...
	@Test public void selectOnBeerAsync() {		
		// Simple select on sample beer async bucket
		Observable<AsyncN1qlQueryResult> beers = beerSample.async().query(simple(select("*").from(i("beer-sample")).limit(10)));
		// Handling async result
		beers.subscribe(result -> {
			result.info().subscribe(info -> assertEquals(10, info.resultCount()));
			out.println(result.errors().map(errors -> errors.toString()));
			out.println(result.info().map(info -> info.resultCount()));
			out.println(result.info().map(info -> info.elapsedTime()));
		});	
	}
	
	
	@Before public void before() {
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment.create();
		cluster = CouchbaseCluster.create(env);
		beerSample = cluster.openBucket("beer-sample");
		// Needed to use N1QL
		beerSample.query(simple(Index.createPrimaryIndex().on("beer-sample")));
	}
	
	@After public void after() {
		cluster.disconnect();
	}

}
