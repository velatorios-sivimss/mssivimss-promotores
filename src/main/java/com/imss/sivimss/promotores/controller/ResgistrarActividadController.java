package com.imss.sivimss.promotores.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.promotores.service.GestionarPromotorService;
import com.imss.sivimss.promotores.service.RegistrarActividadService;
import com.imss.sivimss.promotores.service.impl.ResgistrarActividadImpl;
import com.imss.sivimss.promotores.util.DatosRequest;
import com.imss.sivimss.promotores.util.ProviderServiceRestTemplate;
import com.imss.sivimss.promotores.util.Response;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/registrar-actividad")
public class ResgistrarActividadController {
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private RegistrarActividadService registrarActividad;
	
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/buscar")
	public CompletableFuture<?> buscarFormatoActividadesPromotor(@RequestBody DatosRequest request,Authentication authentication) throws IOException{
		Response<?> response = registrarActividad.buscarFormatoActividades(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}	
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/detalle-actividades")
	public CompletableFuture<?> detalleActividadesPromotor(@RequestBody DatosRequest request,Authentication authentication) throws IOException{
		Response<?> response = registrarActividad.detalleActividades(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}	

	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/detalle-formato")
	public CompletableFuture<?> detalleFormatoActividadesPromotor(@RequestBody DatosRequest request,Authentication authentication) throws IOException{
		Response<?> response = registrarActividad.detalleFormatoActividades(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}	
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/agregar")
	public CompletableFuture<?> insertarAtividadesPromotor(@RequestBody DatosRequest request,Authentication authentication) throws IOException, ParseException{
		Response<?> response = registrarActividad.agregarRegistroActividades(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
      
	}
	

	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/eliminar")
	public CompletableFuture<?> cambiarEstatusActividad(@RequestBody DatosRequest request,Authentication authentication) throws IOException{
		Response<?> response = registrarActividad.eliminarActividad(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
      
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/catalogo")
	public CompletableFuture<?> verCatalogo(@RequestBody DatosRequest request,Authentication authentication) throws IOException{
		Response<?> response = registrarActividad.catalogos(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
      
	}

	/**
	 * fallbacks generico
	 * 
	 * @return respuestas
	 */
	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			CallNotPermittedException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			RuntimeException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			NumberFormatException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

}
