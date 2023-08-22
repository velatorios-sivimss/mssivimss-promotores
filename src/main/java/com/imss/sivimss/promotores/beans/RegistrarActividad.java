package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.promotores.model.request.RegistrarActividadesRequest;
import com.imss.sivimss.promotores.model.request.RegistrarFormatoActividadesRequest;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorActividadesRequest;
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
public class RegistrarActividad {

	private Integer idFormato;
	private Integer idVelatorio;
	private String folio;
	private String fecElaboracion;
	private List<RegistrarActividadesRequest> actividades;
	private Integer idUsuario;
	
	public RegistrarActividad(RegistrarFormatoActividadesRequest actividadRequest) {
		this.idFormato = actividadRequest.getIdFormato();
		this.idVelatorio = actividadRequest.getIdVelatorio();
		this.folio = actividadRequest.getFolio();
		this.fecElaboracion = actividadRequest.getFecElaboracion();
		this.actividades = actividadRequest.getActividades();
	}

	
	/*public GestionarPromotor(PromotorRequest promoRequest) {
		this.desCurp = promoRequest.getCurp();
		this.nomPromotor = promoRequest.getNomPromotor();
		this.aPaterno = promoRequest.getAPaterno();
		this.aMaterno = promoRequest.getAMaterno();
		this.desCorreo = promoRequest.getCorreo();
		this.fecNacimiento = promoRequest.getFecNac();
		this.fecIngreso = promoRequest.getFecIngreso();
	/*public DatosRequest insertarActividades(ActividadesRequest actividadesRequest) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", "0");
		q.agregarParametroValues("ID_VELATORIO", ""+actividadesRequest.getActividades().get(0).getIdVelatorio()+"");
		q.agregarParametroValues("DES_FOLIO", "'prueba'");
		q.agregarParametroValues("FEC_ELABORACION", setValor(actividadesRequest.getActividades().get(0).getFecElaboracion()));
		q.agregarParametroValues("HORA_INICIO", setValor(actividadesRequest.getActividades().get(0).getHrInicio()));
		q.agregarParametroValues("HORA_FIN", setValor(actividadesRequest.getActividades().get(0).getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividadesRequest.getActividades().get(0).getIdPromotor() + "");
		//q.agregarParametroValues("NOM_OTRO_PROMOTOR", "'" +fecIngreso +"");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividadesRequest.getActividades().get(0).getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(actividadesRequest.getActividades().get(0).getUnidadImss()));
		q.agregarParametroValues("DES_EMPRESA", setValor(actividadesRequest.getActividades().get(0).getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(actividadesRequest.getActividades().get(0).getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(actividadesRequest.getActividades().get(0).getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividadesRequest.getActividades().get(0).getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		//q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
			StringBuilder queries= new StringBuilder();
			queries.append(query);
			for(int i=1; i<actividadesRequest.getActividades().size(); i++) {
			//for(String descansos: this.fecPromotorDiasDescanso) {
		        Actividades actividades = actividadesRequest.getActividades().get(i);
				queries.append("$$" + insertarMasActividades(actividades));
			}
			log.info("estoy en resgistro actividades " +queries.toString());
			queries.append("$$"+actualizarPadre());	 
			String encoded = encodedQuery(queries.toString());
				  parametro.put(AppConstantes.QUERY, encoded);
			        parametro.put("separador","$$");
			        parametro.put("replace","idTabla");
		        request.setDatos(parametro);
	
		return request;
	
	} */
	
