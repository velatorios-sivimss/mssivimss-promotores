package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.promotores.model.request.Actividades;
import com.imss.sivimss.promotores.model.request.ActividadesRequest;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.QueryHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistrarActividad {

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
	
	
	public DatosRequest insertarActividades(ActividadesRequest actividadesRequest) {
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


	public String insertarMasActividades(Actividades actividades, Integer idPadre) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idPadre+"");
		q.agregarParametroValues("ID_VELATORIO", ""+actividades.getIdVelatorio()+"");
		q.agregarParametroValues("DES_FOLIO", "'123'");
		q.agregarParametroValues("FEC_ELABORACION", setValor(actividades.getFecElaboracion()));
		q.agregarParametroValues("HORA_INICIO", setValor(actividades.getHrInicio()));
		q.agregarParametroValues("HORA_FIN", setValor(actividades.getHrFin()));
		q.agregarParametroValues("ID_PROMOTOR", "" +actividades.getIdPromotor() + "");
		//q.agregarParametroValues("NOM_OTRO_PROMOTOR", "'" +fecIngreso +"");
		q.agregarParametroValues("NUM_PLATICAS", ""+ actividades.getNumPlaticas()+"");
		q.agregarParametroValues("DES_UNIDAD_IMSS", setValor(actividades.getUnidadImss()));
		q.agregarParametroValues("DES_EMPRESA", setValor(actividades.getEmpresa()));
		q.agregarParametroValues("DES_ACTIVIDAD_REALIZADA", setValor(actividades.getActividadRealizada()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(actividades.getObservaciones()));
		q.agregarParametroValues("IND_EVIDENCIA", ""+actividades.getEvidencia()+"");
		q.agregarParametroValues("" +AppConstantes.IND_ACTIVO+ "", "1");
		//q.agregarParametroValues("ID_USUARIO_ALTA", "" +idUsuario+ "");
		q.agregarParametroValues("FEC_ALTA", "" +AppConstantes.CURRENT_TIMESTAMP + "");
		String query = q.obtenerQueryInsertar();
		  String encoded = encodedQuery(query);
		  parametro.put(AppConstantes.QUERY, encoded);
        request.setDatos(parametro);
        return query;
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

	public DatosRequest insertarRegistroActividades(ActividadesRequest actividadesRequest, Integer idPadre) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_ACTIVIDAD_PROMOTORES");
		q.agregarParametroValues("ID_FORMATO_ACTIVIDAD", ""+idPadre+"");
		q.agregarParametroValues("ID_VELATORIO", ""+actividadesRequest.getActividades().get(1).getIdVelatorio()+"");
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

}
