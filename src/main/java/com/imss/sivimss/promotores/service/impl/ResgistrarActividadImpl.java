package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import com.google.gson.Gson;
import com.imss.sivimss.promotores.beans.RegistrarActividad;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorActividadesRequest;
import com.imss.sivimss.promotores.model.request.RegistrarFormatoActividadesRequest;
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
public class ResgistrarActividadImpl implements RegistrarActividadService {

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
	@Value("${formato-fecha}")
	private String fecFormat;
	
	private static final String BAJA = "baja";
	private static final String ALTA = "alta";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	private static final String INFORMACION_INCOMPLETA = "Informacion incompleta";
	private static final String EXITO = "EXITO";

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	RegistrarActividad registrarActividad=new RegistrarActividad();
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@Override
	public Response<?> buscarFormatoActividades(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosPromotorActividadesRequest filtros = gson.fromJson(datosJson, FiltrosPromotorActividadesRequest.class);
		 Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
	        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
	        filtros.setTamanio(tamanio.toString());
	        filtros.setPagina(pagina.toString());
	    	UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	    	Response<?> response = providerRestTemplate.consumirServicio(registrarActividad.buscarFormatoActividades(request, filtros, fecFormat).getDatos(), urlPaginado,
				authentication);
	        if(response.getDatos().toString().contains("id")) {
	        	logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CONSULTA FORMATO REGISTRO DE ACTIVIDADES OK", CONSULTA, authentication, usuario);
	        }else {
	        	response.setError(true);
	        	response.setMensaje("45");
	        	response.setDatos(null);
	        } 
	    	return response;
	}


	@Override
	public Response<?> agregarRegistroActividades(DatosRequest request, Authentication authentication)
			throws IOException {
		Response<?> response = new Response<>();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RegistrarFormatoActividadesRequest actividadesRequest =  gson.fromJson(datosJson, RegistrarFormatoActividadesRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		registrarActividad=new RegistrarActividad(actividadesRequest);
		registrarActividad.setIdUsuario(usuario.getIdUsuario());
			try {
				if(actividadesRequest.getIdFormato()==null) {
					response =  providerRestTemplate.consumirServicio(registrarActividad.insertarFormatoActividades(actividadesRequest.getActividades()).getDatos(), urlCrear, authentication);
				}else {
					response = providerRestTemplate.consumirServicio(registrarActividad.insertarActividad(actividadesRequest.getActividades(), actividadesRequest.getIdFormato()).getDatos(), urlCrearMultiple, authentication);
				}
				
					return response;
			}catch (Exception e) {
				String consulta = registrarActividad.insertarFormatoActividades(actividadesRequest.getActividades()).getDatos().get("query").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"error", MODIFICACION, authentication, usuario);
				throw new IOException("5", e.getCause()) ;
			}
	}
	
	/*
	@Override
	public Response<?> actualizarFormato(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response = new Response<>();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RegistrarFormatoActividadesRequest actividadesRequest =  gson.fromJson(datosJson, RegistrarFormatoActividadesRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		
		registrarActividad=new RegistrarActividad(actividadesRequest);
		registrarActividad.setIdUsuario(usuario.getIdUsuario());
	/*	if(!validarDias(actividadesRequest.getIdFormato(), authentication)) {
			response.setCodigo(200);
			response.setError(true);
			response.setMensaje("5");
			response.setDatos(null);
		} 
		try {
				response = providerRestTemplate.consumirServicio(registrarActividad.actualizarRegistroActividades().getDatos(), urlInsertarMultiple, authentication);		
					return response;
			}catch (Exception e) {
				String consulta = registrarActividad.actualizarRegistroActividades().getDatos().get("query").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"error", MODIFICACION, authentication, usuario);
				throw new IOException("5", e.getCause()) ;
			}
	} */

	@Override
	public Response<?> detalleFormatoActividades(DatosRequest request, Authentication authentication)
			throws IOException {
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		String palabra = request.getDatos().get("palabra").toString();
		Integer idFormato = Integer.parseInt(palabra);
		Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
        Response<?> response = MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(registrarActividad.verDetalleActividades(request, idFormato, pagina, tamanio).getDatos(), urlPaginado,
				authentication), EXITO);   
        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),
				this.getClass().getPackage().toString(), "Consulta actividades Ok", CONSULTA, authentication, usuario);
    	return response;
	/*	UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		Response<?> response = new Response<>();
		List<FormatoResponse> formato;
		List<ActividadesResponse> actividades;
		// providerRestTemplate.consumirServicio(renovarBean.validarBeneficiarios(request, numConvenio, idContra, usuarioDto.getIdUsuario()).getDatos(), urlActualizar,authentication);
		Response<?> responseDatosFormato = providerRestTemplate.consumirServicio(registrarActividad.datosFormato(request, idFormato, fecFormat, pagina, tamanio).getDatos(), urlPaginado,
				authentication);
		if(responseDatosFormato.getCodigo()==200) {
			formato = Arrays.asList(modelMapper.map(responseDatosFormato.getDatos(), FormatoResponse[].class));
			actividades = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(registrarActividad.buscarActividades(request, idFormato, pagina, tamanio).getDatos(), urlPaginado, authentication).getDatos(), ActividadesResponse[].class));  
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), "Consulta Beneficiarios Ok", CONSULTA, authentication, usuario);
			FormatoResponse datosFormato = formato.get(0);
			datosFormato.setActividades(actividades);
			 response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosFormato));
		}
		    response.setCodigo(200);
            response.setError(false);
            response.setMensaje("Exito"); */
	}
	
	private boolean validarDias(Integer idFormato, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(registrarActividad.buscarRepetido(idFormato).getDatos(), urlConsulta,
				authentication);
		if (response.getCodigo()==200){
			Object rst=response.getDatos();
			return !rst.toString().equals("[]");	
			}
		return false;
	}


	@Override
	public Response<?> actualizarFormato(DatosRequest request, Authentication authentication) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
