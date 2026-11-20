package com.ipartek.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class MenuControlador {

	@GetMapping("/menu")
	public String menu(HttpSession session) {
		if (session.getAttribute("token") == null) {
			return "redirect:/";
		}
		return "menu";
	}

	private String pendiente(Model model, String titulo, HttpSession session, String rolNecesario) {
		if (!rolNecesario.equals(session.getAttribute("rol"))) {
			return "redirect:/";
		}
		model.addAttribute("titulo", titulo);
		return "pendiente";
	}

	// --- USUARIO
	@GetMapping("/canciones")
	public String canciones(Model model, HttpSession session) {
		return pendiente(model, "Canciones", session, "USUARIO");
	}

	@GetMapping("/misCanciones")
	public String misCanciones(Model model, HttpSession session) {
		return pendiente(model, "Mis canciones", session, "USUARIO");
	}

	@GetMapping("/perfil")
	public String perfil(Model model, HttpSession session) {
		return pendiente(model, "Perfil", session, "USUARIO");
	}

	// --- ADMIN
	@GetMapping("/gestionCanciones")
	public String gestionCanciones(Model model, HttpSession session) {
		return pendiente(model, "Gestionar canciones", session, "ADMIN");
	}

	@GetMapping("/gestionUsuarios")
	public String gestionUsuarios(Model model, HttpSession session) {
		return pendiente(model, "Gestionar usuarios", session, "ADMIN");
	}

}