package io.github.consumerapi.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.github.consumerapi.enums.Sex;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "consumers")
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false, unique = true)
    private String cpf;

    @Column(length = 150, nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 13)
    private Sex sex;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 11)
    private String phone;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

}
