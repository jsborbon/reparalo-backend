package com.reparalo.authservice.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.reparalo.authservice.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UsuarioService {

    private static final String COLECCION = "usuarios";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Usuario obtenerUsuario(String uid) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Usuario.class);
            } else {
                log.info("Usuario no encontrado con UID: {}", uid);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al obtener usuario: {}", e.getMessage());
            throw new RuntimeException("Error al obtener usuario", e);
        }
    }

    public Usuario crearUsuario(Usuario usuario) {
        try {
            if (usuario.getUid() == null) {
                throw new IllegalArgumentException("El UID del usuario no puede ser nulo");
            }

            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(usuario.getUid());

            // Establecer valores por defecto
            if (usuario.getRol() == null) {
                usuario.setRol("CLIENTE"); // Rol por defecto
            }
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now().format(FORMATTER));
            usuario.setFechaActualizacion(LocalDateTime.now().format(FORMATTER));

            // Guardar en Firestore
            docRef.set(usuario).get();
            return usuario;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    public Usuario actualizarUsuario(String uid, Usuario usuario) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Usuario no encontrado para actualizar con UID: {}", uid);
                return null;
            }

            // Asegurar que el UID no cambie
            usuario.setUid(uid);
            usuario.setFechaActualizacion(LocalDateTime.now().format(FORMATTER));

            // Preservar la fecha de creación original
            Usuario usuarioExistente = document.toObject(Usuario.class);
            if (usuarioExistente != null && usuarioExistente.getFechaCreacion() != null) {
                usuario.setFechaCreacion(usuarioExistente.getFechaCreacion());
            }

            // Actualizar en Firestore
            docRef.set(usuario).get();
            return usuario;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al actualizar usuario: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    public boolean eliminarUsuario(String uid) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Usuario no encontrado para eliminar con UID: {}", uid);
                return false;
            }

            // Eliminar de Firestore
            docRef.delete().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al eliminar usuario: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    public boolean actualizarRol(String uid, String nuevoRol) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Usuario no encontrado para actualizar rol con UID: {}", uid);
                return false;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("rol", nuevoRol);
            updates.put("fechaActualizacion", LocalDateTime.now().format(FORMATTER));

            docRef.update(updates).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al actualizar rol de usuario: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar rol de usuario", e);
        }
    }
}