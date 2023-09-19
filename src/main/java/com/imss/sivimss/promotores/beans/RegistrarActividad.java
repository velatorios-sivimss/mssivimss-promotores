package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.promotores.model.request.RegistrarActividadesRequest;
import com.imss.sivimss.promotores.model.request.RegistrarFormatoActividadesRequest;
import com.imss.sivimss.promotores.model.request.ReporteDto;
import com.imss.sivimss.promotores.service.impl.GestionarPromotorImpl;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorActividadesRequest;
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
public class RegistrarActividad {

	private Integer idFormato;
	private Integer idVelatorio;
	private String fecInicio;
	private String fecFin;
	private String fecActividad;
	//private RegistrarActividadesRequest actividades;
	private Integer idUsuario;
	
	public RegistrarActividad(RegistrarFormatoActividadesRequest actividadRequest) {
		this.idFormato = actividadRequest.getIdFormato();
		this.idVelatorio = actividadRequest.getIdVelatorio();
		this.fecInicio = actividadRequest.getFecInicio();
		this.fecFin = actividadRequest.getFecFin();
		//this.actividades = actividadRequest.getActividades();
	}
	
	//Tablas
	private static final String SVT_FORMATO_ACTIVIDAD_PROMOTORES = "SVT_FORMATO_ACTIVIDAD_PROM FORM";
	private static final String SVT_ACTIVIDAD_PROMOTORES = "SVT_ACTIVIDAD_PROMOTORES PROM";
	private static final String SVC_VELATORIO = "SVC_VELATORIO SV";
	
	//Parameters
	private static final String PAGINA = "pagina";
	private static final String TAMANIO = "tamanio";
	private static final String ESTATUS_FORMATO = "FORM.IND_ACTIVO=1";
	private static final String ESTATUS_REGISTRO = "PROM.IND_ACTIVO=1";
	
