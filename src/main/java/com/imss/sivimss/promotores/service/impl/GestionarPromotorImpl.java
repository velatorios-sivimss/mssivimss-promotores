package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.promotores.beans.GestionarPromotor;
import com.imss.sivimss.promotores.exception.BadRequestException;
import com.imss.sivimss.promotores.model.DiasDescansoModel;
import com.imss.sivimss.promotores.model.request.FiltrosPromotorRequest;
import com.imss.sivimss.promotores.model.request.PromotorRequest;
import com.imss.sivimss.promotores.model.request.UsuarioDto;
import com.imss.sivimss.promotores.model.response.PromotorResponse;
import com.imss.sivimss.promotores.service.GestionarPromotorService;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.ConvertirGenerico;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.LogUtil;
import com.imss.sivimss.promotores.util.MensajeResponseUtil;
import com.imss.sivimss.promotores.util.ProviderServiceRestTemplate;
import com.imss.sivimss.promotores.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GestionarPromotorImpl implements GestionarPromotorService{
	
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
	GestionarPromotor promotores=new GestionarPromotor();
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public Response<?> mostrarCatalogo(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosPromotorRequest filtros = gson.fromJson(datosJson, FiltrosPromotorRequest.class);
		 Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
	        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
	        filtros.setTamanio(tamanio.toString());
	        filtros.setPagina(pagina.toString());
	    	UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	        Response<?> response = providerRestTemplate.consumirServicio(promotores.catalogoPromotores(request, filtros, fecFormat).getDatos(), urlPaginado,
				authentication);
	        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CONSULTA CONTRATANTES OK", CONSULTA, authentication, usuario);
		return response;
	}
	
	@Override
	public Response<?> verDetalle(DatosRequest request, Authentication authentication) throws IOException {
		String palabra = request.getDatos().get("palabra").toString();
		List<PromotorResponse> detallePromotorResponse;
		List<DiasDescansoModel> promotorDescansos;
		PromotorResponse promoResponse;
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		Response<?> response= MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(promotores.detalle(request, palabra, fecFormat).getDatos(), urlConsulta,
				authentication), EXITO);
		if(response.getCodigo()==200) {
			detallePromotorResponse = Arrays.asList(modelMapper.map(response.getDatos(), PromotorResponse[].class));
			 promotorDescansos = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(promotores.buscarDiasDescanso(request, palabra, fecFormat).getDatos(), urlConsulta, authentication).getDatos(), DiasDescansoModel[].class));
			promoResponse = detallePromotorResponse.get(0);
			promoResponse.setPromotorDiasDescanso(promotorDescansos);
			response.setCodigo(200);
            response.setError(false);
            response.setMensaje(EXITO);
			 response.setDatos(ConvertirGenerico.convertInstanceOfObject(promoResponse));
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DETALLE PROMOTOR OK", CONSULTA, authentication, usuario);
		}
		return response;
	}
	
	
	@Override
	public Response<?> agregarPromotor(DatosRequest request, Authentication authentication) throws IOException, ParseException {
		Response<?> response = new Response<>();
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		PromotorRequest promoRequest = gson.fromJson(datosJson, PromotorRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		    promotores=new GestionarPromotor(promoRequest);
		    promotores.setFecIngreso(formatFecha(promoRequest.getFecIngreso()));
		    promotores.setFecNacimiento(formatFecha(promoRequest.getFecNac()));
			promotores.setIdUsuario(usuario.getIdUsuario());
	
			if(validarCurp(promoRequest.getCurp(), authentication)) {
				logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"EL PROMOTOR QUE DESEAS INGRESAR YA SE ENCUENTRA REGISTRADO EN EL SISTEMA.", ALTA, authentication, usuario);
				response.setCodigo(200);
				response.setError(true);
				response.setMensaje("42");
				return response;
			}
			try {
				if(promoRequest.getFecPromotorDiasDescanso()==null) {
					response = providerRestTemplate.consumirServicio(promotores.insertarPromotor().getDatos(), urlCrear, authentication);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"PROMOTOR AGREGADO CORRECTAMENTE", ALTA, authentication, usuario);
					
				}else {
					response = providerRestTemplate.consumirServicio(promotores.insertarPromotor().getDatos(), urlCrearMultiple, authentication);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"PROMOTOR AGREGADO CORRECTAMENTE", ALTA, authentication, usuario);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DIAS DE DESCANSOS AGREGADOS CORRECTAMENTE", ALTA, authentication, usuario);
						
				}
				return response;
			}catch (Exception e) {
				String consulta = promotores.insertarPromotor().getDatos().get("query").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"error", MODIFICACION, authentication, usuario);
				throw new IOException("5", e.getCause()) ;
			}
			      
}

	

	@Override
	public Response<?> actualizarPromotor(DatosRequest request, Authentication authentication) throws IOException, ParseException {
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		PromotorRequest promoRequest = gson.fromJson( String.valueOf(request.getDatos().get(AppConstantes.DATOS)), PromotorRequest.class);
		
		if (promoRequest.getIdPromotor() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);
		}
		promotores=new GestionarPromotor(promoRequest);
		promotores.setIdUsuario(usuario.getIdUsuario());
		try {
			if(promoRequest.getFecPromotorDiasDescanso()==null) {
				Response<?> response =  providerRestTemplate.consumirServicio(promotores.actualizarPromotor().getDatos(), urlActualizar, authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"PROMOTOR MODIFICADO CORRECTAMENTE", ALTA, authentication, usuario);
				return response;
			}else {
				
				Response<?> response = providerRestTemplate.consumirServicio(promotores.actualizarPromotor().getDatos(), urlInsertarMultiple,
					 authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"PROMOTOR MODIFICADO CORRECTAMENTE", ALTA, authentication, usuario);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DIAS DE DESCANSOS AGREGADOS CORRECTAMENTE", ALTA, authentication, usuario);
				return response;
			
		}
		}catch (Exception e) {
			String consulta = promotores.actualizarPromotor().getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query" +encoded);
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"error", MODIFICACION, authentication, usuario);
			throw new IOException("5", e.getCause()) ;
		}
			} 

	
	@Override
	public Response<?> cambiarEstatus(DatosRequest request, Authentication authentication) throws IOException {
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		PromotorRequest promotor = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), PromotorRequest.class);
		
		if (promotor.getIdPromotor()== null || promotor.getEstatus()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);
		}
		promotores.setIdUsuario(usuario.getIdUsuario());
		Response<?> response =  MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(promotores.cambiarEstatus(promotor).getDatos(), urlActualizar,
				authentication), EXITO);
		if(promotor.getEstatus()==1) {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ACTIVADO CORRECTAMENTE", MODIFICACION, authentication, usuario);
		}else {
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DESACTIVADO CORRECTAMENTE", BAJA, authentication, usuario);
		}
		return response;
	}

	
	private boolean validarCurp(String curp, Authentication authentication) throws IOException {
		Response<?> response= MensajeResponseUtil.mensajeConsultaResponse(providerRestTemplate.consumirServicio(promotores.buscarCurp(curp).getDatos(), urlConsulta,
				authentication), EXITO);
			Object rst=response.getDatos();
			return !rst.toString().equals("[]");	
			
	}
	
	    public String formatFecha(String fecha) throws ParseException {
		Date dateF = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
		DateFormat fecForma = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "MX"));
		return fecForma.format(dateF);       
	}

}
