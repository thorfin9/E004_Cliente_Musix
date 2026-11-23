package com.ipartek.servicios;

import org.springframework.http.ResponseEntity;

import com.ipartek.modelo.Cancion;

public interface CancionServicio {
	public ResponseEntity<?> buscar(String token, Integer usuario, Integer estilo, Integer duracionMin,
			Integer duracionMax, String titulo, String texto, Boolean reportada, boolean incluirBaneados);
	public ResponseEntity<?> obtenerPorId(String token, Integer id);
	public ResponseEntity<?> obtenerPorUsuario(String token, Integer usuarioId);
	public ResponseEntity<?> insertar(String token, Cancion c);
	public ResponseEntity<?> modificar(String token, Cancion c);
	public ResponseEntity<?> borrar(String token, Integer id);
	public ResponseEntity<?> reportar(String token, Integer id);
}