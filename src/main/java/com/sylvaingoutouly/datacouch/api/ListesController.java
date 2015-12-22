package com.sylvaingoutouly.datacouch.api;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sylvaingoutouly.datacouch.model.Liste;
import com.sylvaingoutouly.datacouch.repository.ListeRepository;

@RestController 
@RequestMapping( value = "/courses/listes" , produces = "application/json")
public class ListesController {

	@Autowired ListeRepository listes;
	
	@RequestMapping(method = GET, value = "/{id}")
	public HttpEntity<?> liste(@PathVariable String id) {
		return ok(listes.findOne(id));
	}

	@RequestMapping(method = GET, value = "/")
	public HttpEntity<?> listes() {
		return ok(listes.findAll());
	}
	
	@RequestMapping(method = PUT, value = "/{id}")
	public HttpEntity<?> update(@RequestBody Liste liste, @PathVariable String id) {
		listes.save(liste);
		return noContent().build();
	}
	
	@RequestMapping(method = POST, value = "/")
	public HttpEntity<?> add(@RequestBody Liste liste) {
		liste.newId(); // Une classe de service permettrait de gérer ceci
		listes.save(liste);
		return noContent().build();
	}
	
	@RequestMapping(method = DELETE, value = "/{id}")
	public HttpEntity<?> delete(@PathVariable String id) {
		listes.delete(id);
		return noContent().build();
	}
	
}
