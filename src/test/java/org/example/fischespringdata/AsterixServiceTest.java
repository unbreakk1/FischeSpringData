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

/**
 * Unit tests for the AsterixService class, utilizing Mockito to mock dependencies.
 * All tests ensure functionality is properly isolated and methods work as intended.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito annotations
class AsterixServiceTest
{

    @Mock
    private AsterixCharacters asterixRepo; // Mock of the MongoDB repository for Character objects

    @Mock
    private IdService idService; // Mock of the service used to generate random IDs for new characters

    @InjectMocks
    private AsterixService asterixService; // Class under test, with mocked dependencies automatically injected

    /**
     * Test the getAllCharacters() method.
     * Ensures the service retrieves a list of characters from the repository.
     */
    @Test
    void findAllCharacters_ShouldReturnListOfCharacters()
    {
        // Arrange: Mock repository to return a list of characters
        List<Character> mockCharacters = List.of(
                new Character("1", "Asterix", 35, "Warrior"),
                new Character("2", "Obelix", 30, "Menhir Deliveryman")
        );
        when(asterixRepo.findAll()).thenReturn(mockCharacters);

        // Act: Call the method under test
        List<Character> allCharacters = asterixService.getAllCharacters();

        // Assert: Verify the service returns the expected result
        assertThat(allCharacters).isEqualTo(mockCharacters);
        verify(asterixRepo, times(1)).findAll(); // Confirm findAll() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other interactions with the repository
    }

    /**
     * Test the getCharacterById() method.
     * Ensures the service retrieves the correct character when the ID exists.
     */
    @Test
    void findById_WhenCharacterExists_ReturnsCharacter()
    {
        // Arrange: Mock repository to return a specific character
        String characterId = "1";
        Character mockCharacter = new Character(characterId, "Asterix", 35, "Warrior");
        when(asterixRepo.findById(characterId)).thenReturn(Optional.of(mockCharacter));

        // Act: Call the method under test
        Character foundCharacter = asterixService.getCharacterById(characterId);

        // Assert: Verify the returned character matches the mock
        assertThat(foundCharacter).isEqualTo(mockCharacter);
        verify(asterixRepo, times(1)).findById(characterId); // Confirm findById() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other interactions with the repository
    }

    /**
     * Test the getCharacterById() method when the character with the given ID does not exist.
     * Ensures the service returns null.
     */
    @Test
    void findById_WhenCharacterDoesNotExist_ReturnsNull()
    {
        // Arrange: Mock repository to return an empty Optional
        String characterId = "1";
        when(asterixRepo.findById(characterId)).thenReturn(Optional.empty());

        // Act: Call the method under test
        Character foundCharacter = asterixService.getCharacterById(characterId);

        // Assert: Verify the returned character is null
        assertThat(foundCharacter).isNull();
        verify(asterixRepo, times(1)).findById(characterId); // Confirm findById() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other interactions with the repository
    }

    /**
     * Test the updateCharacter() method when the character exists.
     * Ensures the service updates the character and returns the updated object.
     */
    @Test
    void updateCharacter_WhenCharacterExists_UpdatesAndReturnsUpdatedCharacter() throws Exception
    {
        // Arrange: Mock existing and updated characters
        String characterId = "1";
        Character existingCharacter = new Character(characterId, "Asterix", 35, "Warrior");
        CharacterUpdateDTO dto = new CharacterUpdateDTO();
        dto.setName("Updated Asterix");
        dto.setAge(36);
        dto.setProfession("Leader");

        Character updatedCharacter = new Character(characterId, "Updated Asterix", 36, "Leader");

        when(asterixRepo.findById(characterId)).thenReturn(Optional.of(existingCharacter));
        when(asterixRepo.save(any(Character.class))).thenReturn(updatedCharacter);

        // Act: Call the method under test
        Character result = asterixService.updateCharacter(characterId, dto);

        // Assert: Verify the character was updated correctly
        assertThat(result).isEqualTo(updatedCharacter);
        verify(asterixRepo, times(1)).findById(characterId); // Confirm findById() was called once
        verify(asterixRepo, times(1)).save(any(Character.class)); // Confirm save() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other repository interactions
    }

    /**
     * Test the updateCharacter() method when the character does not exist.
     * Ensures an exception is thrown.
     */
    @Test
    void updateCharacter_WhenCharacterDoesNotExist_ThrowsException()
    {
        // Arrange: Mock repository to return an empty Optional
        String characterId = "1";
        CharacterUpdateDTO dto = new CharacterUpdateDTO();
        when(asterixRepo.findById(characterId)).thenReturn(Optional.empty());

        // Act & Assert: Ensure an exception is thrown
        assertThatThrownBy(() -> asterixService.updateCharacter(characterId, dto))
                .isInstanceOf(Exception.class)
                .hasMessage("Character not found");

        verify(asterixRepo, times(1)).findById(characterId); // Confirm findById() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other repository interactions
    }

    /**
     * Test the deleteCharacterById() method.
     * Ensures the service deletes the character when it exists.
     */
    @Test
    void deleteCharacter_WhenCharacterExists_DeletesCharacter()
    {
        // Arrange: Define a character ID to delete
        String characterId = "1";

        // Act: Call the method under test
        asterixService.deleteCharacterById(characterId);

        // Assert: Verify the delete operation
        verify(asterixRepo, times(1)).deleteById(characterId); // Confirm deleteById() was called once
        verifyNoMoreInteractions(asterixRepo); // Ensure no other repository interactions
    }

    /**
     * Test the addCharacter() method.
     * Ensures the service generates a random ID and saves the character.
     */
    @Test
    void addCharacter_ShouldGenerateIdAndSaveCharacter()
    {
        // Arrange: Mock input and expected results
        Character inputCharacter = new Character(null, "Asterix", 35, "Warrior");
        String generatedId = "123-abc-456";
        Character savedCharacter = new Character(generatedId, "Asterix", 35, "Warrior");

        when(idService.randomId()).thenReturn(generatedId); // Mock ID generation
        when(asterixRepo.save(any(Character.class))).thenReturn(savedCharacter); // Mock save operation

        // Act: Call the method under test
        Character result = asterixService.addCharacter(inputCharacter);

        // Assert: Verify the character was saved with random ID
        assertThat(result).isEqualTo(savedCharacter);
        verify(idService, times(1)).randomId(); // Confirm randomId() was called once
        verify(asterixRepo, times(1)).save(any(Character.class)); // Confirm save() was called once
        verifyNoMoreInteractions(idService, asterixRepo); // Ensure no unexpected interactions with mocks
    }
}

