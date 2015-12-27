package com.sylvaingoutouly.datacouch.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor 
@Document	
public class Liste extends Entity {

	@Field public String titre;
	@Field @JsonFormat(pattern = "dd/MM/yyyy") public Date dateRedaction;
    @Field @JsonFormat(pattern = "dd/MM/yyyy") public Date dateCourse;
    @Field public List<Course> courses;
    
    @Data @NoArgsConstructor
    public static class Course {
    	@Field public String designation;
    	@Field public int qte;
    	@Field public String unite;
    }
    
}
