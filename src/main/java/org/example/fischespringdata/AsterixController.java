package org.example.fischespringdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asterix")
public class AsterixController
{
    private final AsterixService asterixService;

    public AsterixController(AsterixService asterixService)
    {
        this.asterixService = asterixService;
    }

    // Get all characters
    @GetMapping("/characters")
    public ResponseEntity<?> getCharacters(@RequestParam(value = "age", required = false) Integer age) {
        if (age != null)
        {
            // Perform filtering if age is provided
            List<Character> filteredCharacters = asterixService.getCharactersByMaxAge(age);
            return ResponseEntity.ok(filteredCharacters);
        }

        // Default behavior if age is null
        List<Character> allCharacters = asterixService.getAllCharacters();
        return ResponseEntity.ok(allCharacters);
    }

    // Get a character by ID
    @GetMapping("/characters/{id}")
    public ResponseEntity<?> getCharacterById(@PathVariable String id)
    {
        Character character = asterixService.getCharacterById(id);

        if (character != null)
            return ResponseEntity.ok(character);
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Character with ID " + id + " not found");
    }

    // Add a new character
    @PostMapping("/characters")
    public ResponseEntity<?> addCharacter(@RequestBody CharacterDTO characterDTO)
    {
        if (characterDTO.getName() == null || characterDTO.getName().isEmpty())
            return ResponseEntity.badRequest().body("Character name is required");

        Character characterWithoutId = new Character
                (
                        null,
                        characterDTO.getName(),
                        characterDTO.getAge(),
                        characterDTO.getProfession()
                );

        try
        {
            Character savedCharacter = asterixService.addCharacter(characterWithoutId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCharacter);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save character");
        }
    }

    // Delete a character by ID
    @DeleteMapping("/characters/{id}")
    public ResponseEntity<?> deleteCharacterById(@PathVariable String id)
    {
        Character character = asterixService.getCharacterById(id);

        if (character != null)
        {
            asterixService.deleteCharacterById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Character with ID " + id + " was deleted");
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Character with ID " + id + " not found");
    }

    // Update a character by ID
    @PutMapping("/characters/{id}")
    public ResponseEntity<?> updateCharacter(@PathVariable String id, @RequestBody CharacterDTO characterDTO)
    {
        Character updatedCharacter = new Character
        (
                id,
                characterDTO.getName(),
                characterDTO.getAge(),
                characterDTO.getProfession()
        );
        // Save or update character
        Character savedCharacter = asterixService.addCharacter(updatedCharacter);

        return ResponseEntity.ok(savedCharacter);
    }

}

