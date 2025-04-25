package com.reparalo.technicianservice.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.reparalo.technicianservice.model.Technician;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class TechnicianRepository {

    private final Firestore firestore;
    private final CollectionReference techniciansCollection;

    public TechnicianRepository(Firestore firestore) {
        this.firestore = firestore;
        this.techniciansCollection = firestore.collection("technicians");
    }

    public Technician save(Technician technician) throws ExecutionException, InterruptedException {
        if (technician.getId() == null) {
            technician.setId(UUID.randomUUID().toString());
            technician.setCreatedAt(LocalDateTime.now());
            // Inicializar valores por defecto para nuevos técnicos
            if (technician.getRating() == null) technician.setRating(0.0);
            if (technician.getReviewCount() == null) technician.setReviewCount(0);
            if (technician.getAvailable() == null) technician.setAvailable(true);
        }
        technician.setUpdatedAt(LocalDateTime.now());
        
        techniciansCollection.document(technician.getId()).set(technician).get();
        return technician;
    }

    public Optional<Technician> findById(String id) throws ExecutionException, InterruptedException {
        var documentSnapshot = techniciansCollection.document(id).get().get();
        if (documentSnapshot.exists()) {
            return Optional.of(documentSnapshot.toObject(Technician.class));
        }
        return Optional.empty();
    }

    public Optional<Technician> findByUserId(String userId) throws ExecutionException, InterruptedException {
        var querySnapshot = techniciansCollection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .get();
        
        if (!querySnapshot.isEmpty()) {
            return Optional.of(querySnapshot.getDocuments().get(0).toObject(Technician.class));
        }
        return Optional.empty();
    }

    public List<Technician> findAll() throws ExecutionException, InterruptedException {
        return techniciansCollection
                .whereEqualTo("available", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .get()
                .toObjects(Technician.class);
    }

    public List<Technician> findBySpecialty(String specialty) throws ExecutionException, InterruptedException {
        return techniciansCollection
                .whereEqualTo("available", true)
                .get()
                .get()
                .toObjects(Technician.class)
                .stream()
                .filter(technician -> technician.getSpecialties().contains(specialty))
                .collect(Collectors.toList());
    }

    public List<Technician> findByLocation(String location) throws ExecutionException, InterruptedException {
        return techniciansCollection
                .whereEqualTo("available", true)
                .whereEqualTo("location", location)
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .get()
                .toObjects(Technician.class);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        techniciansCollection.document(id).delete().get();
    }
}