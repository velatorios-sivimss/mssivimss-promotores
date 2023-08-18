package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.promotores.exception.BadRequestException;
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
	private String fecIngreso;
	private Integer monSueldoBase;
	private Integer idVelatorio;
	private String fecBaja;
	private String desCorreo;
	private String desPuesto;
	private String desCategoria;
	private Integer indEstatus;
	private Integer idUsuario;
    private List<String> fecPromotorDiasDescanso;
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
		this.fecPromotorDiasDescanso = promoRequest.getFecPromotorDiasDescanso(); 
	}

    //TABLA	
	public static final String SVT_PROMOTOR= "SVT_PROMOTOR PR";
	
	//PARAMETERS
	public static final String ID_TABLA= "idTabla";
	public static final String ID_PROMOTOR= "ID_PROMOTOR";
	
	public DatosRequest catalogoPromotores(DatosRequest request, FiltrosPromotorRequest filtros, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PR.ID_PROMOTOR AS idPromotor",
				"PR.NUM_EMPLEDO AS numEmpleado",
				"PR.DES_CURP AS curp",
				"PR.NOM_PROMOTOR AS nombre",
				"PR.NOM_PAPELLIDO AS primerApellido",
				"PR.NOM_SAPELLIDO AS segundoApellido",
				"DATE_FORMAT(PR.FEC_NACIMIENTO, '"+fecFormat+"') AS fecNac",
				"DATE_FORMAT(PR.FEC_INGRESO, '"+fecFormat+"') AS fecIngreso",
				"DATE_FORMAT(PR.FEC_BAJA, '"+fecFormat+"') AS fecBaja",
				"PR.MON_SUELDOBASE AS sueldoBase",
				"SV.DES_VELATORIO AS velatorio",
				"COUNT(DIA.FEC_PROMOTOR_DIAS_DESCANSO) AS diasDescanso",
				"GROUP_CONCAT(DATE_FORMAT(DIA.FEC_PROMOTOR_DIAS_DESCANSO, '"+fecFormat+"')) AS fecDescansos",
				"IF(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()) < 12, "
				+ "CONCAT(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' meses'), "
				+ "CONCAT(TIMESTAMPDIFF(YEAR, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' año (s)') )AS antiguedad",
				"PR.DES_CORREO AS correo",
				"PR.DES_PUESTO AS puesto",
				"PR.DES_CATEGORIA AS categoria",
				"PR.IND_ACTIVO AS estatus")
		.from(SVT_PROMOTOR)
		.join("SVC_VELATORIO SV ", "PR.ID_VELATORIO = SV.ID_VELATORIO")
		.leftJoin("SVT_PROMOTOR_DIAS_DESCANSO DIA", "PR.ID_PROMOTOR = DIA.ID_PROMOTOR AND DIA.IND_ACTIVO = 1");
		if(filtros.getIdDelegacion()!=null) {
			queryUtil.where("SV.ID_DELEGACION = "+ filtros.getIdDelegacion() + "");
		}
		if(filtros.getIdVelatorio()!=null){
			queryUtil.where("PR.ID_VELATORIO = " + filtros.getIdVelatorio() + "");	
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
				"PR.DES_CURP AS curp",
				"PR.NOM_PROMOTOR AS nombre",
				"PR.NOM_PAPELLIDO AS primerApellido",
				"PR.NOM_SAPELLIDO AS segundoApellido",
				"DATE_FORMAT(PR.FEC_NACIMIENTO, '"+fecFormat+"') AS fecNac",
				"DATE_FORMAT(PR.FEC_INGRESO, '"+fecFormat+"') AS fecIngreso",
				"DATE_FORMAT(PR.FEC_BAJA, '"+fecFormat+"') AS fecBaja",
				"PR.MON_SUELDOBASE AS sueldoBase",
				"SV.DES_VELATORIO AS velatorio",
				"IF(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()) < 12, "
				+ "CONCAT(TIMESTAMPDIFF(MONTH, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' meses'), "
				+ "CONCAT(TIMESTAMPDIFF(YEAR, PR.FEC_INGRESO, CURRENT_TIMESTAMP()), ' año (s)') )AS antiguedad",
				"PR.DES_CORREO AS correo",
				"PR.DES_PUESTO AS puesto",
				"PR.DES_CATEGORIA AS categoria",
				"PR.IND_ACTIVO AS estatus")
		.from(SVT_PROMOTOR)
		.join("SVC_VELATORIO SV ", "PR.ID_VELATORIO = SV.ID_VELATORIO");
		queryUtil.where("PR.ID_PROMOTOR = :id")
		.setParameter("id", Integer.parseInt(palabra));
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
				"DATE_FORMAT(DIA.FEC_PROMOTOR_DIAS_DESCANSO, '"+fecFormat+"') AS fecDescanso")
		.from(SVT_PROMOTOR)
		.leftJoin("SVT_PROMOTOR_DIAS_DESCANSO DIA", "PR.ID_PROMOTOR = DIA.ID_PROMOTOR");
		queryUtil.where("PR.ID_PROMOTOR = :id").and("DIA.IND_ACTIVO=1")
		.setParameter("id", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("dias de descanso: "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}

	public DatosRequest insertarPromotor() throws ParseException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_PROMOTOR");
		q.agregarParametroValues("DES_CURP", "'" + this.desCurp + "'");
		q.agregarParametroValues("NOM_PROMOTOR", setValor(this.nomPromotor));
		q.agregarParametroValues("NOM_PAPELLIDO", setValor(this.aPaterno));
		q.agregarParametroValues("NOM_SAPELLIDO", setValor(this.aMaterno));
		q.agregarParametroValues("FEC_NACIMIENTO", "'" +fecNacimiento +"'");
		q.agregarParametroValues("DES_CORREO", setValor(this.desCorreo));
		q.agregarParametroValues("NUM_EMPLEDO", "'" +this.numEmpleado + "'");
		q.agregarParametroValues("FEC_INGRESO", "'" +fecIngreso +"'");
		q.agregarParametroValues("MON_SUELDOBASE", ""+ this.monSueldoBase +"");
		q.agregarParametroValues("ID_VELATORIO", "" + this.idVelatorio + "");
		q.agregarParametroValues("DES_PUESTO", "'" + this.desPuesto + "'");
		q.agregarParametroValues("DES_CATEGORIA", setValor(this.desCategoria));
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
		if(this.fecPromotorDiasDescanso!=null) {
			StringBuilder queries= new StringBuilder();
			queries.append(query);
			//for(int i=0; i<this.fecPromotorDiasDescanso.size(); i++) {
			for(String descansos: this.fecPromotorDiasDescanso) {
		        String fecha=formatFecha(descansos);
				queries.append("$$" + insertarDiasDescanso(fecha, this.idPromotor));
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

	public String insertarDiasDescanso(String descansos, Integer id) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_PROMOTOR_DIAS_DESCANSO");
		if(id!=null) {
			q.agregarParametroValues(ID_PROMOTOR, ""+id+"");
		}else {
			q.agregarParametroValues(ID_PROMOTOR, ID_TABLA);
		}
		log.info(descansos);
		q.agregarParametroValues("FEC_PROMOTOR_DIAS_DESCANSO", "'" +descansos+ "'");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", " 1 ");
		String query = q.obtenerQueryInsertar();
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		log.info(query);
		return query;
	}


	public DatosRequest buscarCurp(String curp) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DES_CURP")
		.from("SVT_PROMOTOR" );
		queryUtil.where("DES_CURP = :curp")
		.setParameter("curp", curp);
		String query = obtieneQuery(queryUtil);
		log.info("valida " +query);
			String encoded=encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			request.getDatos().remove(""+AppConstantes.DATOS+"");
			return request;
	}

	public DatosRequest actualizarPromotor() throws ParseException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_PROMOTOR");
		log.info(fecIngreso);
		q.agregarParametroValues("DES_CORREO", setValor(this.desCorreo));
		q.agregarParametroValues("FEC_INGRESO", setValor(fecIngreso));
		q.agregarParametroValues("MON_SUELDOBASE", ""+ this.monSueldoBase +"");
		q.agregarParametroValues("ID_VELATORIO", "" + this.idVelatorio + "");
		q.agregarParametroValues("DES_PUESTO", setValor(this.desPuesto));
		q.agregarParametroValues("DES_CATEGORIA", setValor(this.desCategoria));
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ACTUALIZACION", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		if(this.indEstatus==0) {
			q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "FALSE");
			q.agregarParametroValues("FEC_BAJA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
			q.agregarParametroValues("ID_USUARIO_BAJA", "" + idUsuario + "");
		}else {
			q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "TRUE");
		}
		q.addWhere("ID_PROMOTOR = " + this.idPromotor);
		String query = q.obtenerQueryActualizar();
		log.info(query);
		if(this.fecPromotorDiasDescanso!=null) {
				StringBuilder queries= new StringBuilder();
				queries.append(query);
				for(String descansos: this.fecPromotorDiasDescanso) {
			        String fecha=formatFecha(descansos);
					queries.append("$$" + insertarDiasDescanso(fecha, this.idPromotor));
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
			q.agregarParametroValues("FEC_BAJA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
			q.agregarParametroValues("ID_USUARIO_BAJA",  "" + idUsuario + "");
			q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "FALSE");
		}else {
			q.agregarParametroValues("FEC_ACTUALIZACION", "" +AppConstantes.CURRENT_TIMESTAMP + "");
			q.agregarParametroValues("ID_USUARIO_MODIFICA",  "" + idUsuario + "");
			q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "TRUE");
		}
		q.addWhere("ID_PROMOTOR = " + promotor.getIdPromotor());
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
				queryUtil.where("SV.ID_DELEGACION = "+ filtros.getIdDelegacion() + "");
			}
			if(filtros.getIdVelatorio()!=null){
				queryUtil.where("PROM.ID_VELATORIO = " + filtros.getIdVelatorio() + "");	
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
	

	public DatosRequest catalogoVelatorios(DatosRequest request, FiltrosPromotorRequest filtros) {
		 Map<String, Object> parametro = new HashMap<>();
	        SelectQueryUtil queryUtil = new SelectQueryUtil();
	        queryUtil.select("SV.ID_VELATORIO AS idVelatorio",
	                        "SV.DES_VELATORIO AS velatorio")
	                .from("SVC_VELATORIO SV");
	        String query = obtieneQuery(queryUtil);
	        log.info(query);
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.getDatos().remove(AppConstantes.DATOS);
	        request.setDatos(parametro);
	        return request;
	}
	
	public DatosRequest catalogoDelegaciones(DatosRequest request, FiltrosPromotorRequest filtros) {
		 Map<String, Object> parametro = new HashMap<>();
	        SelectQueryUtil queryUtil = new SelectQueryUtil();
	        queryUtil.select("SD.ID_DELEGACION AS iddelegacion",
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
