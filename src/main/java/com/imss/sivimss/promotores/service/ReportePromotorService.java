package com.imss.sivimss.promotores.service;

import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.Response;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface ReportePromotorService {
    Response<?> buscarReportes(DatosRequest request, Authentication authentication) throws IOException;
}