	public DatosRequest buscarFormatoActividades(DatosRequest request, FiltrosPromotorActividadesRequest filtros,
			String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("FORM.ID_FORMATO_ACTIVIDAD AS idFormatoRegistro",
				"DATE_FORMAT(FORM.FEC_ELABORACION, '"+fecFormat+"') AS fecElaboracion",
				"CONCAT(FORM.ID_VELATORIO, ' ', SV.DES_VELATORIO) AS Velatorio",
				"FORM.DES_FOLIO AS folio",
				"SUM(PROM.NUM_PLATICAS) AS numActividades")
		.from("SVT_FORMATO_ACTIVIDAD_PROMOTORES FORM")
		.join("SVT_ACTIVIDAD_PROMOTORES PROM", "FORM.ID_FORMATO_ACTIVIDAD = PROM.ID_FORMATO_ACTIVIDAD")
		.join("SVC_VELATORIO SV", "FORM.ID_VELATORIO = SV.ID_VELATORIO");
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
			queryUtil.where("FORM.FEC_ELABORACION BETWEEN '" + filtros.getFecInicio()+"'" ).and("'"+filtros.getFecFin()+"'");	
		}
		queryUtil.groupBy("FORM.ID_FORMATO_ACTIVIDAD ORDER BY FORM.FEC_ELABORACION ASC");
		String query = obtieneQuery(queryUtil);
		log.info("actividades promotores "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put("pagina",filtros.getPagina());
        parametros.put("tamanio",filtros.getTamanio());
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}

	
	
