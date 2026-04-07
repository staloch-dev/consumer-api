package io.github.consumerapi.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.github.consumerapi.enums.Sex;

public record ConsumerRequestDTO(

        @NotBlank(message = "O CPF é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos.")
        String cpf,

        @NotBlank(message = "O nome completo é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String fullName,

        @NotNull(message = "A data de nascimento é obrigatória.")
        @Past(message = "A data de nascimento deve ser no passado.")
        LocalDate birthDate,

        @NotNull(message = "O sexo é obrigatório.")
        Sex sex,

        @NotBlank(message = "O endereço é obrigatório.")
        @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres.")
        String address,

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos.")
        @Size(max = 11, message = "O telefone deve ter no máximo 11 caracteres.")
        String phone,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ter um formato válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String email

) {}