	public DatosRequest buscarFormatoActividades(DatosRequest request, FiltrosPromotorActividadesRequest filtros,
			String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("FORM.ID_FORMATO_ACTIVIDAD AS idFormatoRegistro",
				"DATE_FORMAT(FORM.FEC_ELABORACION, '"+fecFormat+"') AS fecElaboracion",
				"SV.DES_VELATORIO AS velatorio",
				"FORM.DES_FOLIO AS folio",
				"SUM(PROM.NUM_PLATICAS) AS numActividades",
				"IF(TIMESTAMPDIFF(DAY, FORM.FEC_ELABORACION, CURDATE())>7, FALSE, TRUE) AS banderaModificar")
		.from(SVT_FORMATO_ACTIVIDAD_PROMOTORES)
		.leftJoin(SVT_ACTIVIDAD_PROMOTORES, "FORM.ID_FORMATO_ACTIVIDAD=PROM.ID_FORMATO_ACTIVIDAD")
		.join(SVC_VELATORIO, "FORM.ID_VELATORIO=SV.ID_VELATORIO");
		queryUtil.where(ESTATUS_FORMATO);
		if(filtros.getIdDelegacion()!=null) {
			queryUtil.where("SV.ID_DELEGACION = "+ filtros.getIdDelegacion() + "");
		}
		if(filtros.getIdVelatorio()!=null){
			queryUtil.where("FORM.ID_VELATORIO = " + filtros.getIdVelatorio() + "");	
		}
		if(filtros.getFolio()!=null){
			queryUtil.where("FORM.DES_FOLIO = '" + filtros.getFolio()+ "'");	
		}
		if(filtros.getFecInicio()!=null) {
			queryUtil.where("FORM.FEC_ELABORACION BETWEEN '" + fecInicio+"'" ).and("'"+fecFin+"'");	
		}
		queryUtil.groupBy("FORM.ID_FORMATO_ACTIVIDAD ORDER BY FORM.FEC_ELABORACION ASC");
		String query = obtieneQuery(queryUtil);
		log.info("actividades promotores "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put(PAGINA,filtros.getPagina());
        parametros.put(TAMANIO,filtros.getTamanio());
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}

	
	
	public DatosRequest insertarFormatoActividades(RegistrarActividadesRequest actividades) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_FORMATO_ACTIVIDAD_PROM");
		q.agregarParametroValues("ID_VELATORIO", ""+this.getIdVelatorio()+"");
		q.agregarParametroValues("FEC_INICIO", "'"+fecInicio+"'");
		q.agregarParametroValues("FEC_FIN", "'"+fecFin+"'");
		q.agregarParametroValues("DES_FOLIO", "(SELECT CONCAT(LPAD(COUNT(FORM.ID_FORMATO_ACTIVIDAD)+1, 5,'0'),'-', "
				+ "(SELECT SV.ID_VELATORIO FROM SVC_VELATORIO SV WHERE ID_VELATORIO = "+this.idVelatorio+")) "
				+ "FROM SVT_FORMATO_ACTIVIDAD_PROM FORM "
				+ "JOIN SVC_VELATORIO SV ON FORM.ID_VELATORIO = SV.ID_VELATORIO WHERE FORM.IND_ACTIVO=1)");
		q.agregarParametroValues("FEC_ELABORACION", "" +AppConstantes.CURRENT_TIMESTAMP +"" );
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "0");
	    q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP +"");
		log.info("-> " +actividades.toString());
		String query = q.obtenerQueryInsertar(); //+ "$$" + insertarActividades(actividades);
			log.info("estoy en resgistro actividades " +query);
			String encoded = encodedQuery(query);
				  parametro.put(AppConstantes.QUERY, encoded);
		        request.setDatos(parametro);
		return request;
	}
	
	
	public DatosRequest actualizarActividad(RegistrarActividadesRequest actividad) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("FEC_ACTIVIDAD", "'"+fecActividad+"'");
		q.agregarParametroValues("TIM_HORA_INICIO", setValor(actividad.getHrInicio()));
		q.agregarParametroValues("TIM_HORA_FIN", setValor(actividad.getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividad.getIdPromotor() + "");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividad.getNumPlaticas()+"");
		q.agregarParametroValues("REF_UNIDAD_IMSS", setValor(actividad.getUnidad()));
		q.agregarParametroValues("REF_EMPRESA", setValor(actividad.getEmpresa()));
		q.agregarParametroValues("REF_ACTIVIDAD_REALIZADA", setValor(actividad.getActividadRealizada()));
		q.agregarParametroValues("REF_OBSERVACIONES", setValor(actividad.getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividad.getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ACTUALIZACION", "" +AppConstantes.CURRENT_TIMESTAMP + "");
			q.addWhere("ID_REGISTRO_ACTIVIDAD = " +actividad.getIdActividad());
		String query = q.obtenerQueryActualizar();
		log.info("actualizar: " +query);
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return request;
	}
	
	public DatosRequest insertarActividad(RegistrarActividadesRequest actividades, Integer idFormato, Integer idFormatoResponse) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query="";
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("FEC_ACTIVIDAD", "'"+fecActividad+"'");
		q.agregarParametroValues("TIM_HORA_INICIO", setValor(actividades.getHrInicio()));
		q.agregarParametroValues("TIM_HORA_FIN", setValor(actividades.getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividades.getIdPromotor() + "");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividades.getNumPlaticas()+"");
		q.agregarParametroValues("REF_UNIDAD_IMSS", setValor(actividades.getUnidad()));
		q.agregarParametroValues("REF_EMPRESA", setValor(actividades.getEmpresa()));
		q.agregarParametroValues("REF_ACTIVIDAD_REALIZADA", setValor(actividades.getActividadRealizada()));
		q.agregarParametroValues("REF_OBSERVACIONES", setValor(actividades.getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividades.getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		if(idFormato!=null) {
			q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idFormato+"");	
			query = q.obtenerQueryInsertar();	
			log.info("insertar: "+query);
			 String encoded = encodedQuery(query);
			  parametro.put(AppConstantes.QUERY, encoded);
		}else {
			q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idFormatoResponse+"");		
			query = q.obtenerQueryInsertar() +"$$" +actualizarPadre(idFormatoResponse);	
			log.info("insertarMultiple: "+query);
			 String encoded = encodedQuery(query);
			  parametro.put(AppConstantes.QUERY, encoded);
			  parametro.put("separador","$$");
		      parametro.put("replace","idTabla");
		}
        request.setDatos(parametro);
        return request;
	}
	
	
	private String actualizarPadre(Integer idFormatoResponse) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_FORMATO_ACTIVIDAD_PROM");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.addWhere("ID_FORMATO_ACTIVIDAD = " +idFormatoResponse);
		String query = q.obtenerQueryActualizar();
		log.info("ActualizarPadre"+query);
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return query;
	}



	public DatosRequest datosFormato(DatosRequest request, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		String palabra = request.getDatos().get("palabra").toString();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("FORM.ID_FORMATO_ACTIVIDAD AS idFormato",
				"DATE_FORMAT(FORM.FEC_ELABORACION, '"+fecFormat+"') AS fecElaboracion",
				"SV.DES_VELATORIO AS Velatorio",
				"FORM.DES_FOLIO AS folio",
				"FORM.FEC_INICIO AS fecInicio",
				"FORM.FEC_FIN AS fecFin",
				"IFNULL(SUM(PROM.NUM_PLATICAS), 0) AS numActividades")
		.from(SVT_FORMATO_ACTIVIDAD_PROMOTORES)
		.join(SVT_ACTIVIDAD_PROMOTORES, "FORM.ID_FORMATO_ACTIVIDAD = PROM.ID_FORMATO_ACTIVIDAD")
		.join(SVC_VELATORIO, "FORM.ID_VELATORIO = SV.ID_VELATORIO");
		queryUtil.where(ESTATUS_REGISTRO).and(ESTATUS_FORMATO).and
		("FORM.ID_FORMATO_ACTIVIDAD = " +Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("formato "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}


	public DatosRequest verDetalleActividades(DatosRequest request, Integer idFormato, Integer pagina, Integer tamanio, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PROM.ID_REGISTRO_ACTIVIDAD AS idActividad",
				"DATE_FORMAT(PROM.FEC_ACTIVIDAD, '"+fecFormat+"') AS fecActividad",
				"PROM.ID_FORMATO_ACTIVIDAD AS idFormato",
				 "PROM.TIM_HORA_INICIO AS hrInicio",
				 "PROM.TIM_HORA_FIN AS hrFin",
				 "SP.ID_PROMOTOR AS idPromotor",
				 "CONCAT(SP.NOM_PROMOTOR, ' ',"
				+ "SP.NOM_PAPELLIDO,' ', SP.NOM_SAPELLIDO) AS nomPromotor",
				 "SP.DES_PUESTO AS puesto",
				 "PROM.NUM_PLATICAS AS numPlaticas",
				 "PROM.REF_UNIDAD_IMSS AS unidad",
				 "PROM.REF_EMPRESA AS empresa",
				 "PROM.REF_ACTIVIDAD_REALIZADA AS actividadRealizada",
				 "PROM.REF_OBSERVACIONES AS observaciones",
				 "PROM.IND_EVIDENCIA AS evidencia")
		.from(SVT_ACTIVIDAD_PROMOTORES)
		.join(SVT_FORMATO_ACTIVIDAD_PROMOTORES, "PROM.ID_FORMATO_ACTIVIDAD = FORM.ID_FORMATO_ACTIVIDAD")
		.join(SVC_VELATORIO, "FORM.ID_VELATORIO = SV.ID_VELATORIO")
		.join("SVT_PROMOTOR SP", "PROM.ID_PROMOTOR = SP.ID_PROMOTOR");
		queryUtil.where(ESTATUS_REGISTRO).and(ESTATUS_FORMATO);
		queryUtil.where("PROM.ID_FORMATO_ACTIVIDAD = " +idFormato);
		String query = obtieneQuery(queryUtil);
		log.info("formato "+query); 
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put(PAGINA, pagina);
        parametros.put(TAMANIO,tamanio);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}
	
	public DatosRequest buscarFormato(Integer idActividad) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("FORM.FEC_ELABORACION")
		.from(SVT_FORMATO_ACTIVIDAD_PROMOTORES)
		.join(SVT_ACTIVIDAD_PROMOTORES, "FORM.ID_FORMATO_ACTIVIDAD = PROM.ID_FORMATO_ACTIVIDAD");
			queryUtil.where("TIMESTAMPDIFF(DAY, FORM.FEC_ELABORACION, CURDATE())>7")
			.and("PROM.ID_REGISTRO_ACTIVIDAD  = " + idActividad);	
		String query = obtieneQuery(queryUtil);
		log.info("validacion "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
		return request;
	}



	public DatosRequest eliminarActividad(Integer idActividad, Integer idUsuario) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_ACTIVIDAD_PROMOTORES");	
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "!IND_ACTIVO");
		q.agregarParametroValues("ID_USUARIO_BAJA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_BAJA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
			q.addWhere("ID_REGISTRO_ACTIVIDAD = " +idActividad);
		String query = q.obtenerQueryActualizar();
		log.info("ELIMINAR: " +query);
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return request;
	}



	public DatosRequest catalogoPromotores(DatosRequest request, Integer idVelatorio) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SP.ID_PROMOTOR AS idPromotor",
				"CONCAT(SP.NOM_PROMOTOR, ' ',"
				+ "SP.NOM_PAPELLIDO, ' ', SP.NOM_SAPELLIDO) AS nomPromotor",
				"SP.DES_PUESTO AS puesto")
		.from("SVT_PROMOTOR SP");
			queryUtil.where("SP.IND_ACTIVO=1");
			if(idVelatorio!=null) {
				queryUtil.where("SP.ID_VELATORIO ="+idVelatorio);
			}
		String query = obtieneQuery(queryUtil);
		log.info("catalogo "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
		return request;
	}
	
	public Map<String, Object> reporteActividades(ReporteDto reporte, String reporteActiv) throws ParseException {
		GestionarPromotorImpl prom = new GestionarPromotorImpl();
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condition= new StringBuilder();
		if(reporte.getIdDelegacion()!=null) {
			condition.append(" AND SV.ID_DELEGACION= "+reporte.getIdDelegacion()+"");
		}
	    if(reporte.getIdVelatorio()!=null) {
			condition.append(" AND SV.ID_VELATORIO = "+reporte.getIdVelatorio()+"");
		}
	    if(reporte.getFolio()!=null) {
			condition.append(" AND FORM.DES_FOLIO = '"+reporte.getFolio()+"'");
		}
	    if (reporte.getFecInicio()!=null) {
	    	String fecConsultaInicio = prom.formatFecha(reporte.getFecInicio());
    		String fecConsultaFin = prom.formatFecha(reporte.getFecFin());
			condition.append(" AND FORM.FEC_ELABORACION BETWEEN '" + fecConsultaInicio+"' AND '"+fecConsultaFin+"'");
		} 
	    log.info("->" +condition.toString());
		envioDatos.put("condition", condition.toString());		
		envioDatos.put("tipoReporte", reporte.getTipoReporte());
		envioDatos.put("rutaNombreReporte", reporteActiv);
		if(reporte.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		return envioDatos;
	}
	
	public Map<String, Object> formatoActividades(ReporteDto reporte, String anexo) {
		Map<String, Object> envioDatos = new HashMap<>();
		envioDatos.put("idFormato", reporte.getIdFormato());
		envioDatos.put("idVelatorio", reporte.getIdVelatorio());
		envioDatos.put("idRol", reporte.getIdRol());
		envioDatos.put("rutaNombreReporte", anexo);
		envioDatos.put("tipoReporte", "pdf");
		return envioDatos;
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

	
	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
	}

}
