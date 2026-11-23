package com.ipartek.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cancion {
	private Integer id;
	private String titulo;
	private String enlace;
	private Integer duracion; // segundos
	private String descripcion;
	private Boolean reportada = false;
	private Estilo estiloFk = new Estilo();
	private Usuario usuarioFk = new Usuario();
}