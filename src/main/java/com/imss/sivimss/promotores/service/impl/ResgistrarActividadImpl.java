package com.imss.sivimss.promotores.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imss.sivimss.promotores.beans.GestionarPromotor;
import com.imss.sivimss.promotores.beans.RegistrarActividad;
import com.imss.sivimss.promotores.model.request.Actividades;
import com.imss.sivimss.promotores.model.request.RegistrarActividadesRequest;
import com.imss.sivimss.promotores.model.request.PromotorRequest;
import com.imss.sivimss.promotores.model.request.UsuarioDto;
import com.imss.sivimss.promotores.service.RegistrarActividadService;
import com.imss.sivimss.promotores.util.AppConstantes;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.LogUtil;
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
	public Response<?> agregarRegistroActividades(DatosRequest request, Authentication authentication)
			throws IOException {
		Response<?> response = new Response<>();
		 //JsonParser parser = new JsonParser();
	     //JsonObject jO = (JsonObject) parser.parse((String) request.getDatos().get(AppConstantes.DATOS));
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		RegistrarActividadesRequest actividadesRequest =  gson.fromJson(datosJson, RegistrarActividadesRequest.class);	
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		registrarActividad=new RegistrarActividad(actividadesRequest);
		registrarActividad.setIdUsuario(usuario.getIdUsuario());
			try {
				response = providerRestTemplate.consumirServicio(registrarActividad.insertarFormatoActividades().getDatos(), urlCrearMultiple, authentication);
				/*	response = providerRestTemplate.consumirServicio(registrarActividad.insertarActividades(actividadesRequest).getDatos(), urlCrear, authentication);
				response = providerRestTemplate.consumirServicio(registrarActividad.insertarMasActividades(actividadesRequest).getDatos(), urlCrear, authentication);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"REGISTRO PADRE AGREGADO CORRECTAMENTE", ALTA, authentication, usuario);		
					Integer idPadre = Integer.parseInt(response.getDatos().toString()); 
					if(response.getCodigo()==200 && actividadesRequest.getActividades().size()>1) {
					providerRestTemplate.consumirServicio(registrarActividad.insertarRegistroActividades(actividadesRequest, idPadre).getDatos(), urlInsertarMultiple, authentication);
				}else {
					providerRestTemplate.consumirServicio(registrarActividad.actualizarRegistroPadre(idPadre).getDatos(), urlInsertarMultiple, authentication);	
				} */
					
					return response;
			}catch (Exception e) {
				String consulta = registrarActividad.insertarFormatoActividades().getDatos().get("query").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"error", MODIFICACION, authentication, usuario);
				throw new IOException("5", e.getCause()) ;
			}
	}

}
