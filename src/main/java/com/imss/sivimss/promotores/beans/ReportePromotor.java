package com.imss.sivimss.promotores.beans;

import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.SelectQueryUtil;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;

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
}
