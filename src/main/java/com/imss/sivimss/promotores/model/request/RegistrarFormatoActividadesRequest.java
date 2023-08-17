package com.imss.sivimss.promotores.model.request;

import java.util.List;

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
public class RegistrarFormatoActividadesRequest {
	
	private Integer idFormato;
	private Integer idVelatorio;
	private String folio;
	private String fecElaboracion;
	private List<RegistrarActividadesRequest> actividades;
	
}
