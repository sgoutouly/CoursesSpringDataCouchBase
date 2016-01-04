package com.sylvaingoutouly.datacouch.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.hateoas.core.Relation;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor // Lombok shortcuts 
@Document // Document Couchbase
@Relation(collectionRelation = "listes") // Sprint hateoas : name of the attribute holding the items collection
public class Liste extends Entity { 

	@Version private long version; // Optimistic locking with Spring Data 
	
	@Field private String titre;
	@Field @JsonFormat(pattern = "dd/MM/yyyy") private Date dateRedaction;
    @Field @JsonFormat(pattern = "dd/MM/yyyy") private Date dateCourse;
    @Field private List<Course> courses;
    
    @Data @NoArgsConstructor
    public static class Course {
    	@Field private String designation;
    	@Field private int qte;
    	@Field private String unite;
    }
    
}
