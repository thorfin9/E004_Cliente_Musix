package com.ipartek.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ipartek.modelo.Pregunta;

@Service
public class PreguntaServicioImp implements PreguntaServicio {

	@Value("${url.api.preguntas}")
	private String URL;
	private RestClient restClient = RestClient.create();

	@Override
	public ResponseEntity<?> obtenerTodas() {
		ResponseEntity<?> resultado = restClient.get()
				.uri(URL)
				.retrieve()
				.toEntity(Pregunta[].class);

		return resultado;
	}

}