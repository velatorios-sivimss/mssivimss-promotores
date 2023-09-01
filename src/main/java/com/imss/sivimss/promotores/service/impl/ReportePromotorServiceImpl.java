package com.imss.sivimss.promotores.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imss.sivimss.promotores.beans.ReportePromotor;
import com.imss.sivimss.promotores.controller.ReportePromotorController;
import com.imss.sivimss.promotores.model.InformacionReportesModel;
import com.imss.sivimss.promotores.model.response.ReportePromotorResponse;
import com.imss.sivimss.promotores.service.ReportePromotorService;
import com.imss.sivimss.promotores.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportePromotorServiceImpl implements ReportePromotorService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportePromotorServiceImpl.class);
    @Autowired
    private ProviderServiceRestTemplate providerRestTemplate;
    @Value("${endpoints.mod-catalogos}")
    private String consultas;
    @Autowired
    ModelMapper modelMapper;
    ReportePromotor reporte = new ReportePromotor();

    @Override
    public Response<?> buscarReportes(DatosRequest request, Authentication authentication) throws IOException {
        log.info(request.getDatos().toString());
        JsonObject jsonObject = JsonParser.parseString((String) request.getDatos().get(AppConstantes.DATOS)).getAsJsonObject();
        Response<?> response = new Response<>();
        String tipoReporte = jsonObject.get("tipoReporte").getAsString();
        log.info("service - " + tipoReporte);
        List<InformacionReportesModel> infoReportes;
        Response<?> responseReportes = providerRestTemplate.consumirServicio(reporte.buscarReportes(tipoReporte).getDatos(), consultas + "/consulta", authentication);
        infoReportes = Arrays.asList(modelMapper.map(responseReportes.getDatos(), InformacionReportesModel[].class));
        ReportePromotorResponse rp = new ReportePromotorResponse();
        rp.setReportes(infoReportes);
        response.setDatos(ConvertirGenerico.convertInstanceOfObject(rp));
        response.setError(false);
        response.setCodigo(200);
        response.setMensaje("");
        return response;
    }
}
