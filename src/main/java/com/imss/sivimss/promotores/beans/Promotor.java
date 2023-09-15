package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.SelectQueryUtil;

public class Promotor {

	private static Promotor instancia;
	
	private static final Logger log = LoggerFactory.getLogger(Promotor.class);

	private Promotor() {}
	
	public static Promotor getInstancia() {
		if (instancia==null) {
			instancia= new Promotor();
		}
		
		return instancia;
	}
	
	public DatosRequest consultarPromotores() {
		DatosRequest datosRequest= new DatosRequest();
		Map<String, Object>parametro= new HashMap<>();
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select("SPR.ID_PROMOTOR AS idPromotor","CONCAT(SPR.NOM_PROMOTOR,' ', SPR.NOM_PAPELLIDO,' ',SPR.NOM_SAPELLIDO) AS nombrePromotor")
		.from("SVT_PROMOTOR SPR")
		.where("SPR.IND_ACTIVO in (0,1) ")
		
		.orderBy("CONCAT(SPR.NOM_PROMOTOR,' ', SPR.NOM_PAPELLIDO,' ',SPR.NOM_SAPELLIDO) ASC");
		String query= selectQueryUtil.build();
		log.info(query);

		String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		datosRequest.setDatos(parametro);
		return datosRequest;
	}
}
