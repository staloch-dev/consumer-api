package io.github.consumerapi.mapper;

import org.springframework.stereotype.Component;

import io.github.consumerapi.domain.Consumer;
import io.github.consumerapi.dto.ConsumerRequestDTO;
import io.github.consumerapi.dto.ConsumerResponseDTO;

@Component
public class ConsumerMapper {

    public Consumer toEntity(ConsumerRequestDTO dto) {
        Consumer consumer = new Consumer();
        consumer.setCpf(dto.cpf());
        consumer.setFullName(dto.fullName());
        consumer.setBirthDate(dto.birthDate());
        consumer.setSex(dto.sex());
        consumer.setAddress(dto.address());
        consumer.setPhone(dto.phone());
        consumer.setEmail(dto.email());
        return consumer;
    }

    public ConsumerResponseDTO toResponseDTO(Consumer consumer) {
        return new ConsumerResponseDTO(
                consumer.getId(),
                consumer.getCpf(),
                consumer.getFullName(),
                consumer.getBirthDate(),
                consumer.getSex(),
                consumer.getAddress(),
                consumer.getPhone(),
                consumer.getEmail()
        );
    }

}
