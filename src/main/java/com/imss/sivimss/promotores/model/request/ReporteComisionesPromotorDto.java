package com.imss.sivimss.promotores.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteComisionesPromotorDto {
	
	private Integer idDelegacion;
	private Integer idVelatorio;
	private String ods;
	private Integer idPromotor;
	private Integer anio;
	private Integer mes;
	private String nombreVelatorio;
	
}
