package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.promotores.model.DiasDescansoModel;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorRequest;
import com.imss.sivimss.promotores.model.request.PromotorRequest;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.QueryHelper;
import com.imss.sivimss.promotores.util.SelectQueryUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class GestionarPromotor {
	
	private Integer idPromotor;
	private String numEmpleado;
	private String desCurp;
	private String nomPromotor;
	private String aPaterno;
	private String aMaterno;
	private String fecNacimiento;
	private Integer idLugarNac;
	private String fecIngreso;
	private Integer monSueldoBase;
	private Integer idVelatorio;
	private String fecBaja;
	private String desCorreo;
	private String desPuesto;
	private String desCategoria;
	private Integer indEstatus;
	private Integer idUsuario;
	private Integer indEstatusDescanso;
	
	public GestionarPromotor(PromotorRequest promoRequest) {
		this.desCurp = promoRequest.getCurp();
		this.nomPromotor = promoRequest.getNomPromotor();
		this.aPaterno = promoRequest.getAPaterno();
		this.aMaterno = promoRequest.getAMaterno();
		this.desCorreo = promoRequest.getCorreo();
		this.fecNacimiento = promoRequest.getFecNac();
		this.fecIngreso = promoRequest.getFecIngreso();
		this.idPromotor = promoRequest.getIdPromotor();
		this.numEmpleado = promoRequest.getNumEmpleado();
		this.monSueldoBase = promoRequest.getSueldoBase();
		this.idVelatorio = promoRequest.getIdVelatorio();
		this.desCorreo = promoRequest.getCorreo();
		this.desPuesto = promoRequest.getPuesto();
		this.desCategoria = promoRequest.getCategoria();
		this.indEstatus = promoRequest.getEstatus();
		this.idLugarNac = promoRequest.getIdLugarNac();
	}

    //TABLA	
	public static final String SVT_PROMOTOR= "SVT_PROMOTOR PR";
	public static final String SVC_VELATORIO= "SVC_VELATORIO SV";
	public static final String SVC_ESTADO= "SVC_ESTADO SE";
	
	//PARAMETERS
	public static final String ID_TABLA= "idTabla";
	public static final String ID_PROMOTOR= "ID_PROMOTOR";
	public static final String SV_DES_VELATORIO= "SV.DES_VELATORIO";
	
	public DatosRequest catalogoPromotores(DatosRequest request, FiltrosPromotorRequest filtros, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PR.ID_PROMOTOR AS idPromotor",
				"PR.NUM_EMPLEDO AS numEmpleado",
				"PR.CVE_CURP AS curp",
				"PR.NOM_PROMOTOR AS nombre",
				"PR.NOM_PAPELLIDO AS primerApellido",
				"PR.NOM_SAPELLIDO AS segundoApellido",
				"PR.ID_ESTADO AS idLugarNac",
				"SE.DES_ESTADO AS lugarNac",
				"DATE_FORMAT(PR.FEC_NACIMIENTO, '"+fecFormat+"') AS fecNac",
				"DATE_FORMAT(PR.FEC_INGRESO, '"+fecFormat+"') AS fecIngreso",
				"DATE_FORMAT(PR.FEC_BAJA, '"+fecFormat+"') AS fecBaja",
				"PR.MON_SUELDOBASE AS sueldoBase",
				SV_DES_VELATORIO +" AS velatorio",
				"COUNT(DIA.FEC_PROMOTOR_DIAS_DESCANSO) AS diasDescanso",
				"GROUP_CONCAT(DATE_FORMAT(DIA.FEC_PROMOTOR_DIAS_DESCANSO, '"+fecFormat+"')) AS fecDescansos",
				"IF(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()) < 12, "
				+ "CONCAT(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' meses'), "
				+ "CONCAT(TIMESTAMPDIFF(YEAR, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' año (s)') )AS antiguedad",
				"PR.REF_CORREO AS correo",
				"PR.REF_PUESTO AS puesto",
				"PR.REF_CATEGORIA AS categoria",
				"PR.IND_ACTIVO AS estatus")
		.from(SVT_PROMOTOR)
		.join(SVC_VELATORIO, "PR.ID_VELATORIO = SV.ID_VELATORIO")
		.leftJoin("SVT_PROMOTOR_DIAS_DESCANSO DIA", "PR.ID_PROMOTOR = DIA.ID_PROMOTOR AND DIA.IND_ACTIVO = 1")
		.leftJoin(SVC_ESTADO, "PR.ID_ESTADO=SE.ID_ESTADO");
		if(filtros.getIdDelegacion()!=null) {
			queryUtil.where("SV.ID_DELEGACION = "+ filtros.getIdDelegacion());
		}
		if(filtros.getIdVelatorio()!=null){
			queryUtil.where("PR.ID_VELATORIO = " + filtros.getIdVelatorio());	
		}
		if(filtros.getNomPromotor()!=null){
			queryUtil.where("CONCAT(PR.NOM_PROMOTOR,' ', "
					+"PR.NOM_PAPELLIDO,' ', "
					+ "PR.NOM_SAPELLIDO) LIKE '%" + filtros.getNomPromotor() + "%'");	
		}
		queryUtil.groupBy("PR.ID_PROMOTOR");
		String query = obtieneQuery(queryUtil);
		log.info("promotores "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put("pagina",filtros.getPagina());
        parametros.put("tamanio",filtros.getTamanio());
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}
	
	
	public DatosRequest detalle(DatosRequest request, String palabra, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PR.ID_PROMOTOR AS idPromotor",
				"PR.NUM_EMPLEDO AS numEmpleado",
				"SV.ID_VELATORIO AS idVelatorio",
				"PR.CVE_CURP AS curp",
				"PR.NOM_PROMOTOR AS nombre",
				"PR.NOM_PAPELLIDO AS primerApellido",
				"PR.NOM_SAPELLIDO AS segundoApellido",
				"PR.ID_ESTADO AS idLugarNac",
				"SE.DES_ESTADO AS lugarNac",
				"DATE_FORMAT(PR.FEC_NACIMIENTO, '"+fecFormat+"') AS fecNac",
				"DATE_FORMAT(PR.FEC_INGRESO, '"+fecFormat+"') AS fecIngreso",
				"DATE_FORMAT(PR.FEC_BAJA, '"+fecFormat+"') AS fecBaja",
				"PR.MON_SUELDOBASE AS sueldoBase",
				"SV.DES_VELATORIO AS velatorio",
				"IF(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()) < 12, "
				+ "CONCAT(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' meses'), "
				+ "CONCAT(TIMESTAMPDIFF(YEAR, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' año (s)') )AS antiguedad",
				"PR.REF_CORREO AS correo",
				"PR.REF_PUESTO AS puesto",
				"PR.REF_CATEGORIA AS categoria",
				"PR.IND_ACTIVO AS estatus")
		.from(SVT_PROMOTOR)
		.join("SVC_VELATORIO SV ", "PR.ID_VELATORIO = SV.ID_VELATORIO")
		.leftJoin(SVC_ESTADO, "PR.ID_ESTADO=SE.ID_ESTADO");
		queryUtil.where("PR.ID_PROMOTOR = " +palabra);
		String query = obtieneQuery(queryUtil);
		log.info("promotores "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}
	
	
	public DatosRequest buscarDiasDescanso(DatosRequest request, String palabra, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DIA.ID_PROMOTOR_DIAS_DESCANSO AS id",
				"DATE_FORMAT(DIA.FEC_PROMOTOR_DIAS_DESCANSO, '"+fecFormat+"') AS fecDescanso",
				"DIA.IND_ACTIVO AS estatus")
		.from(SVT_PROMOTOR)
		.leftJoin("SVT_PROMOTOR_DIAS_DESCANSO DIA", "PR.ID_PROMOTOR = DIA.ID_PROMOTOR");
		queryUtil.where("PR.ID_PROMOTOR = "+palabra).and("DIA.IND_ACTIVO=1");
		String query = obtieneQuery(queryUtil);
		log.info("dias de descanso: "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}

	public DatosRequest insertarPromotor(PromotorRequest promoRequest) throws ParseException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_PROMOTOR");
		q.agregarParametroValues("CVE_CURP", "'" + this.desCurp + "'");
		q.agregarParametroValues("NOM_PROMOTOR", setValor(this.nomPromotor));
		q.agregarParametroValues("NOM_PAPELLIDO", setValor(this.aPaterno));
		q.agregarParametroValues("NOM_SAPELLIDO", setValor(this.aMaterno));
		q.agregarParametroValues("FEC_NACIMIENTO", "'" +fecNacimiento +"'");
		q.agregarParametroValues("ID_ESTADO", ""+this.idLugarNac+"");
		q.agregarParametroValues("REF_CORREO", setValor(this.desCorreo));
		q.agregarParametroValues("NUM_EMPLEDO", "'" +this.numEmpleado + "'");
		q.agregarParametroValues("FEC_INGRESO", "'" +fecIngreso +"'");
		q.agregarParametroValues("MON_SUELDOBASE", ""+ this.monSueldoBase +"");
		q.agregarParametroValues("ID_VELATORIO", this.idVelatorio.toString());
		q.agregarParametroValues("REF_PUESTO", "'" + this.desPuesto + "'");
		q.agregarParametroValues("REF_CATEGORIA", setValor(this.desCategoria));
		q.agregarParametroValues(AppConstantes.IND_ACTIVO, "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", idUsuario.toString());
		q.agregarParametroValues("FEC_ALTA", AppConstantes.CURRENT_TIMESTAMP);
		String query = q.obtenerQueryInsertar();
		if(promoRequest.getFecPromotorDiasDescanso()!=null) {
			StringBuilder queries= new StringBuilder();
			queries.append(query);
			//for(int i=0; i<this.fecPromotorDiasDescanso.size(); i++)
			for(DiasDescansoModel descansos: promoRequest.getFecPromotorDiasDescanso()) {
		        String fecha=formatFecha(descansos.getFecDescanso());
				queries.append("$$" + insertarDiasDescanso(fecha, this.idPromotor, descansos.getId(), descansos.getEstatus()));
			}
			log.info("estoy en fecDescansos: " +queries.toString());
				  String encoded = encodedQuery(queries.toString());
				  parametro.put(AppConstantes.QUERY, encoded);
			        parametro.put("separador","$$");
			        parametro.put("replace",ID_TABLA);
		}else {
			log.info("estoy en: " +query);
			String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
		}
		        
		        request.setDatos(parametro);
	
		return request;
	      
	}

	public String insertarDiasDescanso(String descansos, Integer idPromotor, Integer idDescanso, Integer estatus) {
		String query = "";
		QueryHelper q;
		if(idDescanso!=null) {
			 q = new QueryHelper("UPDATE SVT_PROMOTOR_DIAS_DESCANSO");
			 q.agregarParametroValues(AppConstantes.IND_ACTIVO, ""+estatus+"");
			 if(estatus==1) {
				 q.agregarParametroValues(AppConstantes.ID_USUARIO_MODIFICA, idUsuario.toString());
				 q.agregarParametroValues(AppConstantes.FEC_ACTUALIZACION, AppConstantes.CURRENT_TIMESTAMP);
			 }else {
				 q.agregarParametroValues(AppConstantes.ID_USUARIO_BAJA, idUsuario.toString());
				 q.agregarParametroValues(AppConstantes.FEC_BAJA, AppConstantes.CURRENT_TIMESTAMP);
			 }
		}else {
			 q = new QueryHelper("INSERT INTO SVT_PROMOTOR_DIAS_DESCANSO");
			 q.agregarParametroValues(AppConstantes.IND_ACTIVO, " 1 ");
			 q.agregarParametroValues("ID_USUARIO_ALTA", idUsuario.toString());
				q.agregarParametroValues("FEC_ALTA", AppConstantes.CURRENT_TIMESTAMP);
		}
		if(idPromotor!=null) {
			q.agregarParametroValues(ID_PROMOTOR, idPromotor.toString());
		}else {
			q.agregarParametroValues(ID_PROMOTOR, ID_TABLA);
		} 
		q.agregarParametroValues("FEC_PROMOTOR_DIAS_DESCANSO", "'" +descansos+ "'");
		if(idDescanso!=null) {
			q.addWhere("ID_PROMOTOR_DIAS_DESCANSO =" +idDescanso);
			query = q.obtenerQueryActualizar();
		}else {
			 query = q.obtenerQueryInsertar();
		}
		return query;
	}


	public DatosRequest buscarCurp(String curp) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("CVE_CURP")
		.from("SVT_PROMOTOR" );
		queryUtil.where("CVE_CURP = :curp")
		.setParameter("curp", curp);
		String query = obtieneQuery(queryUtil);
		log.info("valida " +query);
			String encoded=encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			request.getDatos().remove(AppConstantes.DATOS);
			return request;
	}

	public DatosRequest actualizarPromotor(PromotorRequest promoRequest) throws ParseException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PROMOTOR");
		q.agregarParametroValues("REF_CORREO", setValor(this.desCorreo));
		q.agregarParametroValues("FEC_INGRESO", setValor(fecIngreso));
		q.agregarParametroValues("MON_SUELDOBASE", ""+ this.monSueldoBase +"");
		q.agregarParametroValues("ID_VELATORIO", "" + this.idVelatorio + "");
		q.agregarParametroValues("REF_PUESTO", setValor(this.desPuesto));
		q.agregarParametroValues("REF_CATEGORIA", setValor(this.desCategoria));
		q.agregarParametroValues(AppConstantes.ID_USUARIO_MODIFICA, idUsuario.toString());
		q.agregarParametroValues(AppConstantes.FEC_ACTUALIZACION, AppConstantes.CURRENT_TIMESTAMP);
		q.addWhere(ID_PROMOTOR +"=" + this.idPromotor);
		String query = q.obtenerQueryActualizar();
		log.info(query);
		if(promoRequest.getFecPromotorDiasDescanso()!=null) {
				StringBuilder queries= new StringBuilder();
				queries.append(query);
				for(DiasDescansoModel descansos: promoRequest.getFecPromotorDiasDescanso()) {
			        String fecha=formatFecha(descansos.getFecDescanso());
					queries.append("$$" + insertarDiasDescanso(fecha, this.idPromotor, descansos.getId(), descansos.getEstatus()));
				}
				log.info("actualizar "+query);
					  String encoded = encodedQuery(queries.toString());
				        parametro.put(AppConstantes.QUERY, encoded);
				        parametro.put("separador","$$");
				        parametro.put("replace",ID_TABLA);
		}else {
			 String encoded = encodedQuery(query);
		        parametro.put(AppConstantes.QUERY, encoded);
		}
				        request.setDatos(parametro);
		return request;
	}

	public DatosRequest cambiarEstatus(PromotorRequest promotor) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PROMOTOR");
		if(promotor.getEstatus()==0) {
			q.agregarParametroValues(AppConstantes.FEC_BAJA, AppConstantes.CURRENT_TIMESTAMP);
			q.agregarParametroValues(AppConstantes.ID_USUARIO_BAJA, idUsuario.toString());
			q.agregarParametroValues(AppConstantes.IND_ACTIVO, "FALSE");
		}else {
			q.agregarParametroValues(AppConstantes.FEC_ACTUALIZACION, AppConstantes.CURRENT_TIMESTAMP);
			q.agregarParametroValues(AppConstantes.ID_USUARIO_MODIFICA,  idUsuario.toString());
			q.agregarParametroValues(AppConstantes.IND_ACTIVO, "TRUE");
		}
		q.addWhere(ID_PROMOTOR +"= " + promotor.getIdPromotor());
			String query = q.obtenerQueryActualizar();
			String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;	
	}
	
	public DatosRequest bajaDescansos(Integer idPromotor) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PROMOTOR_DIAS_DESCANSO");
			q.agregarParametroValues(AppConstantes.FEC_BAJA, AppConstantes.CURRENT_TIMESTAMP);
			q.agregarParametroValues(AppConstantes.ID_USUARIO_BAJA,  idUsuario.toString());
			q.agregarParametroValues(AppConstantes.IND_ACTIVO, "FALSE");
		    q.addWhere(ID_PROMOTOR +" =" + idPromotor);
			String query = q.obtenerQueryActualizar();
			String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;	
	}
	
	
	public DatosRequest buscarPromotorPorNombre(DatosRequest request, FiltrosPromotorRequest filtros ) {
		 Map<String, Object> parametro = new HashMap<>();
	        SelectQueryUtil queryUtil = new SelectQueryUtil();
	        queryUtil.select("PROM.ID_PROMOTOR AS idPromotor",
	        		"CONCAT(PROM.NOM_PROMOTOR,' ', " 
	        		+"PROM.NOM_PAPELLIDO, ' ', "
	                        +"PROM.NOM_SAPELLIDO) AS nomPromotor")
	                .from("SVT_PROMOTOR PROM")
	                .join("SVC_VELATORIO SV ", "PROM.ID_VELATORIO = SV.ID_VELATORIO");
	        queryUtil.where("CONCAT(PROM.NOM_PROMOTOR,' ', "
	        		+ "PROM.NOM_PAPELLIDO,' ', "
	        		+ "PROM.NOM_SAPELLIDO) LIKE" +"'%"+filtros.getNomPromotor() +"%'");
	    	if(filtros.getIdDelegacion()!=null) {
				queryUtil.where("SV.ID_DELEGACION = "+ filtros.getIdDelegacion());
			}
			if(filtros.getIdVelatorio()!=null){
				queryUtil.where("PROM.ID_VELATORIO = " + filtros.getIdVelatorio());	
			}
	        queryUtil.groupBy("PROM.NOM_PROMOTOR, PROM.NOM_PAPELLIDO , PROM.NOM_SAPELLIDO");
	        String query = obtieneQuery(queryUtil);
	        log.info(query);
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.getDatos().remove(AppConstantes.DATOS);
	        request.setDatos(parametro);
	        return request;
	}
	
	public DatosRequest catalogoVelatorios(DatosRequest request) {
		 Map<String, Object> parametro = new HashMap<>();
	        SelectQueryUtil queryUtil = new SelectQueryUtil();
	        queryUtil.select("SV.ID_VELATORIO AS idVelatorio",
	                        "SV.DES_VELATORIO AS velatorio")
	                .from(SVC_VELATORIO);
	        String query = obtieneQuery(queryUtil);
	        log.info(query);
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.getDatos().remove(AppConstantes.DATOS);
	        request.setDatos(parametro);
	        return request;
	}

	public DatosRequest catalogoDelegaciones(DatosRequest request) {
		 Map<String, Object> parametro = new HashMap<>();
	        SelectQueryUtil queryUtil = new SelectQueryUtil();
	        queryUtil.select("SD.ID_DELEGACION AS idDelegacion",
	                        "SD.DES_DELEGACION AS delegacion")
	                .from("SVC_DELEGACION SD");
	        String query = obtieneQuery(queryUtil);
	        log.info(query);
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.getDatos().remove(AppConstantes.DATOS);
	        request.setDatos(parametro);
	        return request;
	}
	

	public DatosRequest catalogoEstados(DatosRequest request) {
		Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil queryUtil = new SelectQueryUtil();
        queryUtil.select("SE.ID_ESTADO AS idEstado",
                        "SE.DES_ESTADO AS estado")
                .from(SVC_ESTADO);
        String query = obtieneQuery(queryUtil);
        log.info(query);
        String encoded = encodedQuery(query);
        parametro.put(AppConstantes.QUERY, encoded);
        request.getDatos().remove(AppConstantes.DATOS);
        request.setDatos(parametro);
        return request;
	}


	   public String formatFecha(String fecha) throws ParseException {
			Date dateF = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
			DateFormat fecForma = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "MX"));
			return fecForma.format(dateF);
	   }
	   
	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }
	
	private static String encodedQuery(String query) {
        return DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
    }
	
	private String setValor(String valor) {
        if (valor==null || valor.equals("")) {
            return "NULL";
        }else {
            return "'"+valor+"'";
        }
        
    }
}
