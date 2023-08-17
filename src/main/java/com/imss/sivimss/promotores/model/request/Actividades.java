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
public class Actividades {
	
	private Integer idRegistro;
	private Integer idFormato;
	private Integer idVelatorio;
	private String fecElaboracion;
	private String hrInicio;
	private String hrFin;
	private Integer idPromotor;
	private String nomPromotor;
	private Integer numPlaticas;
	private String unidadImss;
	private String empresa;
	private String actividadRealizada;
	private String observaciones;
	private Integer evidencia;


}
