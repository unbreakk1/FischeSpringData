package org.example.fischespringdata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = FischeSpringDataApplication.class)
@AutoConfigureMockMvc
@Commit
public class AsterixCharactersIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AsterixService service;

    @BeforeEach
    void setUp()
    {
        service.deleteCharacterById("1");
        service.deleteCharacterById("2");

        Character character1 = new Character("1", "Max", 10, "Warrior");
        Character character2 = new Character("2", "Obelix", 35, "Builder");

        service.addCharacter(character1);
        service.addCharacter(character2);

        System.out.println("Added characters:");
        System.out.println(service.getCharacterById("1"));
        System.out.println(service.getCharacterById("2"));
    }

    @Test
    void shouldGetAllCharacters() throws Exception
    {
        mockMvc.perform(get("/asterix/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Max')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Obelix')]").exists());

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

        var characters = service.getAllCharacters();

        assert characters.size() == 3 :
                "Expected 3 characters in store, but found " + characters.size();

        assert characters.stream().anyMatch(c -> c.name().equals("Vitalstatistix")) :
                "Character 'Vitalstatistix' was not found in the list";
    }

    @Test
    void shouldDeleteCharacter() throws Exception
    {
        mockMvc.perform(delete("/asterix/characters/1"))
                .andExpect(status().isNoContent());

        var deletedCharacter = service.getCharacterById("1");
        assert deletedCharacter == null;
    }

    @Test
    void shouldNotRetrieveDeletedCharacter() throws Exception
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
}


