package com.reparalo.tutorialservice.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.reparalo.tutorialservice.model.Tutorial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TutorialService {

    private static final String COLECCION = "tutoriales";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public List<Tutorial> obtenerTodosTutoriales() {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = firestore.collection(COLECCION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            List<Tutorial> tutoriales = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Tutorial tutorial = document.toObject(Tutorial.class);
                if (tutorial.isActivo()) {
                    tutoriales.add(tutorial);
                }
            }
            return tutoriales;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al obtener tutoriales: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tutoriales", e);
        }
    }

    public List<Tutorial> obtenerTutorialesPorCategoria(String categoria) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = firestore.collection(COLECCION)
                    .whereEqualTo("categoria", categoria)
                    .whereEqualTo("activo", true)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Tutorial> tutoriales = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                tutoriales.add(document.toObject(Tutorial.class));
            }
            return tutoriales;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al obtener tutoriales por categoría: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tutoriales por categoría", e);
        }
    }

    public List<Tutorial> obtenerTutorialesPorDificultad(String dificultad) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = firestore.collection(COLECCION)
                    .whereEqualTo("dificultad", dificultad)
                    .whereEqualTo("activo", true)
                    .get();
            
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<Tutorial> tutoriales = new ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                tutoriales.add(document.toObject(Tutorial.class));
            }
            return tutoriales;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al obtener tutoriales por dificultad: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tutoriales por dificultad", e);
        }
    }

    public Tutorial obtenerTutorial(String id) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Tutorial tutorial = document.toObject(Tutorial.class);
                if (tutorial != null && tutorial.isActivo()) {
                    // Incrementar contador de vistas
                    tutorial.setVistas(tutorial.getVistas() + 1);
                    docRef.update("vistas", tutorial.getVistas());
                    return tutorial;
                }
                return null; // Tutorial inactivo
            } else {
                log.info("Tutorial no encontrado con ID: {}", id);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al obtener tutorial: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tutorial", e);
        }
    }

    public Tutorial crearTutorial(Tutorial tutorial, String autorId, String autorNombre) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            
            // Generar ID si no existe
            if (tutorial.getId() == null || tutorial.getId().isEmpty()) {
                DocumentReference docRef = firestore.collection(COLECCION).document();
                tutorial.setId(docRef.getId());
            }
            
            // Establecer datos del autor y fechas
            tutorial.setAutorId(autorId);
            tutorial.setAutorNombre(autorNombre);
            tutorial.setFechaCreacion(LocalDateTime.now().format(FORMATTER));
            tutorial.setFechaActualizacion(LocalDateTime.now().format(FORMATTER));
            tutorial.setActivo(true);
            tutorial.setVistas(0);
            tutorial.setLikes(0);
            
            // Guardar en Firestore
            DocumentReference docRef = firestore.collection(COLECCION).document(tutorial.getId());
            ApiFuture<WriteResult> result = docRef.set(tutorial);
            result.get(); // Esperar a que se complete la escritura
            
            return tutorial;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al crear tutorial: {}", e.getMessage());
            throw new RuntimeException("Error al crear tutorial", e);
        }
    }

    public Tutorial actualizarTutorial(String id, Tutorial tutorial) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Tutorial no encontrado para actualizar con ID: {}", id);
                return null;
            }

            Tutorial tutorialExistente = document.toObject(Tutorial.class);
            if (tutorialExistente == null) {
                return null;
            }
            
            // Mantener datos que no deben cambiar
            tutorial.setId(id);
            tutorial.setAutorId(tutorialExistente.getAutorId());
            tutorial.setAutorNombre(tutorialExistente.getAutorNombre());
            tutorial.setFechaCreacion(tutorialExistente.getFechaCreacion());
            tutorial.setVistas(tutorialExistente.getVistas());
            tutorial.setLikes(tutorialExistente.getLikes());
            tutorial.setActivo(tutorialExistente.isActivo());
            
            // Actualizar fecha
            tutorial.setFechaActualizacion(LocalDateTime.now().format(FORMATTER));
            
            // Guardar cambios
            ApiFuture<WriteResult> writeResult = docRef.set(tutorial);
            writeResult.get();
            
            return tutorial;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al actualizar tutorial: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar tutorial", e);
        }
    }

    public boolean eliminarTutorial(String id) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Tutorial no encontrado para eliminar con ID: {}", id);
                return false;
            }

            // Marcar como inactivo en lugar de eliminar físicamente
            Map<String, Object> updates = Map.of(
                "activo", false,
                "fechaActualizacion", LocalDateTime.now().format(FORMATTER)
            );
            
            ApiFuture<WriteResult> writeResult = docRef.update(updates);
            writeResult.get();
            
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al eliminar tutorial: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar tutorial", e);
        }
    }

    public boolean incrementarLikes(String id) {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentReference docRef = firestore.collection(COLECCION).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (!document.exists()) {
                log.info("Tutorial no encontrado para dar like con ID: {}", id);
                return false;
            }

            Tutorial tutorial = document.toObject(Tutorial.class);
            if (tutorial == null || !tutorial.isActivo()) {
                return false;
            }
            
            // Incrementar likes
            int nuevosLikes = tutorial.getLikes() + 1;
            ApiFuture<WriteResult> writeResult = docRef.update("likes", nuevosLikes);
            writeResult.get();
            
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al incrementar likes: {}", e.getMessage());
            throw new RuntimeException("Error al incrementar likes", e);
        }
    }
}