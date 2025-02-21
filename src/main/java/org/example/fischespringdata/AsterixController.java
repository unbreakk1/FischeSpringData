package org.example.fischespringdata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asterix")
public class AsterixController
{

    private final CharacterService characterService;

    public AsterixController(CharacterService characterService)
    {
        this.characterService = characterService;
    }

    @GetMapping("/characters")
    public List<Character> getCharacters()
    {
        return characterService.getAllCharacters();
    }

    @PostMapping("/characters")
    public ResponseEntity<?> addCharacter(@RequestBody CharacterDTO characterDTO)
    {
        if (characterDTO.getName() == null || characterDTO.getName().isEmpty())

            return ResponseEntity.badRequest().body("Character name is required");

        String randomId = java.util.UUID.randomUUID().toString();

        Character character = new Character
        (
                randomId,
                characterDTO.getName(),
                characterDTO.getAge(),
                characterDTO.getProfession()
        );

        try
        {
            Character savedCharacter = characterService.addCharacter(character);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCharacter);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save character");
        }

    }



}
