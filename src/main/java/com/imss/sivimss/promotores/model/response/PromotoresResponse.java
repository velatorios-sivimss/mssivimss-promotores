package com.imss.sivimss.promotores.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotoresResponse {
	private Integer idPromotor;
	private String nombre;
}
