package io.github.consumerapi.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.consumerapi.domain.Consumer;
import io.github.consumerapi.dto.ConsumerRequestDTO;
import io.github.consumerapi.enums.Sex;
import io.github.consumerapi.repository.ConsumerRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ConsumerController - Testes de Integração")
public class ConsumerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        consumerRepository.deleteAll();
    }

    private Consumer buildAndSaveConsumer() {
        Consumer consumer = new Consumer();
        consumer.setCpf("12345678901");
        consumer.setFullName("Johnny Test");
        consumer.setBirthDate(LocalDate.of(1994, 6, 21));
        consumer.setSex(Sex.MASCULINO);
        consumer.setAddress("Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP");
        consumer.setPhone("1134567890");
        consumer.setEmail("johnny@porkbelly.com");
        return consumerRepository.save(consumer);
    }

    private String buildRequestBody(String cpf, String fullName, String email) throws Exception {
        return objectMapper.writeValueAsString(new ConsumerRequestDTO(
                cpf,
                fullName,
                LocalDate.of(1994, 6, 21),
                Sex.MASCULINO,
                "Rua Porkbelly, 11, Casa dos Test, Porkbelly, São Paulo - SP",
                "1134567890",
                email
        ));
    }

    @Test
    @DisplayName("Deve criar consumer e retornar 201 Created")
    void shouldCreateConsumerAndReturn201() throws Exception {
        String body = buildRequestBody("12345678901", "Johnny Test", "johnny@porkbelly.com");

        mockMvc.perform(post("/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.fullName").value("Johnny Test"))
                .andExpect(jsonPath("$.email").value("johnny@porkbelly.com"));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando CPF já está cadastrado")
    void shouldReturn409WhenCpfAlreadyExists() throws Exception {
        buildAndSaveConsumer();
        String body = buildRequestBody("12345678901", "Outro Nome", "outro@email.com");

        mockMvc.perform(post("/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando e-mail já está cadastrado")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        buildAndSaveConsumer();
        String body = buildRequestBody("98765432100", "Outro Nome", "johnny@porkbelly.com");

        mockMvc.perform(post("/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando CPF é inválido")
    void shouldReturn400WhenCpfIsInvalid() throws Exception {
        String body = buildRequestBody("123", "Johnny Test", "johnny@porkbelly.com");

        mockMvc.perform(post("/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando e-mail é inválido")
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        String body = buildRequestBody("12345678901", "Johnny Test", "emailinvalido");

        mockMvc.perform(post("/consumers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e consumer quando ID existe")
    void shouldReturn200WhenConsumerExistsById() throws Exception {
        Consumer saved = buildAndSaveConsumer();

        mockMvc.perform(get("/consumers/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.fullName").value("Johnny Test"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando ID não existe")
    void shouldReturn404WhenConsumerNotFoundById() throws Exception {
        mockMvc.perform(get("/consumers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e consumer quando CPF existe")
    void shouldReturn200WhenConsumerExistsByCpf() throws Exception {
        buildAndSaveConsumer();

        mockMvc.perform(get("/consumers/cpf/{cpf}", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.fullName").value("Johnny Test"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando CPF não existe")
    void shouldReturn404WhenConsumerNotFoundByCpf() throws Exception {
        mockMvc.perform(get("/consumers/cpf/{cpf}", "00000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e lista de consumers")
    void shouldReturn200WithListOfConsumers() throws Exception {
        buildAndSaveConsumer();

        mockMvc.perform(get("/consumers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e lista vazia quando não há consumers")
    void shouldReturn200WithEmptyList() throws Exception {
        mockMvc.perform(get("/consumers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar 200 OK filtrando consumers por nome")
    void shouldReturn200FilteringByName() throws Exception {
        buildAndSaveConsumer();

        mockMvc.perform(get("/consumers").param("name", "Johnny"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Johnny Test"));
    }

    @Test
    @DisplayName("Deve atualizar consumer e retornar 200 OK")
    void shouldUpdateConsumerAndReturn200() throws Exception {
        Consumer saved = buildAndSaveConsumer();
        String body = buildRequestBody("12345678901", "Johnny Atualizado", "johnny.novo@porkbelly.com");

        mockMvc.perform(put("/consumers/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Johnny Atualizado"))
                .andExpect(jsonPath("$.email").value("johnny.novo@porkbelly.com"));
    }

    @Test
    @DisplayName("Deve permitir atualizar consumer mantendo o mesmo CPF e e-mail que já são dele")
    void shouldAllowUpdateKeepingOwnCpfAndEmail() throws Exception {
        Consumer saved = buildAndSaveConsumer();
        String body = buildRequestBody("12345678901", "Johnny Test Atualizado", "johnny@porkbelly.com");

        mockMvc.perform(put("/consumers/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.fullName").value("Johnny Test Atualizado"))
                .andExpect(jsonPath("$.email").value("johnny@porkbelly.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao atualizar consumer inexistente")
    void shouldReturn404WhenUpdatingNonExistentConsumer() throws Exception {
        String body = buildRequestBody("12345678901", "Johnny Test", "johnny@porkbelly.com");

        mockMvc.perform(put("/consumers/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao atualizar com CPF já pertencente a outro consumer")
    void shouldReturn409WhenUpdatingWithExistingCpf() throws Exception {
        buildAndSaveConsumer();

        Consumer consumer2 = new Consumer();
        consumer2.setCpf("98765432100");
        consumer2.setFullName("Fabio Akita");
        consumer2.setBirthDate(LocalDate.of(1977, 3, 10));
        consumer2.setSex(Sex.MASCULINO);
        consumer2.setAddress("Rua Codeminer, 42, Vila Madalena, São Paulo - SP");
        consumer2.setPhone("1198765432");
        consumer2.setEmail("akita@codeminer42.com");
        Consumer saved2 = consumerRepository.save(consumer2);

        String body = buildRequestBody("12345678901", "Fabio Akita", "akita@codeminer42.com");

        mockMvc.perform(put("/consumers/{id}", saved2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao atualizar com e-mail já pertencente a outro consumer")
    void shouldReturn409WhenUpdatingWithExistingEmail() throws Exception {
        buildAndSaveConsumer();

        Consumer consumer2 = new Consumer();
        consumer2.setCpf("98765432100");
        consumer2.setFullName("Fabio Akita");
        consumer2.setBirthDate(LocalDate.of(1977, 3, 10));
        consumer2.setSex(Sex.MASCULINO);
        consumer2.setAddress("Rua Codeminer, 42, Vila Madalena, São Paulo - SP");
        consumer2.setPhone("1198765432");
        consumer2.setEmail("akita@codeminer42.com");
        Consumer saved2 = consumerRepository.save(consumer2);

        String body = buildRequestBody("98765432100", "Fabio Akita", "johnny@porkbelly.com");

        mockMvc.perform(put("/consumers/{id}", saved2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve deletar consumer e retornar 204 No Content")
    void shouldDeleteConsumerAndReturn204() throws Exception {
        Consumer saved = buildAndSaveConsumer();

        mockMvc.perform(delete("/consumers/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao deletar consumer inexistente")
    void shouldReturn404WhenDeletingNonExistentConsumer() throws Exception {
        mockMvc.perform(delete("/consumers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

}
