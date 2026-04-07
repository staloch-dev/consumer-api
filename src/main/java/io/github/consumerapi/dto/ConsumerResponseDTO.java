package io.github.consumerapi.dto;

import java.time.LocalDate;

import io.github.consumerapi.enums.Sex;

public record ConsumerResponseDTO(
        Long id,
        String cpf,
        String fullName,
        LocalDate birthDate,
        Sex sex,
        String address,
        String phone,
        String email
) {}
