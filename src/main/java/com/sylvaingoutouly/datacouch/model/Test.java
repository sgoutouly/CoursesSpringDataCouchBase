package com.sylvaingoutouly.datacouch.model;

import java.util.Date;

import com.couchbase.client.java.repository.annotation.Field;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor // Lombok shortcuts
public class Test {

	@Field @JsonFormat(pattern = "dd/MM/yyyy") public Date dateRedaction;

}
