package com.reparalo.technicianservice.service;

import com.reparalo.technicianservice.dto.TechnicianDTO;
import com.reparalo.technicianservice.model.Technician;
import com.reparalo.technicianservice.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    @Autowired
    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    public TechnicianDTO createTechnician(TechnicianDTO technicianDTO, String userId) throws ExecutionException, InterruptedException {
        // Verificar si el usuario ya tiene un perfil de técnico
        technicianRepository.findByUserId(userId).ifPresent(technician -> {
            throw new IllegalArgumentException("Ya tienes un perfil de técnico registrado");
        });
        
        Technician technician = mapToEntity(technicianDTO);
        technician.setUserId(userId);
        
        Technician savedTechnician = technicianRepository.save(technician);
        return mapToDTO(savedTechnician);
    }

    public TechnicianDTO getTechnicianById(String id) throws ExecutionException, InterruptedException {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Técnico no encontrado con ID: " + id));
        return mapToDTO(technician);
    }

    public TechnicianDTO getTechnicianByUserId(String userId) throws ExecutionException, InterruptedException {
        Technician technician = technicianRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No tienes un perfil de técnico registrado"));
        return mapToDTO(technician);
    }

    public List<TechnicianDTO> getAllTechnicians() throws ExecutionException, InterruptedException {
        List<Technician> technicians = technicianRepository.findAll();
        return technicians.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TechnicianDTO> getTechniciansBySpecialty(String specialty) throws ExecutionException, InterruptedException {
        List<Technician> technicians = technicianRepository.findBySpecialty(specialty);
        return technicians.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TechnicianDTO> getTechniciansByLocation(String location) throws ExecutionException, InterruptedException {
        List<Technician> technicians = technicianRepository.findByLocation(location);
        return technicians.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TechnicianDTO updateTechnician(String id, TechnicianDTO technicianDTO, String userId) throws ExecutionException, InterruptedException {
        Technician existingTechnician = technicianRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Técnico no encontrado con ID: " + id));
        
        // Verificar que el usuario es el propietario del perfil
        if (!existingTechnician.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para editar este perfil");
        }
        
        existingTechnician.setName(technicianDTO.getName());
        existingTechnician.setDescription(technicianDTO.getDescription());
        existingTechnician.setProfilePictureUrl(technicianDTO.getProfilePictureUrl());
        existingTechnician.setSpecialties(technicianDTO.getSpecialties());
        existingTechnician.setLocation(technicianDTO.getLocation());
        existingTechnician.setAvailable(technicianDTO.getAvailable());
        
        Technician updatedTechnician = technicianRepository.save(existingTechnician);
        return mapToDTO(updatedTechnician);
    }

    public void deleteTechnician(String id, String userId) throws ExecutionException, InterruptedException {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Técnico no encontrado con ID: " + id));
        
        // Verificar que el usuario es el propietario del perfil
        if (!technician.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este perfil");
        }
        
        technicianRepository.delete(id);
    }

    public void updateTechnicianRating(String id, Double newRating) throws ExecutionException, InterruptedException {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Técnico no encontrado con ID: " + id));
        
        int currentCount = technician.getReviewCount();
        double currentRating = technician.getRating();
        
        // Calcular la nueva calificación promedio
        double updatedRating = ((currentRating * currentCount) + newRating) / (currentCount + 1);
        
        technician.setRating(updatedRating);
        technician.setReviewCount(currentCount + 1);
        
        technicianRepository.save(technician);
    }

    private Technician mapToEntity(TechnicianDTO technicianDTO) {
        return Technician.builder()
                .id(technicianDTO.getId())
                .userId(technicianDTO.getUserId())
                .name(technicianDTO.getName())
                .description(technicianDTO.getDescription())
                .profilePictureUrl(technicianDTO.getProfilePictureUrl())
                .specialties(technicianDTO.getSpecialties())
                .location(technicianDTO.getLocation())
                .rating(technicianDTO.getRating())
                .reviewCount(technicianDTO.getReviewCount())
                .available(technicianDTO.getAvailable())
                .createdAt(technicianDTO.getCreatedAt())
                .updatedAt(technicianDTO.getUpdatedAt())
                .build();
    }

    private TechnicianDTO mapToDTO(Technician technician) {
        return TechnicianDTO.builder()
                .id(technician.getId())
                .userId(technician.getUserId())
                .name(technician.getName())
                .description(technician.getDescription())
                .profilePictureUrl(technician.getProfilePictureUrl())
                .specialties(technician.getSpecialties())
                .location(technician.getLocation())
                .rating(technician.getRating())
                .reviewCount(technician.getReviewCount())
                .available(technician.getAvailable())
                .createdAt(technician.getCreatedAt())
                .updatedAt(technician.getUpdatedAt())
                .build();
    }
}