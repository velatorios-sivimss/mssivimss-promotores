package com.imss.sivimss.promotores.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@JsonIgnoreType(value = true)
public class RegistrarActividadesRequest {
	
	private Integer idActividad;
	private String hrInicio;
	private String hrFin;
	private Integer idPromotor;
	private String nomPromotor;
	private Integer numPlaticas;
	private String unidad;
	private String empresa;
	private String actividadRealizada;
	private String observaciones;
	private Integer evidencia;


}
