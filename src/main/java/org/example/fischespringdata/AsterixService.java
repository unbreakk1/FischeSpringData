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

    public List<Character> getCharactersByMaxAge(int maxAge)
    {
        List<Character> allCharacters = asterixRepo.findAll();

        return allCharacters.stream().filter(character -> character.age() <= maxAge)
                .toList();
    }

    public Character addCharacter(Character characterWithoutId)
    {
        String randomId = idService.randomId();

        // Build the complete Character object (including the generated ID)
        Character newCharacter = new Character(
                randomId,
                characterWithoutId.name(),
                characterWithoutId.age(),
                characterWithoutId.profession()
        );

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
