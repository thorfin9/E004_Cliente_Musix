package com.ipartek.servicios;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.ipartek.modelo.Usuario;

@Service
public class UsuarioServicioImp implements UsuarioServicio {

	@Value("${url.api.usuarios}")
	private String URL;
	private RestClient restClient = RestClient.create();

	@Override
	public ResponseEntity<?> obtenerTodos(String token) {
		ResponseEntity<?> resultado = restClient.get()
				.uri(URL)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario[].class);

		return resultado;
	}

	@Override
	public ResponseEntity<?> obtenerPorId(String token, Integer id) {
		ResponseEntity<?> resultado = restClient.get()
				.uri(URL + id)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);

		return resultado;
	}

	@Override
	public ResponseEntity<?> obtenerPorNombre(String token, String user) {
		ResponseEntity<?> resultado = restClient.get()
				.uri(URL + "nombre/" + user)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);

		return resultado;
	}

	@Override
	public ResponseEntity<?> insertar(String token, Usuario usu) {
		ResponseEntity<?> resultado = restClient.post()
				.uri(URL)
				.headers(h -> {
					if (token != null) {
						h.setBearerAuth(token);
					}
				})
				.body(usu)
				.retrieve()
				.toEntity(Usuario.class);

		return resultado;
	}

	@Override
	public ResponseEntity<?> modificar(String token, Usuario usu) {
		ResponseEntity<?> resultado = restClient.put()
				.uri(URL)
				.header("Authorization", "Bearer " + token)
				.body(usu)
				.retrieve()
				.toEntity(Usuario.class);

		return resultado;
	}

	@Override
	public ResponseEntity<?> borrar(String token, Integer id) {
		ResponseEntity<?> resultado = restClient.delete()
				.uri(URL + id)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Boolean.class);

		return resultado;
	}

	
	@Override
	public ResponseEntity<?> validarUsuario(Usuario usu) {
		try {
			return restClient.post()
					.uri(URL + "validarUsuario")
					.body(usu)
					.retrieve()
					.toEntity(String.class);
		} catch (HttpStatusCodeException e) {
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	@Override
	public ResponseEntity<?> subirFoto(String token, Integer id, MultipartFile foto) {
		try {
			final String nombreArchivo = foto.getOriginalFilename();

			MultiValueMap<String, Object> cuerpo = new LinkedMultiValueMap<>();
			cuerpo.add("foto", new ByteArrayResource(foto.getBytes()) {
				@Override
				public String getFilename() {
					return nombreArchivo;
				}
			});

			return restClient.post()
					.uri(URL + id + "/foto")
					.headers(h -> {
						if (token != null) {
							h.setBearerAuth(token);
						}
					})
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(cuerpo)
					.retrieve()
					.toEntity(Usuario.class);
		} catch (IOException e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@Override
	public ResponseEntity<?> banear(String token, Integer id) {
		return restClient.put()
				.uri(URL + id + "/banear")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);
	}

	@Override
	public ResponseEntity<?> desbanear(String token, Integer id) {
		return restClient.put()
				.uri(URL + id + "/desbanear")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);
	}
	
	@Override
	public ResponseEntity<?> bloquear(String token, Integer id) {
		return restClient.put()
				.uri(URL + id + "/bloquear")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);
	}

	@Override
	public ResponseEntity<?> desbloquear(String token, Integer id) {
		return restClient.put()
				.uri(URL + id + "/desbloquear")
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.toEntity(Usuario.class);
	}

}