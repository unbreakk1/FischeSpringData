package org.example.fischespringdata;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Character updateCharacter(String id, CharacterUpdateDTO dto) throws Exception
    {
        // Fetch the existing character by its id
        Optional<Character> optionalCharacter = asterixRepo.findById(id);
        if (optionalCharacter.isEmpty()) {
            throw new Exception("Character not found");
        }

        Character existingCharacter = optionalCharacter.get();


        Character updatedCharacter = new Character
        (
                existingCharacter.id(),
                dto.getName() != null ? dto.getName() : existingCharacter.name(), // Update name if provided, otherwise keep the existing
                dto.getAge() != null ? dto.getAge() : existingCharacter.age(),    // Update age if provided
                dto.getProfession() != null ? dto.getProfession() : existingCharacter.profession() // Update profession if provided
        );

        // Save the new record back to the repository
        return asterixRepo.save(updatedCharacter);
    }


    public void deleteCharacterById(String id)
    {
        asterixRepo.deleteById(id);
    }
}
