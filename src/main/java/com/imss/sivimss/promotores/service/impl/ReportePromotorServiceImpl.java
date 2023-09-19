package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.promotores.beans.OrdenesServicio;
import com.imss.sivimss.promotores.beans.Promotor;
import com.imss.sivimss.promotores.beans.ReportePromotor;
import com.imss.sivimss.promotores.model.request.ReporteComisionesPromotorDto;
import com.imss.sivimss.promotores.model.request.UsuarioDto;
import com.imss.sivimss.promotores.model.response.PromotoresResponse;
import com.imss.sivimss.promotores.service.ReportePromotorService;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.ConvertirGenerico;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.LogUtil;
import com.imss.sivimss.promotores.util.MensajeResponseUtil;
import com.imss.sivimss.promotores.util.ProviderServiceRestTemplate;
import com.imss.sivimss.promotores.util.Response;

@Service
public class ReportePromotorServiceImpl implements ReportePromotorService {
    
    @Autowired
    private ProviderServiceRestTemplate providerRestTemplate;
  
    @Value("${data.msit_COMISION_PROMOTOR}")
    private String nombreReporteComision;
    
    @Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
    
    @Value("${endpoints.ms-reportes}")
	private String urlReportes;
    
    private static final String EXITO = "EXITO";
    
    @Autowired
    ModelMapper modelMapper;
    
    ReportePromotor reporte = new ReportePromotor();
    
    Promotor promotor=Promotor.getInstancia();

    OrdenesServicio ordenesServicio= OrdenesServicio.getInstance();
    
    @Autowired
	private LogUtil logUtil;

    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportePromotorServiceImpl.class);
   
    @Override
    public Response<?> buscarReportes(DatosRequest request, Authentication authentication) throws IOException {
        String datosJson = request.getDatos().get(AppConstantes.DATOS).toString();        
        Gson gson= new Gson();
        ReporteComisionesPromotorDto reporteComisionesPromotorDto = gson.fromJson(datosJson, ReporteComisionesPromotorDto.class);
        Map<String, Object> envioDatos = ReportePromotor.generarReporteComision(reporteComisionesPromotorDto,nombreReporteComision);
        return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes,
                authentication);
    }
    
    @Override
    public Response<?> obtenerPromotores(DatosRequest request, Authentication authentication) throws IOException {
    	Gson gson= new Gson();
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try {
			
            logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
            		this.getClass().getPackage().toString(), "consultarPromotores", AppConstantes.CONSULTA, authentication,usuario);
            Response<?> response= MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(promotor.consultarPromotores().getDatos(), urlConsulta,
    				authentication), EXITO);
            List<PromotoresResponse> promotorResponses;
			if (response.getCodigo() == 200 && !response.getDatos().toString().contains("[]")) {
				promotorResponses = Arrays.asList(modelMapper.map(response.getDatos(), PromotoresResponse[].class));
				response.setDatos(ConvertirGenerico.convertInstanceOfObject(promotorResponses));
			}

			return response;
		} catch (Exception e) {
			String consulta = promotor.consultarPromotores().getDatos().get(AppConstantes.QUERY).toString();
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication,usuario);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
    }
    
    @Override
    public Response<?> obtenerOrdenes(DatosRequest request, Authentication authentication) throws IOException {
    	Gson gson= new Gson();
	    UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		try { 
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
            		this.getClass().getPackage().toString(), "consultarOrdenes", AppConstantes.CONSULTA, authentication,usuario);

			String datosJson=request.getDatos().get(AppConstantes.DATOS).toString();
        
	        ReporteComisionesPromotorDto reporteComisionesPromotorDto = gson.fromJson(datosJson, ReporteComisionesPromotorDto.class);
	        return MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(ordenesServicio.consultarOrdenes(reporteComisionesPromotorDto).getDatos(), urlConsulta,
    				authentication), EXITO);
		} catch (Exception e) {
			String consulta = promotor.consultarPromotores().getDatos().get(AppConstantes.QUERY).toString();
	        String decoded = new String(DatatypeConverter.parseBase64Binary(consulta));
	        log.error(AppConstantes.ERROR_QUERY.concat(decoded));
	        log.error(e.getMessage());
	        logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), AppConstantes.ERROR_LOG_QUERY + decoded, AppConstantes.CONSULTA, authentication,usuario);
	        throw new IOException(AppConstantes.ERROR_CONSULTAR, e.getCause());
		}
    }
}
