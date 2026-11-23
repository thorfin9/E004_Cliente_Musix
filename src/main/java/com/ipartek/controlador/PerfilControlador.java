package com.ipartek.controlador;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilControlador {

	@Autowired
	private UsuarioServicio usuarioServ;

	@Autowired
	private PreguntaServicio preguntaServ;

	private boolean esUsuario(HttpSession session) {
		return "USUARIO".equals(session.getAttribute("rol"));
	}

	private String cargar(Model model, Usuario form) {
		while (form.getPreguntas().size() < 3) {
			form.getPreguntas().add(new UsuarioPregunta());
		}
		model.addAttribute("objUsuario", form);
		try {
			model.addAttribute("listaPreguntas", preguntaServ.obtenerTodas().getBody());
		} catch (Exception e) {
			model.addAttribute("listaPreguntas", new Pregunta[0]);
			model.addAttribute("f_mensaje", "No se han podido cargar las preguntas de seguridad.");
		}
		return "perfil";
	}

	@GetMapping("/perfil")
	public String perfil(Model model, HttpSession session) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		Usuario yo = (Usuario) session.getAttribute("usuario");

		// el formulario se rellena con una copia de los datos actuales
		Usuario form = new Usuario();
		form.setUser(yo.getUser());
		for (UsuarioPregunta p : yo.getPreguntas()) {
			UsuarioPregunta copia = new UsuarioPregunta();
			copia.setPreguntaFk(p.getPreguntaFk());
			form.getPreguntas().add(copia);
		}
		return cargar(model, form);
	}

	@PostMapping("/guardarPerfil")
	public String guardarPerfil(@ModelAttribute("objUsuario") Usuario objUsuario,
			@RequestParam("archivoFoto") MultipartFile archivoFoto, Model model, HttpSession session,
			RedirectAttributes redirect) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		String token = (String) session.getAttribute("token");
		Usuario yo = (Usuario) session.getAttribute("usuario");
		String mensaje;

		try {
			// las preguntas solo se mandan si se han escrito las 3 respuestas
			boolean cambiaPreguntas = objUsuario.getPreguntas().size() == 3 && objUsuario.getPreguntas().stream()
					.allMatch(p -> p.getRespuesta() != null && !p.getRespuesta().isBlank());
			if (!cambiaPreguntas) {
				objUsuario.setPreguntas(new ArrayList<>());
			}

			// pass vacío = no cambiarla; intentos y rol no se tocan desde el perfil
			if (objUsuario.getPass() != null && objUsuario.getPass().isBlank()) {
				objUsuario.setPass(null);
			}
			objUsuario.setId(yo.getId());
			objUsuario.setIntentos(null);
			objUsuario.setRolFk(null);

			usuarioServ.modificar(token, objUsuario);

			if (!archivoFoto.isEmpty()) {
				usuarioServ.subirFoto(token, yo.getId(), archivoFoto);
			}

			// refrescar los datos de la sesión (nombre, foto, preguntas)
			Usuario actualizado = (Usuario) usuarioServ.obtenerPorId(token, yo.getId()).getBody();
			session.setAttribute("usuario", actualizado);

			redirect.addFlashAttribute("f_mensaje", "Perfil actualizado.");
			return "redirect:/perfil";

		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
				mensaje = "Ese nombre de usuario ya existe.";
			} else if (e.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
				mensaje = "Datos no válidos: usuario de 3-30 caracteres y 3 preguntas distintas con su respuesta.";
			} else {
				mensaje = "Error al actualizar el perfil.";
			}
		} catch (Exception e) {
			mensaje = "No se ha podido conectar con el servidor. Inténtalo más tarde.";
		}

		objUsuario.setPass(null);
		cargar(model, objUsuario);
		model.addAttribute("f_mensaje", mensaje);
		return "perfil";
	}
}