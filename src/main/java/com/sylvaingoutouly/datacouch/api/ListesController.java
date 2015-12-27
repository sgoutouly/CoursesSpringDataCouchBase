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
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sylvaingoutouly.datacouch.model.Liste;
import com.sylvaingoutouly.datacouch.repository.ListeRepository;

@RestController 
@RequestMapping( value = "/courses/listes")
public class ListesController {

	@Autowired ListeRepository listes;
	
	@RequestMapping(method = GET, value = "/{id}")
	public Callable<HttpEntity<?>> liste(@PathVariable final String id) {
		return () -> {
			Resource<Liste> resource = new Resource<Liste>(listes.findOne(id));
			resource.add(linkTo(methodOn(ListesController.class).liste(id)).withSelfRel());
			return ok(resource);
		};
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
