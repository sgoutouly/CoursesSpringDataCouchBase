package com.sylvaingoutouly.datacouch.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;

@Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor 
@Document	
public class Liste extends Entity {

	@Field public String titre;
	@Field public String dateRedaction;
    @Field public String dateCourse;
    @Field public List<Course> courses;
    
    @Data @NoArgsConstructor
    public static class Course {
    	@Field public String designation;
    	@Field public int qte;
    	@Field public String unite;
    }
    
}
