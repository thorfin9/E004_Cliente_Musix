package com.ipartek.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ipartek.modelo.Cancion;
import com.ipartek.modelo.Estilo;
import com.ipartek.modelo.Usuario;
import com.ipartek.servicios.CancionServicio;
import com.ipartek.servicios.EstiloServicio;

import jakarta.servlet.http.HttpSession;

@Controller
public class CancionControlador {

	@Autowired
	private CancionServicio cancionServ;

	@Autowired
	private EstiloServicio estiloServ;

	private boolean esUsuario(HttpSession session) {
		return "USUARIO".equals(session.getAttribute("rol"));
	}

	private Cancion[] lista(ResponseEntity<?> r) {
		Cancion[] c = (Cancion[]) r.getBody();
		return c == null ? new Cancion[0] : c;
	}

	// ---------- CANCIONES (todas, sin las de usuarios baneados)
	@GetMapping("/canciones")
	public String canciones(@RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
	    if (!esUsuario(session)) {
	        return "redirect:/";
	    }
	    String token = (String) session.getAttribute("token");
	    try {
	        Cancion[] todas = lista(cancionServ.buscar(token, null, null, null, null, null, null, null, false));

	        int pageSize = 5; // Configurable
	        int totalItems = todas.length;
	        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

	        // Validate page bounds
	        if (page < 0) page = 0;
	        if (page >= totalPages && totalPages > 0) page = totalPages - 1;

	        int start = page * pageSize;
	        int end = Math.min(start + pageSize, totalItems);

	        Cancion[] paginaCanciones = new Cancion[0];
	        if (totalItems > 0 && start < totalItems) {
	            paginaCanciones = java.util.Arrays.copyOfRange(todas, start, end);
	        }

	        model.addAttribute("canciones", paginaCanciones); // Keep name 'canciones' for the view to avoid breaking things, but it now holds the page.
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", totalPages);
	        model.addAttribute("totalItems", totalItems);

	    } catch (Exception e) {
	        model.addAttribute("canciones", new Cancion[0]);
	        model.addAttribute("f_mensaje", "No se han podido cargar las canciones.");
	    }
	    return "canciones";
	}

	@GetMapping("/reportarCancion/{id}")
	public String reportar(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		try {
			cancionServ.reportar((String) session.getAttribute("token"), id);
			redirect.addFlashAttribute("f_mensaje", "Canción reportada. ¡Gracias!");
		} catch (Exception e) {
			redirect.addFlashAttribute("f_mensaje", "No se ha podido reportar la canción.");
		}
		return "redirect:/canciones";
	}

	// ---------- MIS CANCIONES (CRUD)
	private String cargarMisCanciones(Model model, HttpSession session, Cancion cancion) {
		String token = (String) session.getAttribute("token");
		Usuario yo = (Usuario) session.getAttribute("usuario");
		try {
			model.addAttribute("canciones", lista(cancionServ.obtenerPorUsuario(token, yo.getId())));
			Estilo[] estilos = (Estilo[]) estiloServ.obtenerTodos().getBody();
			model.addAttribute("listaEstilos", estilos == null ? new Estilo[0] : estilos);
		} catch (Exception e) {
			model.addAttribute("canciones", new Cancion[0]);
			model.addAttribute("listaEstilos", new Estilo[0]);
			model.addAttribute("f_mensaje", "No se han podido cargar los datos.");
		}
		model.addAttribute("objCancion", cancion);
		return "mis_canciones";
	}

	@GetMapping("/misCanciones")
	public String misCanciones(Model model, HttpSession session) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		return cargarMisCanciones(model, session, new Cancion());
	}

	@GetMapping("/editarCancion/{id}")
	public String editarCancion(@PathVariable Integer id, Model model, HttpSession session) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		try {
			Usuario yo = (Usuario) session.getAttribute("usuario");
			Cancion c = (Cancion) cancionServ.obtenerPorId((String) session.getAttribute("token"), id).getBody();
			if (c != null && yo.getId().equals(c.getUsuarioFk().getId())) {
				return cargarMisCanciones(model, session, c);
			}
		} catch (Exception e) {
			// cae al redirect
		}
		return "redirect:/misCanciones";
	}

	@PostMapping("/guardarCancion")
	public String guardarCancion(@ModelAttribute("objCancion") Cancion objCancion, HttpSession session,
			RedirectAttributes redirect) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		String token = (String) session.getAttribute("token");
		Usuario yo = (Usuario) session.getAttribute("usuario");
		String mensaje;

		try {
			// el dueño siempre es el usuario de la sesión
			Usuario dueno = new Usuario();
			dueno.setId(yo.getId());
			objCancion.setUsuarioFk(dueno);

			if (objCancion.getId() == null) {
				cancionServ.insertar(token, objCancion);
				mensaje = "Canción añadida.";
			} else {
				cancionServ.modificar(token, objCancion);
				mensaje = "Canción modificada.";
			}
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
				mensaje = "Datos no válidos: el enlace debe ser de YouTube y la duración mayor que 0.";
			} else {
				mensaje = "Error al guardar la canción.";
			}
		} catch (Exception e) {
			mensaje = "No se ha podido conectar con el servidor. Inténtalo más tarde.";
		}
		redirect.addFlashAttribute("f_mensaje", mensaje);
		return "redirect:/misCanciones";
	}

	@GetMapping("/borrarCancion/{id}")
	public String borrarCancion(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
		if (!esUsuario(session)) {
			return "redirect:/";
		}
		String token = (String) session.getAttribute("token");
		Usuario yo = (Usuario) session.getAttribute("usuario");
		String mensaje;

		try {
			Cancion c = (Cancion) cancionServ.obtenerPorId(token, id).getBody();
			if (c != null && yo.getId().equals(c.getUsuarioFk().getId())) {
				cancionServ.borrar(token, id);
				mensaje = "Canción borrada.";
			} else {
				mensaje = "No puedes borrar esa canción.";
			}
		} catch (Exception e) {
			mensaje = "No se ha podido borrar la canción.";
		}
		redirect.addFlashAttribute("f_mensaje", mensaje);
		return "redirect:/misCanciones";
	}
}