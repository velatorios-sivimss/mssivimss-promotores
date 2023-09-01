package com.imss.sivimss.promotores.model.response;

import com.imss.sivimss.promotores.model.InformacionReportesModel;
import lombok.Data;

import java.util.List;

@Data
public class ReportePromotorResponse {
    private List<InformacionReportesModel> reportes;
}
