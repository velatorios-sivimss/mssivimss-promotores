package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import com.google.gson.Gson;
import com.imss.sivimss.promotores.beans.RegistrarActividad;
import com.imss.sivimss.promotores.exception.BadRequestException;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorActividadesRequest;
import com.imss.sivimss.promotores.model.request.RegistrarFormatoActividadesRequest;
import com.imss.sivimss.promotores.model.request.ReporteDto;
import com.imss.sivimss.promotores.model.request.UsuarioDto;
import com.imss.sivimss.promotores.service.RegistrarActividadService;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.LogUtil;
import com.imss.sivimss.promotores.util.MensajeResponseUtil;
import com.imss.sivimss.promotores.util.ProviderServiceRestTemplate;
import com.imss.sivimss.promotores.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistrarActividadImpl implements RegistrarActividadService {

	@Autowired
	private LogUtil logUtil;

	@Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
	@Value("${endpoints.rutas.dominio-consulta-paginado}")
	private String urlPaginado;
	@Value("${endpoints.rutas.dominio-crear}")
	private String urlCrear;
	@Value("${endpoints.rutas.dominio-crear-multiple}")
	private String urlCrearMultiple;
	@Value("${endpoints.rutas.dominio-insertar-multiple}")
	private String urlInsertarMultiple;
	@Value("${endpoints.rutas.dominio-actualizar}")
	private String urlActualizar;
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	@Value("${formato-fecha}")
	private String fecFormat;
	@Value("${data.msit_ANEXO_PROM_DIFUSION}")
	private String anexo;
	@Value("${data.msit_REGISTRO_ACTIV}")
	private String reporteActiv;
	
	private static final String BAJA = "baja";
	private static final String ALTA = "alta";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	private static final String INFORMACION_INCOMPLETA = "Informacion incompleta";
	private static final String EXITO = "EXITO";
	private static final String IMPRIMIR = "IMPRIMIR";

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();

	RegistrarActividad registrarActividad=new RegistrarActividad();
	
	
	@Override
	public Response<?> buscarFormatoActividades(DatosRequest request, Authentication authentication) throws IOException, ParseException {
		GestionarPromotorImpl prom = new GestionarPromotorImpl();
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosPromotorActividadesRequest filtros = gson.fromJson(datosJson, FiltrosPromotorActividadesRequest.class);
		 Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
	        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
	        filtros.setTamanio(tamanio.toString());
	        filtros.setPagina(pagina.toString());
	    	UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	    	if(filtros.getFecInicio()!=null) {
	    		registrarActividad.setFecInicio(prom.formatFecha(filtros.getFecInicio()));
	    	}
	    	if(filtros.getFecFin()!=null) {
	    		registrarActividad.setFecFin(prom.formatFecha(filtros.getFecFin()));
	    	}
	    	Response<?> response = providerRestTemplate.consumirServicio(registrarActividad.buscarFormatoActividades(request, filtros, fecFormat).getDatos(), urlPaginado,
				authentication);
	        if(response.getDatos().toString().contains("id")) {
	        	logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CONSULTA FORMATO REGISTRO DE ACTIVIDADES OK", CONSULTA, authentication, usuario);
	        }else {
	        	response.setError(true);
	        	response.setMensaje("45");
	        	response.setDatos(null);
	        	logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"NO HAY INFORMACION RELACIONADA A TU BUSQUEDA", CONSULTA, authentication, usuario);
	        } 
	    	return response;
	}


	@Override
	public Response<?> agregarRegistroActividades(DatosRequest request, Authentication authentication)
			throws IOException, ParseException {
		GestionarPromotorImpl prom = new GestionarPromotorImpl();
		Response<?> response = new Response<>();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RegistrarFormatoActividadesRequest actividadesRequest =  gson.fromJson(datosJson, RegistrarFormatoActividadesRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	
		registrarActividad=new RegistrarActividad(actividadesRequest);
		registrarActividad.setIdUsuario(usuario.getIdUsuario());
		if(actividadesRequest.getFecInicio()!=null) {
			registrarActividad.setFecInicio(prom.formatFecha(actividadesRequest.getFecInicio()));
			registrarActividad.setFecFin(prom.formatFecha(actividadesRequest.getFecFin()));
		}
		registrarActividad.setFecActividad(prom.formatFecha(actividadesRequest.getActividades().getFecActividad()));
		
			try {
				if(actividadesRequest.getActividades().getIdActividad()!=null) {
					if(validarDias(actividadesRequest.getActividades().getIdActividad(), authentication)) {
				       response.setCodigo(200);
				       response.setError(true);
				       response.setMensaje("5");
				       return response;
					}
					response = providerRestTemplate.consumirServicio(registrarActividad.actualizarActividad(actividadesRequest.getActividades()).getDatos(), urlActualizar, authentication);	
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"REGISTRO MODIFICADO CORRCTEMENTE", MODIFICACION, authentication, usuario);
				}
				else if(actividadesRequest.getIdFormato()==null && actividadesRequest.getActividades().getIdActividad()==null) {
					response =  providerRestTemplate.consumirServicio(registrarActividad.insertarFormatoActividades(actividadesRequest.getActividades()).getDatos(), urlCrear, authentication);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"FORMATO DE ACTIVIDADES REGISTRADO CORRECTAMENTE", ALTA, authentication, usuario);
					if(response.getCodigo()==200) {
					Integer idFormato = Integer.parseInt(response.getDatos().toString());
					Response<?> responseActividad = providerRestTemplate.consumirServicio(registrarActividad.insertarActividad(actividadesRequest.getActividades(), actividadesRequest.getIdFormato(), idFormato).getDatos(), urlInsertarMultiple, authentication);
					 logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"REGISTRO AGREGADO CORRECTAMENTE", ALTA, authentication, usuario);
					if(responseActividad.getCodigo()==200) {
						responseActividad = response;
						return responseActividad;
					}
					}
				}else if(actividadesRequest.getIdFormato() !=null){
					Integer idForma=null;
					 response = providerRestTemplate.consumirServicio(registrarActividad.insertarActividad(actividadesRequest.getActividades(), actividadesRequest.getIdFormato(), idForma).getDatos(), urlCrear, authentication);
					 logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"REGISTRO AGREGADO CORRECTAMENTE", ALTA, authentication, usuario);
				}
					return response;
			}catch (Exception e) {
				String consulta = registrarActividad.insertarFormatoActividades(actividadesRequest.getActividades()).getDatos().get("query").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ERROR AL AGREGAR ACTUALIZAR EL REGISTRO", ALTA, authentication, usuario);
				throw new IOException("5", e.getCause()) ;
			}
	}
	

	@Override
	public Response<?> detalleFormatoActividades(DatosRequest request, Authentication authentication)
			throws IOException {
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		 Response<?> response = MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(registrarActividad.datosFormato(request, fecFormat).getDatos(), urlConsulta,
					authentication), EXITO);   
	        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), "Consulta formato Ok", CONSULTA, authentication, usuario);
	    	return response;
	}
	
	
	@Override
	public Response<?> detalleActividades(DatosRequest request, Authentication authentication)
			throws IOException {
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		String palabra = request.getDatos().get("palabra").toString();
		Integer idFormato = Integer.parseInt(palabra);
		Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
        Response<?> response = MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(registrarActividad.verDetalleActividades(request, idFormato, pagina, tamanio, fecFormat).getDatos(), urlPaginado,
				authentication), EXITO);   
        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),
				this.getClass().getPackage().toString(), "Consulta actividades Ok", CONSULTA, authentication, usuario);
    	return response;
	}
	
	private boolean validarDias(Integer idActividad, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(registrarActividad.buscarFormato(idActividad).getDatos(), urlConsulta,
				authentication);
		if (response.getCodigo()==200){
			Object rst=response.getDatos();
			return !rst.toString().equals("[]");	
			}
		return true;
	}

	@Override
	public Response<?> eliminarActividad(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		FiltrosPromotorActividadesRequest filtros =  gson.fromJson(datosJson, FiltrosPromotorActividadesRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		Response<?> response= MensajeResponseUtil.mensajeResponse(providerRestTemplate.consumirServicio(registrarActividad.eliminarActividad(filtros.getIdActividad(), usuario.getIdUsuario()).getDatos(), urlActualizar,
				authentication), EXITO);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"REGISTRO ELIMINADO CORRECTAMENTE", BAJA, authentication, usuario);
		return response;
	}


	@Override
	public Response<?> catalogos(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosPromotorActividadesRequest filtros = gson.fromJson(datosJson, FiltrosPromotorActividadesRequest.class);
	    	UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	    
			      if(filtros.getIdCatalogo() ==1) {
	    		response = providerRestTemplate.consumirServicio(registrarActividad.catalogoPromotores(request, filtros.getIdVelatorio()).getDatos(), urlConsulta,
						authentication);
			        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CATALOGO PROMOTORES OK", CONSULTA, authentication, usuario);
	    	}else {
	    		 logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"INFORMACION INCOMPLETA", CONSULTA, authentication, usuario);
				 throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);
	    	}
	    	return response;
	}


	@Override
	public Response<?> descargarReporteActividades(DatosRequest request, Authentication authentication)
			throws IOException, ParseException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		ReporteDto reporte= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RegistrarActividad().reporteActividades(reporte, reporteActiv);
		Response<?> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes,
				authentication);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"SE GENERO CORRECRAMENTE EL REPORTE DE ACTIVIDADES", IMPRIMIR, authentication, usuario);
		return response;
	}


	@Override
	public Response<?> generarFormato(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		ReporteDto reporte= gson.fromJson(datosJson, ReporteDto.class);
		reporte.setIdRol(usuario.getIdRol());
		reporte.setIdVelatorio(usuario.getIdVelatorio());
		Map<String, Object> envioDatos = new RegistrarActividad().formatoActividades(reporte, anexo);
		Response<?> response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes,
				authentication);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"SE GENERO CORRECRAMENTE EL REPORTE DE ACTIVIDADES", IMPRIMIR, authentication, usuario);
		return response;
	}

}
