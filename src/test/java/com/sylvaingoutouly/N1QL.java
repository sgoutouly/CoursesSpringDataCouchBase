package com.sylvaingoutouly;

import static com.couchbase.client.java.query.N1qlQuery.simple;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;

import org.junit.Test;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.Index;
import com.couchbase.client.java.query.N1qlQueryResult;


public class N1QL {

	@Test public void n1qlSelectOnBeer() {
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment.create();
		CouchbaseCluster cluster = CouchbaseCluster.create(env);
		Bucket beerSample = cluster.openBucket("travel-sample");
		
		beerSample.query(simple(Index.createPrimaryIndex().on("beer-sample")));
		
		N1qlQueryResult beers = beerSample.query(simple(select("*").from(i("beer-sample")).limit(10)));		
		System.out.println(beers.errors().toString());
		System.out.println(beers.info().elapsedTime());
		System.out.println(beers.info().resultCount());
		
		cluster.disconnect();
	}

}
