package com.ipartek.servicios;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ipartek.modelo.Usuario;

public interface UsuarioServicio {

	public ResponseEntity<?> obtenerTodos(String token);
	public ResponseEntity<?> obtenerPorId(String token, Integer id);
	public ResponseEntity<?> obtenerPorNombre(String token, String user);
	public ResponseEntity<?> insertar(String token, Usuario usu);
	public ResponseEntity<?> modificar(String token, Usuario usu);
	public ResponseEntity<?> borrar(String token, Integer id);

	public ResponseEntity<?> validarUsuario(Usuario usu);
	public ResponseEntity<?> subirFoto(String token, Integer id, MultipartFile foto);
}