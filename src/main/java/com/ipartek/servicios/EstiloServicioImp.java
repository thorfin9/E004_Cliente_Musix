package com.ipartek.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ipartek.modelo.Estilo;

@Service
public class EstiloServicioImp implements EstiloServicio {

	@Value("${url.api.estilos}")
	private String URL;
	private RestClient restClient = RestClient.create();

	@Override
	public ResponseEntity<?> obtenerTodos() {
		return restClient.get().uri(URL).retrieve().toEntity(Estilo[].class);
	}
}