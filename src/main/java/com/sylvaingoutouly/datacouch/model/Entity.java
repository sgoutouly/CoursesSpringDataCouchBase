package com.sylvaingoutouly.datacouch.model;

import java.util.UUID;

import lombok.Getter;

import com.couchbase.client.java.repository.annotation.Id;

/**
 * Abastraction pour mutualiser la gestion des IDs pour
 * toutes les entités
 * 
 * Une classe de service permettrait de gérer ceci
 */
@Getter
public abstract class Entity {

	/** l'identifiant couchbase ducoument */
	@Id public String id;
    
	/** 
	 * Fabrique un identifiant
	 * return {@link Entity} car c'est une méthode fluent 
	 */
	public Entity newId() {
    	this.id = UUID.randomUUID().toString();
    	return this;
    }

}