	public DatosRequest insertarFormatoActividades() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_FORMATO_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_VELATORIO", ""+this.getIdVelatorio()+"");
		q.agregarParametroValues("DES_FOLIO", "'"+this.folio+"'");
		q.agregarParametroValues("FEC_ELABORACION", "" +AppConstantes.CURRENT_TIMESTAMP +"" );
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
	    q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP +"");
		String query = q.obtenerQueryInsertar();
		StringBuilder queries= new StringBuilder();
		queries.append(query);
			//	for(int i=0; i<actividadesRequest.getActividades().size(); i++) {
					for(RegistrarActividadesRequest actividades: this.getActividades()) {
				    //    Actividades actividades = actividadesRequest.getActividades().get(i);
						queries.append("$$" + insertarActividades(actividades));
			}
			log.info("estoy en resgistro actividades " +queries.toString());
		//	queries.append("$$"+actualizarPadre(idPadre));	 
			String encoded = encodedQuery(queries.toString());
				  parametro.put(AppConstantes.QUERY, encoded);
			        parametro.put("separador","$$");
			        parametro.put("replace","idTabla");
		        request.setDatos(parametro);
		return request;
	}
	
	
	public DatosRequest actualizarRegistroActividades() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query="";
		StringBuilder queries= new StringBuilder();
		QueryHelper q;
		for(RegistrarActividadesRequest fActividad: this.getActividades()) {
		if(fActividad.getIdActividad()!=null){
			q = new QueryHelper("UPDATE SVT_ACTIVIDAD_PROMOTORES");
		}else {
			q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		}
		log.info("--> "+this.idFormato );
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+this.idFormato+"");
		q.agregarParametroValues("TIM_HORA_INICIO", setValor(fActividad.getHrInicio()));
		q.agregarParametroValues("TIM_HORA_FIN", setValor(fActividad.getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +fActividad.getIdPromotor() + "");
		q.agregarParametroValues("NOM_PROMOTOR", setValor(fActividad.getNomPromotor()));
		q.agregarParametroValues("NUM_PLATICAS", ""+ fActividad.getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(fActividad.getUnidad()));
		q.agregarParametroValues("DES_EMPRESA", setValor(fActividad.getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(fActividad.getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(fActividad.getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+fActividad.getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		if(fActividad.getIdActividad()!=null) {
			q.addWhere("ID_REGISTRO_ACTIVIDAD = " +fActividad.getIdActividad());
			query = q.obtenerQueryActualizar();
		}else {
		    query = q.obtenerQueryInsertar();	
		}
		queries.append(query+"$$");
		}
		log.info("actualizar: " +queries.toString());
		  String encoded = encodedQuery(queries.toString());
		  parametro.put(AppConstantes.QUERY, encoded);
		   parametro.put("separador","$$");
        request.setDatos(parametro);
        return request;
	}
	
	
	public DatosRequest insertarActividadesOld(RegistrarFormatoActividadesRequest actividadesRequest) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", "0");
		q.agregarParametroValues("ID_VELATORIO", ""+actividadesRequest.getIdVelatorio()+"");
		q.agregarParametroValues("DES_FOLIO", "'prueba'");
		q.agregarParametroValues("FEC_ELABORACION", setValor(actividadesRequest.getFecElaboracion()));
		q.agregarParametroValues("HORA_INICIO", setValor(actividadesRequest.getActividades().get(0).getHrInicio()));
		q.agregarParametroValues("HORA_FIN", setValor(actividadesRequest.getActividades().get(0).getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividadesRequest.getActividades().get(0).getIdPromotor() + "");
		//q.agregarParametroValues("NOM_OTRO_PROMOTOR", "'" +fecIngreso +"");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividadesRequest.getActividades().get(0).getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(actividadesRequest.getActividades().get(0).getUnidad()));
		q.agregarParametroValues("DES_EMPRESA", setValor(actividadesRequest.getActividades().get(0).getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(actividadesRequest.getActividades().get(0).getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(actividadesRequest.getActividades().get(0).getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividadesRequest.getActividades().get(0).getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "0");
		//q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
			log.info("estoy en registro padre " +query); 
			String encoded = encodedQuery(query);
				  parametro.put(AppConstantes.QUERY, encoded);
		        request.setDatos(parametro);
		return request;
	}
	
	private String actualizarPadre(Integer idPadre) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idPadre+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.addWhere("ID_REGISTRO_ACTIVIDAD = " +idPadre);
		String query = q.obtenerQueryActualizar();
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return query;
	}


	public String insertarActividades(RegistrarActividadesRequest actividades) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", "idTabla");
		q.agregarParametroValues("TIM_HORA_INICIO", setValor(actividades.getHrInicio()));
		q.agregarParametroValues("TIM_HORA_FIN", setValor(actividades.getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividades.getIdPromotor() + "");
		q.agregarParametroValues("NOM_PROMOTOR", setValor(actividades.getNomPromotor()));
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividades.getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(actividades.getUnidad()));
		q.agregarParametroValues("DES_EMPRESA", setValor(actividades.getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(actividades.getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(actividades.getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividades.getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return query;
	}  
	
	
	public DatosRequest datosFormato(DatosRequest request, Integer idFormato, String fecFormat, Integer pagina, Integer tamanio) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("FORM.ID_FORMATO_ACTIVIDAD AS idFormato",
				"DATE_FORMAT(FORM.FEC_ELABORACION, '"+fecFormat+"') AS fecElaboracion",
				"CONCAT(FORM.ID_VELATORIO, ' ', SV.DES_VELATORIO) AS Velatorio",
				"FORM.DES_FOLIO AS folio",
				"SUM(PROM.NUM_PLATICAS) AS numActividades")
		.from("SVT_FORMATO_ACTIVIDAD_PROMOTORES FORM")
		.join("SVT_ACTIVIDAD_PROMOTORES PROM", "FORM.ID_FORMATO_ACTIVIDAD = PROM.ID_FORMATO_ACTIVIDAD")
		.join("SVC_VELATORIO SV", "FORM.ID_VELATORIO = SV.ID_VELATORIO");
		queryUtil.where("FORM.ID_FORMATO_ACTIVIDAD = " +idFormato);
		String query = obtieneQuery(queryUtil);
		log.info("formato "+query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put("pagina", pagina);
        parametros.put("tamanio",tamanio);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}


	public DatosRequest verDetalleActividades(DatosRequest request, Integer idFormato, Integer pagina, Integer tamanio) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PROM.ID_REGISTRO_ACTIVIDAD AS idActividad",
				"PROM.ID_FORMATO_ACTIVIDAD AS idFormato",
				 "PROM.TIM_HORA_INICIO AS hrInicio",
				 "PROM.TIM_HORA_FIN AS hrFin",
				 "SP.ID_PROMOTOR AS idPromotor",
				 "IFNULL(CONCAT(SP.NOM_PROMOTOR, ' ',"
				+ "SP.NOM_PAPELLIDO,' ', SP.NOM_SAPELLIDO), PROM.NOM_PROMOTOR) AS nomPromotor",
				 "SP.DES_PUESTO AS puesto",
				 "PROM.NUM_PLATICAS AS numPlaticas",
				 "PROM.DES_UNIDAD_IMSS AS unidad",
				 "PROM.DES_EMPRESA AS empresa",
				 "PROM.DES_ACTIVIDAD_REALIZADA AS actividadRealizada",
				 "PROM.DES_OBSERVACIONES AS observaciones",
				 "PROM.IND_EVIDENCIA AS evidencia")
		.from("SVT_ACTIVIDAD_PROMOTORES PROM")
		.join("SVT_FORMATO_ACTIVIDAD_PROMOTORES FORM", "PROM.ID_FORMATO_ACTIVIDAD = FORM.ID_FORMATO_ACTIVIDAD")
		.join("SVC_VELATORIO SV", "FORM.ID_VELATORIO = SV.ID_VELATORIO")
		.leftJoin("SVT_PROMOTOR SP", "PROM.ID_PROMOTOR = SP.ID_PROMOTOR");
		queryUtil.where("PROM.IND_ACTIVO = 1");
		queryUtil.where("PROM.ID_FORMATO_ACTIVIDAD = " +idFormato);
		String query = obtieneQuery(queryUtil);
		log.info("formato "+query); 
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put("pagina", pagina);
        parametros.put("tamanio",tamanio);
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
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

	/*public DatosRequest insertarRegistroActividades(ActividadesRequest actividadesRequest, Integer idPadre) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idPadre+"");
		q.agregarParametroValues("ID_VELATORIO", ""+actividadesRequest.getIdVelatorio()+"");
		q.agregarParametroValues("DES_FOLIO", "'prueba'");
		q.agregarParametroValues("FEC_ELABORACION", setValor(actividadesRequest.getActividades().get(1).getFecElaboracion()));
		q.agregarParametroValues("HORA_INICIO", setValor(actividadesRequest.getActividades().get(1).getHrInicio()));
		q.agregarParametroValues("HORA_FIN", setValor(actividadesRequest.getActividades().get(1).getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividadesRequest.getActividades().get(1).getIdPromotor() + "");
		//q.agregarParametroValues("NOM_OTRO_PROMOTOR", "'" +fecIngreso +"");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividadesRequest.getActividades().get(1).getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(actividadesRequest.getActividades().get(1).getUnidadImss()));
		q.agregarParametroValues("DES_EMPRESA", setValor(actividadesRequest.getActividades().get(1).getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(actividadesRequest.getActividades().get(1).getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(actividadesRequest.getActividades().get(1).getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividadesRequest.getActividades().get(1).getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		//q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
		StringBuilder queries= new StringBuilder();
		queries.append(query);
				for(int i=2; i<actividadesRequest.getActividades().size(); i++) {
					//for(String descansos: this.fecPromotorDiasDescanso) {
				        Actividades actividades = actividadesRequest.getActividades().get(i);
						queries.append("$$" + insertarMasActividades(actividades, idPadre));
			}
			log.info("estoy en resgistro actividades " +queries.toString());
			queries.append("$$"+actualizarPadre(idPadre));	 
			String encoded = encodedQuery(queries.toString());
				  parametro.put(AppConstantes.QUERY, encoded);
			        parametro.put("separador","$$");
			    //    parametro.put("replace","idTabla");
		        request.setDatos(parametro);
		return request;
	}
*/
	public DatosRequest actualizarRegistroPadre(Integer idPadre) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query = actualizarPadre(idPadre);
		String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
	        parametro.put("separador","$$");
      request.setDatos(parametro);
return request;
	}
	
	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
	}
	
}
