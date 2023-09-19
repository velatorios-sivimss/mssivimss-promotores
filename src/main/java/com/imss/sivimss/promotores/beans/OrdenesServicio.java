package com.imss.sivimss.promotores.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.promotores.model.request.ReporteComisionesPromotorDto;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.SelectQueryUtil;

public class OrdenesServicio {

	private static OrdenesServicio instance;
	
	private static final Logger log = LoggerFactory.getLogger(OrdenesServicio.class);

	private OrdenesServicio () {}
	
	public static OrdenesServicio getInstance() {
		if (instance==null) {
			instance= new OrdenesServicio();
		}
		
		return instance;
	}
	
	public DatosRequest consultarOrdenes(ReporteComisionesPromotorDto comisionesPromotorDto) {
		DatosRequest datosRequest= new DatosRequest();
		Map<String, Object>params=new HashMap<>();
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select("SO.CVE_FOLIO AS folio")
		.from("SVC_ORDEN_SERVICIO SO")
		.join("SVC_VELATORIO SV", "SO.ID_VELATORIO = SV.ID_VELATORIO")
		.join("SVC_DELEGACION SDE", "SV.ID_DELEGACION = SDE.ID_DELEGACION")
		.where("SO.ID_ESTATUS_ORDEN_SERVICIO IN (4,5) ")
		.and("SO.CVE_FOLIO is not null ");
		
		if (Objects.nonNull(comisionesPromotorDto.getId_velatorio())) {
			selectQueryUtil.and("SO.ID_VELATORIO =  ".concat(comisionesPromotorDto.getId_velatorio().toString()));
    	}
    	
    	if (Objects.nonNull(comisionesPromotorDto.getId_delegacion())) {
    		selectQueryUtil.and("SV.ID_DELEGACION = ".concat(comisionesPromotorDto.getId_delegacion().toString()));
    	}
    	
    	
    	String query=selectQueryUtil.build();
    	log.info(query);
    	String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
    	params.put(AppConstantes.QUERY, encoded);
    	datosRequest.setDatos(params);
		return datosRequest;
	}
}
