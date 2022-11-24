package com.example.project.mindray.oidc.usermanage.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.project.mindray.oidc.usermanage.domain.Person;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    Optional<Person> findByName(String name);
}
