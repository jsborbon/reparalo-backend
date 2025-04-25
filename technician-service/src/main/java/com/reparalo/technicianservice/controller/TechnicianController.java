package com.reparalo.technicianservice.controller;

import com.reparalo.technicianservice.dto.TechnicianDTO;
import com.reparalo.technicianservice.service.TechnicianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    @Autowired
    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping
    public ResponseEntity<TechnicianDTO> createTechnician(
            @Valid @RequestBody TechnicianDTO technicianDTO,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        TechnicianDTO createdTechnician = technicianService.createTechnician(technicianDTO, userId);
        return new ResponseEntity<>(createdTechnician, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianDTO> getTechnicianById(@PathVariable String id) throws ExecutionException, InterruptedException {
        try {
            TechnicianDTO technicianDTO = technicianService.getTechnicianById(id);
            return ResponseEntity.ok(technicianDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<TechnicianDTO> getMyProfile(
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        try {
            TechnicianDTO technicianDTO = technicianService.getTechnicianByUserId(userId);
            return ResponseEntity.ok(technicianDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TechnicianDTO>> getAllTechnicians() throws ExecutionException, InterruptedException {
        List<TechnicianDTO> technicians = technicianService.getAllTechnicians();
        return ResponseEntity.ok(technicians);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<TechnicianDTO>> getTechniciansBySpecialty(
            @PathVariable String specialty) throws ExecutionException, InterruptedException {
        List<TechnicianDTO> technicians = technicianService.getTechniciansBySpecialty(specialty);
        return ResponseEntity.ok(technicians);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<TechnicianDTO>> getTechniciansByLocation(
            @PathVariable String location) throws ExecutionException, InterruptedException {
        List<TechnicianDTO> technicians = technicianService.getTechniciansByLocation(location);
        return ResponseEntity.ok(technicians);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicianDTO> updateTechnician(
            @PathVariable String id,
            @Valid @RequestBody TechnicianDTO technicianDTO,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        try {
            TechnicianDTO updatedTechnician = technicianService.updateTechnician(id, technicianDTO, userId);
            return ResponseEntity.ok(updatedTechnician);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken principal) throws ExecutionException, InterruptedException {
        String userId = principal.getName();
        try {
            technicianService.deleteTechnician(id, userId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}