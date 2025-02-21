package org.example.fischespringdata;

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
    public Character addCharacter(@RequestBody Character character)
    {
        return characterService.addCharacter(character);
    }

}
