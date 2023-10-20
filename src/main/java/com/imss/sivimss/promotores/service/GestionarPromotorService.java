package com.imss.sivimss.promotores.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;



public interface GestionarPromotorService {
	

	Response<Object> agregarPromotor(DatosRequest request, Authentication authentication)throws IOException, ParseException;

	Response<Object> actualizarPromotor(DatosRequest request, Authentication authentication)throws IOException, ParseException;

	Response<Object> mostrarCatalogo(DatosRequest request, Authentication authentication)throws IOException;

	Response<Object> verDetalle(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> cambiarEstatus(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> buscarPorNombre(DatosRequest request, Authentication authentication) throws IOException;

}
