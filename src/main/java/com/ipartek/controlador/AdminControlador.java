package com.ipartek.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ipartek.modelo.Cancion;
import com.ipartek.modelo.Estilo;
import com.ipartek.modelo.Usuario;
import com.ipartek.servicios.CancionServicio;
import com.ipartek.servicios.EstiloServicio;
import com.ipartek.servicios.UsuarioServicio;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminControlador {

	@Autowired
	private CancionServicio cancionServ;

	@Autowired
	private EstiloServicio estiloServ;

	@Autowired
	private UsuarioServicio usuarioServ;

	private boolean esAdmin(HttpSession session) {
		return "ADMIN".equals(session.getAttribute("rol"));
	}

	
	@GetMapping("/gestionCanciones")
	public String gestionCanciones(@RequestParam(required = false) Integer usuario,
			@RequestParam(required = false) Integer estilo, @RequestParam(required = false) Integer duracionMin,
			@RequestParam(required = false) Integer duracionMax, @RequestParam(required = false) Boolean reportada,
			Model model, HttpSession session) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		String token = (String) session.getAttribute("token");

		try {
			Cancion[] canciones = (Cancion[]) cancionServ
					.buscar(token, usuario, estilo, duracionMin, duracionMax, null, null, reportada, true).getBody();
			Usuario[] usuarios = (Usuario[]) usuarioServ.obtenerTodos(token).getBody();
			Estilo[] estilos = (Estilo[]) estiloServ.obtenerTodos().getBody();

			model.addAttribute("canciones", canciones == null ? new Cancion[0] : canciones);
			model.addAttribute("listaUsuarios", usuarios == null ? new Usuario[0] : usuarios);
			model.addAttribute("listaEstilos", estilos == null ? new Estilo[0] : estilos);
		} catch (Exception e) {
			model.addAttribute("canciones", new Cancion[0]);
			model.addAttribute("listaUsuarios", new Usuario[0]);
			model.addAttribute("listaEstilos", new Estilo[0]);
			model.addAttribute("f_mensaje", "No se han podido cargar los datos.");
		}

		
		model.addAttribute("fUsuario", usuario);
		model.addAttribute("fEstilo", estilo);
		model.addAttribute("fMin", duracionMin);
		model.addAttribute("fMax", duracionMax);
		model.addAttribute("fReportada", reportada);
		return "gestion_canciones";
	}

	@GetMapping("/borrarCancionAdmin/{id}")
	public String borrarCancion(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		try {
			cancionServ.borrar((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Canción borrada.");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido borrar la canción.");
		}
		return "redirect:/gestionCanciones";
	}

	
	@GetMapping("/gestionUsuarios")
	public String gestionUsuarios(Model model, HttpSession session) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		try {
			Usuario[] usuarios = (Usuario[]) usuarioServ.obtenerTodos((String) session.getAttribute("token"))
					.getBody();
			model.addAttribute("usuarios", usuarios == null ? new Usuario[0] : usuarios);
		} catch (Exception e) {
			model.addAttribute("usuarios", new Usuario[0]);
			model.addAttribute("f_mensaje", "No se han podido cargar los usuarios.");
		}
		return "gestion_usuarios";
	}

	private String destino(String volver) {
		return "gestionCanciones".equals(volver) ? "redirect:/gestionCanciones" : "redirect:/gestionUsuarios";
	}

	@GetMapping("/banearUsuario/{id}")
	public String banear(@PathVariable Integer id, @RequestParam(defaultValue = "") String volver,
			HttpSession session, RedirectAttributes redirect) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		Usuario yo = (Usuario) session.getAttribute("usuario");
		if (yo == null) {
			return "redirect:/";
		}
		if (yo.getId().equals(id)) {
			redirect.addFlashAttribute("f_mensaje", "No puedes banearte a ti mismo.");
			return destino(volver);
		}
		try {
			usuarioServ.banear((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Usuario baneado.");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido banear al usuario.");
		}
		return destino(volver);
	}

	@GetMapping("/desbanearUsuario/{id}")
	public String desbanear(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		try {
			usuarioServ.desbanear((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Usuario desbaneado.");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido desbanear al usuario.");
		}
		return "redirect:/gestionUsuarios";
	}
	
	@GetMapping("/bloquearUsuario/{id}")
	public String bloquear(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		Usuario yo = (Usuario) session.getAttribute("usuario");
		if (yo == null) {
			return "redirect:/";
		}
		if (yo.getId().equals(id)) {
			redirect.addFlashAttribute("f_mensaje", "No puedes bloquearte a ti mismo.");
			return "redirect:/gestionUsuarios";
		}
		try {
			usuarioServ.bloquear((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Usuario bloqueado.");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido bloquear al usuario.");
		}
		return "redirect:/gestionUsuarios";
	}

	@GetMapping("/desbloquearUsuario/{id}")
	public String desbloquear(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esAdmin(session)) {
			return "redirect:/";
		}
		try {
			usuarioServ.desbloquear((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Usuario desbloqueado.");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido desbloquear al usuario.");
		}
		return "redirect:/gestionUsuarios";
	}
}