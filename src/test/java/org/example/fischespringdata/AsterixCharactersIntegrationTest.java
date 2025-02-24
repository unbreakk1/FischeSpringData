package org.example.fischespringdata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AsterixCharactersIntegrationTest
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AsterixService service;

    @BeforeEach
    void setUp() {
        // Clear the database and populate it with test characters
        service.deleteCharacterById("1");
        service.deleteCharacterById("2");
        service.addCharacter(new Character("1", "Max", 10, "Warrior"));
        service.addCharacter(new Character("2", "Obelix", 35, "Builder"));
    }

    @Test
    void shouldGetAllCharacters() throws Exception
    {
        mockMvc.perform(get("/asterix/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Verifies 2 characters are returned
                .andExpect(jsonPath("$[0].name").value("Max"))
                .andExpect(jsonPath("$[1].name").value("Obelix"));
    }

    @Test
    void shouldGetCharacterById() throws Exception
    {
        mockMvc.perform(get("/asterix/characters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.age").value(10))
                .andExpect(jsonPath("$.profession").value("Warrior"));
    }

    @Test
    void shouldReturnNotFoundForNonexistentCharacter() throws Exception
    {
        mockMvc.perform(get("/asterix/characters/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Character with ID 999 not found"));
    }

    @Test
    void shouldAddNewCharacter() throws Exception
    {
        String newCharacterJson = """
            {
                "name": "Vitalstatistix",
                "age": 50,
                "profession": "Chief"
            }
        """;

        mockMvc.perform(post("/asterix/characters")
                        .contentType(APPLICATION_JSON)
                        .content(newCharacterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vitalstatistix"))
                .andExpect(jsonPath("$.age").value(50))
                .andExpect(jsonPath("$.profession").value("Chief"));
    }

    @Test
    void shouldNotAddCharacterWithInvalidData() throws Exception
    {
        String invalidCharacterJson = """
            {
                "age": 10,
                "profession": "Warrior"
            }
        """;

        mockMvc.perform(post("/asterix/characters")
                        .contentType(APPLICATION_JSON)
                        .content(invalidCharacterJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Character name is required"));
    }

    @Test
    void shouldUpdateCharacter() throws Exception
    {
        String updatedCharacterJson = """
            {
                "name": "Updated Max",
                "age": 15,
                "profession": "Hero"
            }
        """;

        mockMvc.perform(put("/asterix/characters/1")
                        .contentType(APPLICATION_JSON)
                        .content(updatedCharacterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Max"))
                .andExpect(jsonPath("$.age").value(15))
                .andExpect(jsonPath("$.profession").value("Hero"));
    }

    @Test
    void shouldNotUpdateNonexistentCharacter() throws Exception
    {
        String updatedCharacterJson = """
            {
                "name": "Nonexistent",
                "age": 20,
                "profession": "Unknown"
            }
        """;

        mockMvc.perform(put("/asterix/characters/999")
                        .contentType(APPLICATION_JSON)
                        .content(updatedCharacterJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to update character"));
    }

    @Test
    void shouldDeleteCharacter() throws Exception
    {
        mockMvc.perform(delete("/asterix/characters/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/asterix/characters/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteNonexistentCharacter() throws Exception
    {
        mockMvc.perform(delete("/asterix/characters/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Character with ID 999 not found"));
    }

    @Test
    void shouldGetFilteredCharactersByAge() throws Exception
    {
        mockMvc.perform(get("/asterix/characters?age=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Max"));
    }
}

