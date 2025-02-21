package org.example.fischespringdata;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AsterixCharacters extends MongoRepository<Character, String>
{
}
