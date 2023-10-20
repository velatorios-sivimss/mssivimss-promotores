package com.imss.sivimss.promotores.service;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;

public interface RegistrarActividadService {

	Response<Object> agregarRegistroActividades(DatosRequest request, Authentication authentication) throws IOException, ParseException;

	Response<Object> buscarFormatoActividades(DatosRequest request, Authentication authentication) throws IOException, ParseException;

	Response<Object> detalleActividades(DatosRequest request, Authentication authentication)  throws IOException;

	Response<Object> eliminarActividad(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> detalleFormatoActividades(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> catalogos(DatosRequest request, Authentication authentication) throws IOException;

	Response<Object> descargarReporteActividades(DatosRequest request, Authentication authentication) throws IOException, ParseException;

	Response<Object> generarFormato(DatosRequest request, Authentication authentication) throws IOException;

}
