package com.imss.sivimss.promotores.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;



public interface GestionarPromotorService {
	

	Response<?> agregarPromotor(DatosRequest request, Authentication authentication)throws IOException, ParseException;

	Response<?> actualizarPromotor(DatosRequest request, Authentication authentication)throws IOException, ParseException;

	Response<?> mostrarCatalogo(DatosRequest request, Authentication authentication)throws IOException;

	Response<?> verDetalle(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<?> cambiarEstatus(DatosRequest request, Authentication authentication) throws IOException;

}
