package com.ipartek.modelo;


import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements java.io.Serializable{
	private Integer id;
	private String user;
	private String pass;
	private Integer intentos = 0;
	private String foto;
	private Rol rolFk;
	private List<UsuarioPregunta> preguntas = new ArrayList<>();

	
}