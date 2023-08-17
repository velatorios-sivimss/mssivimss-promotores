package com.imss.sivimss.promotores.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;

public interface RegistrarActividadService {

	Response<?> agregarRegistroActividades(DatosRequest request, Authentication authentication) throws IOException;

}