package com.ipartek.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ipartek.modelo.Usuario;
import com.ipartek.servicios.UsuarioServicio;

import jakarta.servlet.http.HttpSession;
@Controller
public class LoginControlador {
	
	@Autowired
	private UsuarioServicio usuarioServ;

	@Value("${url.api.fotos}")
	private String urlFotos;
	
	@PostMapping("/validarLogin")
	public String validarLogin(@ModelAttribute("objUsuario") Usuario objUsuario, HttpSession session, Model model) {

		String mensaje;

		try {
			// objUsuario.pass ya viene hasheado con la pimienta (lo hace el JS del formulario)
			ResponseEntity<?> respuesta = usuarioServ.validarUsuario(objUsuario);

			switch (respuesta.getStatusCode().value()) {

			case 200:
				String token = (String) respuesta.getBody();
				Usuario usuario = (Usuario) usuarioServ.obtenerPorNombre(token, objUsuario.getUser()).getBody();

				// lo que necesita el resto de paginas
				session.setAttribute("token", token);
				session.setAttribute("usuario", usuario);
				session.setAttribute("rol", usuario.getRolFk().getNombre());
				session.setAttribute("urlFotos", urlFotos);

				return "redirect:/menu";

			case 423:
				mensaje = "Cuenta bloqueada por 3 intentos fallidos. Contacta con un administrador.";
				break;

			case 403:
				mensaje = "Tu cuenta ha sido baneada.";
				break;

			default:
				mensaje = "Usuario o contraseña incorrectos. Con 3 fallos la cuenta se bloquea.";
				break;
			}

		} catch (Exception e) {
			mensaje = "No se ha podido conectar con el servidor. Inténtalo más tarde.";
		}

		model.addAttribute("objUsuario", new Usuario());
		model.addAttribute("f_mensaje", mensaje);
		return "login";
	}

	@GetMapping("/cerrarSesion")
	public String cerrarSesion(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

}
