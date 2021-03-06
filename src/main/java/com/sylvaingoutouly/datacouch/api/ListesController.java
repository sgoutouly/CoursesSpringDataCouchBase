package com.sylvaingoutouly.datacouch.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rx.Observable;

import com.sylvaingoutouly.datacouch.model.Liste;
import com.sylvaingoutouly.datacouch.repository.ListeRepository;
import com.sylvaingoutouly.datacouch.repository.TestRepository;

@RestController 
@RequestMapping( value = "/courses/listes")
public class ListesController {

	@Autowired private ListeRepository listes;
	@Autowired private TestRepository tests;
	@Autowired private CouchbaseOperations couchbaseTemplate;
	
	@RequestMapping(method = GET, value = "/{id}")
	public Callable<HttpEntity<?>> liste(@PathVariable final String id) {
		return () -> {
			Resource<Liste> resource = new Resource<Liste>(listes.findOne(id));
			resource.add(linkTo(methodOn(ListesController.class).liste(id)).withSelfRel());
			return ok(resource);
		};
	}
	
	@RequestMapping(method = GET, value = "/observable")
	public Observable<HttpEntity<?>> observable() {
		Resource<Liste> resource = new Resource<Liste>(new Liste());
		return Observable.just(ok(resource));
	}

	@RequestMapping(method = GET)
	public Callable<HttpEntity<?>> listes() {
		return () -> {
			Resources<Liste> resources = new Resources<Liste>(listes.findAll());
			resources.add(linkTo(methodOn(ListesController.class).listes()).withSelfRel());
			return ok(resources);
		};
	}
	
	@RequestMapping(method = PUT, value = "/{id}")
	public Callable<HttpEntity<?>> update(@RequestBody Liste liste, @PathVariable String id) {
		return () -> {
			listes.save(liste);
			return noContent().build();
		};
	}
	
	@RequestMapping(method = POST)
	public Callable<HttpEntity<?>> add(@RequestBody Liste liste) {
		return () -> {
			liste.newId(); // Une classe de service permettrait de gérer ceci
			listes.save(liste);
			listes.count(); // FIX to see the insert immediatly (force view index to refresh ??)
			return created(linkTo(methodOn(this.getClass()).liste(liste.getId())).toUri()).build();
		};
	}
	
	@RequestMapping(method = DELETE, value = "/{id}")
	public Callable<HttpEntity<?>> delete(@PathVariable String id) {
		return () -> {
			listes.delete(id);
			return noContent().build();
		};
	}
	
}
