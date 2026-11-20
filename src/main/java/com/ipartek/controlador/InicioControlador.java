package com.ipartek.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ipartek.modelo.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class InicioControlador {
	
	@GetMapping("/")
	public String cargaInicio(Model model, HttpSession session) {
		if (session.getAttribute("token") != null) {
			return "redirect:/menu";
		}

		model.addAttribute("objUsuario", new Usuario());
		return "login";
	}

}
