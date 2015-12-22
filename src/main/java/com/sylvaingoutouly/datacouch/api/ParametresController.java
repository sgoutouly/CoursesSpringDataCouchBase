package com.sylvaingoutouly.datacouch.api;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sylvaingoutouly.datacouch.repository.ParametreRepository;

@RestController @RequestMapping(value = "/courses/parametres", produces = "application/json")
public class ParametresController {

	@Autowired ParametreRepository parametres;
	
	@RequestMapping( method = GET, value = "/")
	public HttpEntity<?> listes() {
		return ok(parametres.findOne("parametres"));
	}
		
}
