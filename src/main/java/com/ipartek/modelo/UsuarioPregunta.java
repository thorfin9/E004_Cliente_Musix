package com.ipartek.modelo;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPregunta implements java.io.Serializable{
	private Integer id;
	private Pregunta preguntaFk = new Pregunta();
	private String respuesta;

}