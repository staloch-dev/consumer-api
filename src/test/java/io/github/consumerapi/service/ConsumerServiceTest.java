package io.github.consumerapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.consumerapi.domain.Consumer;
import io.github.consumerapi.dto.ConsumerRequestDTO;
import io.github.consumerapi.dto.ConsumerResponseDTO;
import io.github.consumerapi.enums.Sex;
import io.github.consumerapi.exception.DuplicateResourceException;
import io.github.consumerapi.exception.ResourceNotFoundException;
import io.github.consumerapi.mapper.ConsumerMapper;
import io.github.consumerapi.repository.ConsumerRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsumerService - Testes Unitários")
public class ConsumerServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private ConsumerMapper consumerMapper;

    @InjectMocks
    private ConsumerService consumerService;

    private Consumer consumer;
    private ConsumerRequestDTO requestDTO;
    private ConsumerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ConsumerRequestDTO(
                "12345678901",
                "Johnny Test",
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP",
                "1134567890",
                "johnny@porkbelly.com"
        );

        consumer = new Consumer();
        consumer.setId(1L);
        consumer.setCpf("12345678901");
        consumer.setFullName("Johnny Test");
        consumer.setBirthDate(LocalDate.of(1994, 6, 21));
        consumer.setSex(Sex.MASCULINO);
        consumer.setAddress("Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP");
        consumer.setPhone("1134567890");
        consumer.setEmail("johnny@porkbelly.com");

        responseDTO = new ConsumerResponseDTO(
                1L,
                "12345678901",
                "Johnny Test",
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP",
                "1134567890",
                "johnny@porkbelly.com"
        );
    }

    @Test
    @DisplayName("Deve criar consumer e retornar ConsumerResponseDTO com sucesso")
    void shouldCreateConsumerAndReturnResponseDTOSuccessfully() {

        when(consumerRepository.existsByCpf(anyString()))
                .thenReturn(false);
        when(consumerRepository.existsByEmail(anyString()))
                .thenReturn(false);
        when(consumerMapper.toEntity(any(ConsumerRequestDTO.class)))
                .thenReturn(consumer);
        when(consumerRepository.save(any(Consumer.class)))
                .thenReturn(consumer);
        when(consumerMapper.toResponseDTO(any(Consumer.class)))
                .thenReturn(responseDTO);

        ConsumerResponseDTO result = consumerService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fullName()).isEqualTo("Johnny Test");
        assertThat(result.cpf()).isEqualTo("12345678901");

        verify(consumerRepository, times(1)).save(any(Consumer.class));
        verify(consumerMapper, times(1)).toEntity(any(ConsumerRequestDTO.class));
        verify(consumerMapper, times(1)).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException quando CPF já está cadastrado")
    void shouldThrowDuplicateResourceExceptionWhenCpfAlreadyExists() {

        when(consumerRepository.existsByCpf(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> consumerService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("12345678901");

        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException quando e-mail já está cadastrado")
    void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {

        when(consumerRepository.existsByCpf(anyString()))
                .thenReturn(false);
        when(consumerRepository.existsByEmail(anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> consumerService.create(requestDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("johnny@porkbelly.com");

        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve retornar ConsumerResponseDTO quando consumer existe")
    void shouldReturnResponseDTOWhenConsumerExists() {

        when(consumerRepository.findById(1L))
                .thenReturn(Optional.of(consumer));
        when(consumerMapper.toResponseDTO(any(Consumer.class)))
                .thenReturn(responseDTO);

        ConsumerResponseDTO result = consumerService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fullName()).isEqualTo("Johnny Test");

        verify(consumerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando consumer não existe")
    void shouldThrowResourceNotFoundExceptionWhenConsumerNotExists() {

        when(consumerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(consumerMapper, never()).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve retornar ConsumerResponseDTO quando CPF existe")
    void shouldReturnResponseDTOWhenCpfExists() {

        when(consumerRepository.findByCpf("12345678901"))
                .thenReturn(Optional.of(consumer));
        when(consumerMapper.toResponseDTO(any(Consumer.class)))
                .thenReturn(responseDTO);

        ConsumerResponseDTO result = consumerService.findByCpf("12345678901");

        assertThat(result).isNotNull();
        assertThat(result.cpf()).isEqualTo("12345678901");

        verify(consumerRepository, times(1)).findByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando CPF não existe")
    void shouldThrowResourceNotFoundExceptionWhenCpfNotExists() {

        when(consumerRepository.findByCpf(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumerService.findByCpf("00000000000"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("00000000000");

        verify(consumerMapper, never()).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve retornar consumers quando nome corresponde à busca")
    void shouldReturnConsumersWhenNameMatches() {

        when(consumerRepository.findByFullNameContainingIgnoreCase("Johnny"))
                .thenReturn(List.of(consumer));
        when(consumerMapper.toResponseDTO(consumer))
                .thenReturn(responseDTO);

        List<ConsumerResponseDTO> result = consumerService.findByName("Johnny");

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).fullName()).isEqualTo("Johnny Test");

        verify(consumerRepository, times(1)).findByFullNameContainingIgnoreCase("Johnny");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum consumer corresponde ao nome")
    void shouldReturnEmptyListWhenNoConsumerMatchesName() {

        when(consumerRepository.findByFullNameContainingIgnoreCase(anyString()))
                .thenReturn(List.of());

        List<ConsumerResponseDTO> result = consumerService.findByName("Inexistente");

        assertThat(result).isNotNull().isEmpty();

        verify(consumerMapper, never()).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve retornar lista de ConsumerResponseDTO quando existem consumers")
    void shouldReturnListOfResponseDTOsWhenConsumersExist() {

        Consumer consumer2 = new Consumer();
        consumer2.setId(2L);
        consumer2.setFullName("Fabio Akita");

        ConsumerResponseDTO responseDTO2 = new ConsumerResponseDTO(
                2L, "98765432100", "Fabio Akita",
                LocalDate.of(1977, 3, 10), Sex.MASCULINO,
                "Rua Codeminer, 42, Vila Madalena, São Paulo - SP",
                "1198765432", "akita@codeminer42.com"
        );

        when(consumerRepository.findAll())
                .thenReturn(List.of(consumer, consumer2));
        when(consumerMapper.toResponseDTO(consumer))
                .thenReturn(responseDTO);
        when(consumerMapper.toResponseDTO(consumer2))
                .thenReturn(responseDTO2);

        List<ConsumerResponseDTO> result = consumerService.findAll();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).fullName()).isEqualTo("Johnny Test");
        assertThat(result.get(1).fullName()).isEqualTo("Fabio Akita");

        verify(consumerMapper, times(2)).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existem consumers")
    void shouldReturnEmptyListWhenNoConsumersExist() {

        when(consumerRepository.findAll()).thenReturn(List.of());

        List<ConsumerResponseDTO> result = consumerService.findAll();

        assertThat(result).isNotNull().isEmpty();

        verify(consumerMapper, never()).toResponseDTO(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve atualizar consumer e retornar ConsumerResponseDTO com sucesso")
    void shouldUpdateConsumerAndReturnResponseDTOSuccessfully() {

        ConsumerRequestDTO updateRequest = new ConsumerRequestDTO(
                "12345678901",
                "Johnny Test Atualizado",
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Praia de Ipanema, 462, Ipanema, Rio de Janeiro - RJ",
                "2199998888",
                "johnny.novo@porkbelly.com"
        );

        ConsumerResponseDTO updatedResponse = new ConsumerResponseDTO(
                1L, "12345678901", "Johnny Test Atualizado",
                LocalDate.of(1994, 6, 21), Sex.MASCULINO,
                "Praia de Ipanema, 462, Ipanema, Rio de Janeiro - RJ",
                "2199998888", "johnny.novo@porkbelly.com"
        );

        when(consumerRepository.findById(1L))
                .thenReturn(Optional.of(consumer));
        when(consumerMapper.toResponseDTO(any(Consumer.class)))
                .thenReturn(updatedResponse);

        ConsumerResponseDTO result = consumerService.update(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Johnny Test Atualizado");

        verify(consumerRepository, times(1)).findById(1L);
        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao atualizar com CPF já existente")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingWithExistingCpf() {

        ConsumerRequestDTO updateRequest = new ConsumerRequestDTO(
                "99999999999",
                "Johnny Test",
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP",
                "1134567890",
                "johnny@porkbelly.com"
        );

        when(consumerRepository.findById(1L))
                .thenReturn(Optional.of(consumer));
        when(consumerRepository.existsByCpf("99999999999"))
                .thenReturn(true);

        assertThatThrownBy(() -> consumerService.update(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("99999999999");

        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao atualizar com e-mail já existente")
    void shouldThrowDuplicateResourceExceptionWhenUpdatingWithExistingEmail() {

        ConsumerRequestDTO updateRequest = new ConsumerRequestDTO(
                "12345678901",
                "Johnny Test",
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP",
                "1134567890",
                "outro@email.com"
        );

        when(consumerRepository.findById(1L))
                .thenReturn(Optional.of(consumer));
        when(consumerRepository.existsByEmail("outro@email.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> consumerService.update(1L, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("outro@email.com");

        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar consumer inexistente")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentConsumer() {

        when(consumerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumerService.update(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(consumerRepository, never()).save(any(Consumer.class));
    }

    @Test
    @DisplayName("Deve deletar consumer com sucesso")
    void shouldDeleteConsumerSuccessfully() {

        when(consumerRepository.findById(1L))
                .thenReturn(Optional.of(consumer));

        consumerService.delete(1L);

        verify(consumerRepository, times(1)).delete(consumer);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar consumer inexistente")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentConsumer() {

        when(consumerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumerService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(consumerRepository, never()).delete(any(Consumer.class));
    }

}
