package com.springfirst.reactiveexamples.repositories;

import com.springfirst.reactiveexamples.domain.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {


    Person mike = Person.builder().id(1).firstName("Mike").lastName("Myers").build();
    Person anne = Person.builder().id(2).firstName("Anne").lastName("Able").build();
    Person peter = Person.builder().id(3).firstName("Peter").lastName("Prune").build();
    Person sam = Person.builder().id(4).firstName("Sam").lastName("Antha").build();

    @Override
    public Mono<Person> getById(Integer id) {
        return Mono.just(mike);
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(mike,peter,sam,anne);
    }
}
