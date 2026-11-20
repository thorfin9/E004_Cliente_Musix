package com.ipartek.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ipartek.modelo.Pregunta;
import com.ipartek.modelo.Usuario;
import com.ipartek.modelo.UsuarioPregunta;
import com.ipartek.servicios.PreguntaServicio;
import com.ipartek.servicios.UsuarioServicio;


@Controller
public class UsuarioControlador {

	@Autowired
	private UsuarioServicio usuarioServ;

	@Autowired
	private PreguntaServicio preguntaServ;

	
	private String cargarFormulario(Model model, Usuario usuario) {
		while (usuario.getPreguntas().size() < 3) {
			usuario.getPreguntas().add(new UsuarioPregunta());
		}
		model.addAttribute("objUsuario", usuario);

		try {
			model.addAttribute("listaPreguntas", preguntaServ.obtenerTodas().getBody());
		} catch (Exception e) {
			model.addAttribute("listaPreguntas", new Pregunta[0]);
			model.addAttribute("f_mensaje", "No se han podido cargar las preguntas de seguridad.");
		}
		return "frm_usuarios_alta";
	}

	@GetMapping("/CrearUsuario")
	public String crearUsuario(Model model) {
		return cargarFormulario(model, new Usuario());
	}

	@PostMapping("/guardarUsuario")
	public String guardarUsuario(@ModelAttribute("objUsuario") Usuario objUsuario,
			@RequestParam("archivoFoto") MultipartFile archivoFoto, Model model, RedirectAttributes redirect) {

		String mensaje;

		try {
			// 1. crear el usuario (endpoint publico, sin token)
			ResponseEntity<?> respuesta = usuarioServ.insertar(null, objUsuario);
			Usuario creado = (Usuario) respuesta.getBody();

			mensaje = "Usuario creado correctamente. Ya puedes entrar.";

			// 2. si ha elegido foto, se sube
			if (!archivoFoto.isEmpty()) {
				try {
					usuarioServ.subirFoto(null, creado.getId(), archivoFoto);
				} catch (Exception e) {
					mensaje += " (No se pudo subir la foto, podrás cambiarla en tu perfil.)";
				}
			}

			// redirect + mensaje flash: si recarga la pagina no se vuelve a crear el usuario
			redirect.addFlashAttribute("f_mensaje", mensaje);
			return "redirect:/";

		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
				mensaje = "Ese nombre de usuario ya existe.";
			} else if (e.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
				mensaje = "Datos no válidos: usuario de 3-30 caracteres (letras, números, _ . -) y 3 preguntas distintas con su respuesta.";
			} else {
				mensaje = "Error al crear el usuario.";
			}
		} catch (Exception e) {
			mensaje = "No se ha podido conectar con el servidor. Inténtalo más tarde.";
		}

		// volver al formulario sin la contraseña
		objUsuario.setPass(null);
		cargarFormulario(model, objUsuario);
		model.addAttribute("f_mensaje", mensaje);
		return "frm_usuarios_alta";
	}

}