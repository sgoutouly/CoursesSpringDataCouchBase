package com.sylvaingoutouly.datacouch.repository;

import java.util.List;

import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.sylvaingoutouly.datacouch.model.Test;


/**
 * Test repository to retrieve a subset of fields from Liste entities in different (Test) entities
 */
public interface TestRepository extends CrudRepository<Test, String> {

	// #{#n1ql.fields} get only CAS and ID from META (required by spring data to build entity)
	// this query gets only dateRedaction data field
	// Whe use here a manual filter to mention a different entity from Test to map some fields from Liste document in a Test entity
	@Query("SELECT #{#n1ql.fields}, dateRedaction FROM #{#n1ql.bucket} WHERE _class='com.sylvaingoutouly.datacouch.model.Liste'")
	List<Test> findAllTests();
	

	
}
