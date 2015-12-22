package com.sylvaingoutouly.datacouch.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;

@Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor
@Document
public class Parametre extends Entity {
    
	@Field private List<Produit> produits;
	@Field private List<Unite> unites;

	@Data @NoArgsConstructor
    public static class Produit {
		@Field private String designation;
		@Field private String categorie;
    }
	
    @Data @NoArgsConstructor
    public static class Unite {
    	@Field private String designation;
    }
    
}