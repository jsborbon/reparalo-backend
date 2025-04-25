package com.reparalo.technicianservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Technician {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String profilePictureUrl;
    private List<String> specialties;
    private String location;
    private Double rating;
    private Integer reviewCount;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}