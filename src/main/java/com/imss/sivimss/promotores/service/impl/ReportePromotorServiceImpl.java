package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.promotores.beans.ReportePromotor;
import com.imss.sivimss.promotores.model.request.ReporteComisionesPromotorDto;
import com.imss.sivimss.promotores.service.ReportePromotorService;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.ProviderServiceRestTemplate;
import com.imss.sivimss.promotores.util.Response;

@Service
public class ReportePromotorServiceImpl implements ReportePromotorService {
    
    @Autowired
    private ProviderServiceRestTemplate providerRestTemplate;
  
    @Value("${data.msit_COMISION_PROMOTOR}")
    private String nombreReporteComision;
    
    @Value("${endpoints.mod-catalogos}")
    private String consultas;
    
    @Value("${endpoints.ms-reportes}")
	private String urlReportes;
    
    @Autowired
    ModelMapper modelMapper;
    
    ReportePromotor reporte = new ReportePromotor();
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportePromotorServiceImpl.class);
   
    @Override
    public Response<?> buscarReportes(DatosRequest request, Authentication authentication) throws IOException {
        String datosJson = request.getDatos().get(AppConstantes.DATOS).toString();
        
        Gson gson= new Gson();
        
        ReporteComisionesPromotorDto reporteComisionesPromotorDto = gson.fromJson(datosJson, ReporteComisionesPromotorDto.class);
    
        Map<String, Object> envioDatos = ReportePromotor.generarReporteComision(reporteComisionesPromotorDto);
        return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes.concat(nombreReporteComision),
                authentication);
    }
}
