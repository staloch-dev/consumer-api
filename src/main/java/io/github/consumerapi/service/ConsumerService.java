package io.github.consumerapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.github.consumerapi.domain.Consumer;
import io.github.consumerapi.dto.ConsumerRequestDTO;
import io.github.consumerapi.dto.ConsumerResponseDTO;
import io.github.consumerapi.exception.DuplicateResourceException;
import io.github.consumerapi.exception.ResourceNotFoundException;
import io.github.consumerapi.mapper.ConsumerMapper;
import io.github.consumerapi.repository.ConsumerRepository;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ConsumerRepository consumerRepository;
    private final ConsumerMapper consumerMapper;

    @Transactional
    public ConsumerResponseDTO create(ConsumerRequestDTO request) {
        if (consumerRepository.existsByCpf(request.cpf())) {
            throw new DuplicateResourceException(
                    "O CPF '" + request.cpf() + "' já está cadastrado.");
        }
        if (consumerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "O e-mail '" + request.email() + "' já está cadastrado.");
        }

        Consumer consumer = consumerMapper.toEntity(request);
        Consumer saved = consumerRepository.save(consumer);
        return consumerMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public ConsumerResponseDTO findById(Long id) {
        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente com ID " + id + " não encontrado."));
        return consumerMapper.toResponseDTO(consumer);
    }

    @Transactional(readOnly = true)
    public ConsumerResponseDTO findByCpf(String cpf) {
        Consumer consumer = consumerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente com CPF '" + cpf + "' não encontrado."));
        return consumerMapper.toResponseDTO(consumer);
    }

    @Transactional(readOnly = true)
    public List<ConsumerResponseDTO> findByName(String name) {
        return consumerRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(consumerMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsumerResponseDTO> findAll() {
        return consumerRepository.findAll()
                .stream()
                .map(consumerMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ConsumerResponseDTO update(Long id, ConsumerRequestDTO request) {
        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente com ID " + id + " não encontrado."));

        if (!consumer.getCpf().equals(request.cpf()) &&
                consumerRepository.existsByCpf(request.cpf())) {
            throw new DuplicateResourceException(
                    "O CPF '" + request.cpf() + "' já está cadastrado.");
        }

        if (!consumer.getEmail().equals(request.email()) &&
                consumerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "O e-mail '" + request.email() + "' já está cadastrado.");
        }

        consumer.setCpf(request.cpf());
        consumer.setFullName(request.fullName());
        consumer.setBirthDate(request.birthDate());
        consumer.setSex(request.sex());
        consumer.setAddress(request.address());
        consumer.setPhone(request.phone());
        consumer.setEmail(request.email());

        return consumerMapper.toResponseDTO(consumer);
    }

    @Transactional
    public void delete(Long id) {
        Consumer consumer = consumerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente com ID " + id + " não encontrado."));
        consumerRepository.delete(consumer);
    }

}
