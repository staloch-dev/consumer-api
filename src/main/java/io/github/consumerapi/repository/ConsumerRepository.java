package io.github.consumerapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.consumerapi.domain.Consumer;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {

    Optional<Consumer> findByCpf(String cpf);

    List<Consumer> findByFullNameContainingIgnoreCase(String name);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

}
