package com.imss.sivimss.promotores.beans;

import com.imss.sivimss.promotores.model.request.ReporteComisionesPromotorDto;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.SelectQueryUtil;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportePromotor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportePromotor.class);

    public DatosRequest buscarReportes(String tipoReporte){
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("ID_CONFIG_REPORTE AS idReporte","DES_REPORTE AS nombreReporte")
                .from("SVC_CONFIG_REPORTE")
                .where("IND_TIPO_REPORTE = " + tipoReporte);
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }
    
    public static Map<String, Object> generarReporteComision(ReporteComisionesPromotorDto comisionesPromotorDto, String reporte) {
    	Map<String, Object>params= new HashMap<>();
    	StringBuilder query= new StringBuilder("");
    	
    	if (Objects.nonNull(comisionesPromotorDto.getAnio())) {
			query.append(" AND SM.NUM_ANIO_COMISION = ".concat(comisionesPromotorDto.getAnio().toString()));
		}
    	
    	if (Objects.nonNull(comisionesPromotorDto.getMes())) {
			query.append(" AND SM.NUM_MES_COMISION = ".concat(comisionesPromotorDto.getMes().toString()));
		}
    	
    	if (Objects.nonNull(comisionesPromotorDto.getId_promotor())) {
			query.append(" AND SM.ID_PROMOTOR = ".concat(comisionesPromotorDto.getId_promotor().toString()));
		}
    	
    	if (Objects.nonNull(comisionesPromotorDto.getOds())) {
			query.append(" AND SOS.CVE_FOLIO = ".concat(comisionesPromotorDto.getOds()));
		}

    	/*
    	 id delegacion == null traer todas delegacion, no se le agrega el and delegacion
    	 id velatorio == null no se agrega el and
    	 * */
    	if (Objects.nonNull(comisionesPromotorDto.getId_velatorio())) {
    		query.append(" AND SPO.ID_VELATORIO =  ".concat(comisionesPromotorDto.getId_velatorio().toString()));
    	}
    	
    	if (Objects.nonNull(comisionesPromotorDto.getId_delegacion())) {
    		query.append(" AND SPO.ID_DELEGACION = ".concat(comisionesPromotorDto.getId_delegacion().toString()));
    	}
    	params.put("consultaOrdenes", query.toString());
    	params.put("periodo", comisionesPromotorDto.getMes()+"/"+comisionesPromotorDto.getAnio());
    	params.put("velatorio", comisionesPromotorDto.getNombreVelatorio());
    	params.put("rutaNombreReporte", reporte);
    	params.put("tipoReporte", comisionesPromotorDto.getTipoReporte());
    	log.info(params.get("consultaOrdenes").toString());
    	return params;
    }
    
    
}
