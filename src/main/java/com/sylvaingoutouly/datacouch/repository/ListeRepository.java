package com.sylvaingoutouly.datacouch.repository;

import java.util.List;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.repository.CrudRepository;

import com.sylvaingoutouly.datacouch.model.Liste;
import com.sylvaingoutouly.datacouch.model.Test;

@N1qlPrimaryIndexed 
@ViewIndexed(viewName = "all", designDoc = "liste")
public interface ListeRepository extends CrudRepository<Liste, String> {

	// #{#n1ql.selectEntity} get CAS and ID from META and all data fields for entity (*) from current bucket
	// N1QL query: "SELECT META(`shopping-list`).id AS _ID, META(`shopping-list`).cas AS _CAS, `shopping-list`.* FROM `shopping-list` WHERE `_class` = \"com.sylvaingoutouly.datacouch.model.Liste\"","scan_consistency":"not_bounded"
	// #{#n1ql.filter}" generate the filter query based on the Entity managed by the repository class name (here com.sylvaingoutouly.datacouch.model.Liste)
	@Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter}")
	List<Liste> findAllListes(); // To replace view index
		
	@Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND titre=$1")
	List<Test> findWithParam(String titre);
}
