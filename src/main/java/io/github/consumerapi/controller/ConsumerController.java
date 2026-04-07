package io.github.consumerapi.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.github.consumerapi.dto.ConsumerRequestDTO;
import io.github.consumerapi.dto.ConsumerResponseDTO;
import io.github.consumerapi.service.ConsumerService;

@RestController
@RequestMapping("/consumers")
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;

    @PostMapping
    public ResponseEntity<ConsumerResponseDTO> create(
            @Valid @RequestBody ConsumerRequestDTO request) {
        ConsumerResponseDTO response = consumerService.create(request);
        return ResponseEntity
                .created(URI.create("/consumers/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsumerResponseDTO> findById(
            @PathVariable Long id) {
        ConsumerResponseDTO response = consumerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ConsumerResponseDTO> findByCpf(
            @PathVariable String cpf) {
        ConsumerResponseDTO response = consumerService.findByCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConsumerResponseDTO>> findAll(
            @RequestParam(required = false) String name) {
        List<ConsumerResponseDTO> response = (name != null && !name.isBlank())
                ? consumerService.findByName(name)
                : consumerService.findAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsumerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ConsumerRequestDTO request) {
        ConsumerResponseDTO response = consumerService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consumerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
