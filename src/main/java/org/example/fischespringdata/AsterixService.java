package org.example.fischespringdata;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsterixService
{
    private final AsterixCharacters asterixRepo;
    private final IdService idService;

    public AsterixService(AsterixCharacters asterixRepo, IdService idService)
    {
        this.asterixRepo = asterixRepo;
        this.idService = idService;
    }

    public List<Character> getAllCharacters()
    {
        return asterixRepo.findAll();
    }

    public Character addCharacter(Character characterWithoutId)
    {
        // Generate a random ID for the new character
        String randomId = idService.randomId();

        // Build the complete Character object (including the generated ID)
        Character newCharacter = new Character(
                randomId,
                characterWithoutId.name(),
                characterWithoutId.age(),
                characterWithoutId.profession()
        );

        // Save the character to the repository
        return asterixRepo.save(newCharacter);
    }


    public Character getCharacterById(String id)
    {
        return asterixRepo.findById(id).orElse(null);
    }

    public void deleteCharacterById(String id)
    {
        asterixRepo.deleteById(id);
    }
}
