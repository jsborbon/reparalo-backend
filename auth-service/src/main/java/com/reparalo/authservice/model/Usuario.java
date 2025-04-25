package com.reparalo.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private String uid;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private String rol; // CLIENTE, TECNICO, ADMIN
    private String urlFoto;
    private Map<String, Object> metadatos;
    private boolean activo;
    private String fechaCreacion;
    private String fechaActualizacion;
}