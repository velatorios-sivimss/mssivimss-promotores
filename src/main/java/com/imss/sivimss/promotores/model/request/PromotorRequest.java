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
public class PromotorRequest {

	private Integer idPromotor;
	private String curp;
	private String nomPromotor;
	private String aPaterno;
	private String aMaterno;
	private String fecNac;
	private String correo;
	private String numEmpleado;
	private String fecIngreso;
	private Integer sueldoBase;
	private Integer idVelatorio;
	private String puesto;
	private String categoria;
	private Integer estatus;
	private List<String> fecPromotorDiasDescanso;
	private Integer indEstatusDescanso;
	
}
