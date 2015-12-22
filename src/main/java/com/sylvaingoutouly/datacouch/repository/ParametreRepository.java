package com.sylvaingoutouly.datacouch.repository;

import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.repository.CrudRepository;

import com.sylvaingoutouly.datacouch.model.Parametre;

@ViewIndexed(viewName = "all", designDoc = "Parametre")
public interface ParametreRepository extends CrudRepository<Parametre, String> {

}
