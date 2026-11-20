package com.ipartek.modelo;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPregunta {
	private Integer id;
	private Pregunta preguntaFk = new Pregunta();
	private String respuesta;

}