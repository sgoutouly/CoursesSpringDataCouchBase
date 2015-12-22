package com.sylvaingoutouly.datacouch.repository;

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.repository.CrudRepository;

import com.sylvaingoutouly.datacouch.model.Liste;

@N1qlPrimaryIndexed @ViewIndexed(viewName = "all", designDoc = "liste")
public interface ListeRepository extends CrudRepository<Liste, String> {

}
