package com.imss.sivimss.promotores.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteComisionesPromotorDto {
	
	private Integer id_delegacion;
	private Integer id_velatorio;
	private String ods;
	private Integer id_promotor;
	private Integer anio;
	private Integer mes;
	private String nombreVelatorio;
	private String tipoReporte;
	
}
