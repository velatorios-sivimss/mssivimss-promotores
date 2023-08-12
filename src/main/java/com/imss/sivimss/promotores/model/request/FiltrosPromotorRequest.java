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
public class FiltrosPromotorRequest {
	
	private Integer idVelatorio;
	private Integer idDelegacion;
	private String nomPromotor;
	private String tamanio;
	private String pagina;

}
