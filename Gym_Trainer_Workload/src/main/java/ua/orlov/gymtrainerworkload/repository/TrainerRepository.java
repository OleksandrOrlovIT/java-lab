package ua.orlov.gymtrainerworkload.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ua.orlov.gymtrainerworkload.model.Trainer;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {

    Optional<Trainer> findByUsername(String username);

    @Query("{'firstName': ?0, 'lastName': ?1}")
    List<Trainer> findByFirstNameAndLastName(String firstName, String lastName);
}
