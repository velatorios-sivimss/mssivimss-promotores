package com.imss.sivimss.promotores.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;

public interface RegistrarActividadService {

	Response<?> agregarRegistroActividades(DatosRequest request, Authentication authentication) throws IOException, ParseException;

	Response<?> buscarFormatoActividades(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> detalleFormatoActividades(DatosRequest request, Authentication authentication)  throws IOException;

	Response<?> eliminarActividad(DatosRequest request, Authentication authentication) throws IOException;

}
