package com.imss.sivimss.promotores.model.response;

import java.util.List;

import com.imss.sivimss.promotores.model.DiasDescansoModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PromotorResponse {
	
	private Integer idPromotor;
	private String numEmpleado;
	private String velatorio;
	private String nombre;
	private String primerApellido;
	private String segundoApellido;
	private String categoria;
	private Integer idVelatorio;
	private String puesto;
	private Boolean estatus;
	private String fecBaja;
	private String correo;
	private String antiguedad;
	private String fecNac;
	private Integer idLugarNac;
	private String lugarNac;
	private String fecIngreso;
	private Integer sueldoBase;
	private String curp;
	private List<DiasDescansoModel> promotorDiasDescanso;
	

}
