package com.ipartek.modelo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta implements java.io.Serializable{
	private Integer id;
	private String texto;

}