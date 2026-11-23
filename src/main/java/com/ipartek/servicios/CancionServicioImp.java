package com.ipartek.servicios;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.ipartek.modelo.Cancion;

@Service
public class CancionServicioImp implements CancionServicio {

	@Value("${url.api.canciones}")
	private String URL;
	private RestClient restClient = RestClient.create();

	@Override
	public ResponseEntity<?> buscar(String token, Integer usuario, Integer estilo, Integer duracionMin,
			Integer duracionMax, String titulo, String texto, Boolean reportada, boolean incluirBaneados) {

		URI uri = UriComponentsBuilder.fromUriString(URL + "buscar")
				.queryParamIfPresent("usuario", Optional.ofNullable(usuario))
				.queryParamIfPresent("estilo", Optional.ofNullable(estilo))
				.queryParamIfPresent("duracionMin", Optional.ofNullable(duracionMin))
				.queryParamIfPresent("duracionMax", Optional.ofNullable(duracionMax))
				.queryParamIfPresent("titulo", Optional.ofNullable(titulo).filter(s -> !s.isBlank()))
				.queryParamIfPresent("texto", Optional.ofNullable(texto).filter(s -> !s.isBlank()))
				.queryParamIfPresent("reportada", Optional.ofNullable(reportada))
				.queryParam("incluirBaneados", incluirBaneados)
				.build().encode().toUri();

		return restClient.get()
				.uri(uri)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Cancion[].class);
	}

	@Override
	public ResponseEntity<?> obtenerPorId(String token, Integer id) {
		return restClient.get()
				.uri(URL + id)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Cancion.class);
	}

	@Override
	public ResponseEntity<?> obtenerPorUsuario(String token, Integer usuarioId) {
		return restClient.get()
				.uri(URL + "usuario/" + usuarioId)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Cancion[].class);
	}

	@Override
	public ResponseEntity<?> insertar(String token, Cancion c) {
		return restClient.post()
				.uri(URL)
				.header("Authorization", "Bearer " + token)
				.body(c)
				.retrieve()
				.toEntity(Cancion.class);
	}

	@Override
	public ResponseEntity<?> modificar(String token, Cancion c) {
		return restClient.put()
				.uri(URL)
				.header("Authorization", "Bearer " + token)
				.body(c)
				.retrieve()
				.toEntity(Cancion.class);
	}

	@Override
	public ResponseEntity<?> borrar(String token, Integer id) {
		return restClient.delete()
				.uri(URL + id)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Boolean.class);
	}

	@Override
	public ResponseEntity<?> reportar(String token, Integer id) {
		return restClient.put()
				.uri(URL + id + "/reportar")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Cancion.class);
	}
}