package org.example.fischespringdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsterixServiceTest
{
    @Mock
    private AsterixCharacters asterixRepo;

    @Mock
    private IdService idService;

    @InjectMocks
    private AsterixService asterixService;

    @Test
    void findAllCharacters_ShouldReturnListOfCharacters()
    {
        List<Character> mockCharacters = Arrays.asList(
                new Character("1", "Asterix", 35, "Warrior"),
                new Character("2", "Obelix", 30, "Menhir Deliveryman")
        );
        when(asterixRepo.findAll()).thenReturn(mockCharacters);

        List<Character> allCharacters = asterixService.getAllCharacters();

        assertThat(allCharacters).isEqualTo(mockCharacters);
        verify(asterixRepo, times(1)).findAll();
    }

    @Test
    void findById_WhenCharacterExists_ReturnsCharacter()
    {
        String characterId = "1";
        Character mockCharacter = new Character(characterId, "Asterix", 35, "Warrior");
        when(asterixRepo.findById(characterId)).thenReturn(Optional.of(mockCharacter));

        Character foundCharacter = asterixService.getCharacterById(characterId);

        assertThat(foundCharacter).isEqualTo(mockCharacter);
        verify(asterixRepo, times(1)).findById(characterId);
    }

    @Test
    void findById_WhenCharacterDoesNotExist_ReturnsNull()
    {
        String characterId = "1";
        when(asterixRepo.findById(characterId)).thenReturn(Optional.empty());

        Character foundCharacter = asterixService.getCharacterById(characterId);

        assertThat(foundCharacter).isNull();
        verify(asterixRepo, times(1)).findById(characterId);
    }

    @Test
    void updateCharacter_WhenCharacterExists_UpdatesAndReturnsUpdatedCharacter() throws Exception
    {
        String characterId = "1";
        Character existingCharacter = new Character(characterId, "Asterix", 35, "Warrior");
        Character updatedCharacter = new Character(characterId, "Updated Asterix", 36, "Leader");

        CharacterUpdateDTO updateDTO = new CharacterUpdateDTO();
        updateDTO.setName("Updated Asterix");
        updateDTO.setAge(36);
        updateDTO.setProfession("Leader");

        when(asterixRepo.findById(characterId)).thenReturn(Optional.of(existingCharacter));
        when(asterixRepo.save(any(Character.class))).thenReturn(updatedCharacter);

        Character result = asterixService.updateCharacter(characterId, updateDTO);

        assertThat(result).isEqualTo(updatedCharacter);
        verify(asterixRepo, times(1)).findById(characterId);
        verify(asterixRepo, times(1)).save(any(Character.class));
    }

    @Test
    void updateCharacter_WhenCharacterDoesNotExist_ThrowsException()
    {
        String characterId = "1";
        CharacterUpdateDTO updateDTO = new CharacterUpdateDTO();
        updateDTO.setName("Nonexistent Character");

        when(asterixRepo.findById(characterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> asterixService.updateCharacter(characterId, updateDTO))
                .isInstanceOf(Exception.class)
                .hasMessage("Character not found");
        verify(asterixRepo, times(1)).findById(characterId);
        verify(asterixRepo, never()).save(any());
    }

    @Test
    void deleteCharacter_WhenCharacterExists_DeletesCharacter()
    {
        String characterId = "1";

        asterixService.deleteCharacterById(characterId);

        verify(asterixRepo, times(1)).deleteById(characterId);
    }

    @Test
    void deleteCharacter_WhenCharacterDoesNotExist_DoesNothing()
    {
        String characterId = "1";

        asterixService.deleteCharacterById(characterId);

        verify(asterixRepo, times(1)).deleteById(characterId);
    }

    @Test
    void addCharacter_ShouldGenerateIdAndSaveCharacter()
    {
        Character inputCharacter = new Character(null, "Asterix", 35, "Warrior");
        String generatedId = "123-abc-456";
        Character savedCharacter = new Character(generatedId, "Asterix", 35, "Warrior");

        when(idService.randomId()).thenReturn(generatedId);

        when(asterixRepo.save(any(Character.class))).thenReturn(savedCharacter);

        Character result = asterixService.addCharacter(inputCharacter);

        assertThat(result).isEqualTo(savedCharacter);
        verify(idService, times(1)).randomId();
        verify(asterixRepo, times(1)).save(any(Character.class));
    }

}
