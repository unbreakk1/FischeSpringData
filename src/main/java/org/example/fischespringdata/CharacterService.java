package org.example.fischespringdata;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService
{
    private final AsterixCharacters asterixRepo;

    public CharacterService(AsterixCharacters asterixRepo)
    {
        this.asterixRepo = asterixRepo;
    }

    public List<Character> getAllCharacters()
    {
        return asterixRepo.findAll();
    }

    public Character addCharacter(Character character)
    {
        return asterixRepo.save(character);
    }

    public Character getCharacterById(String id)
    {
        return asterixRepo.findById(id).orElse(null);
    }

}
