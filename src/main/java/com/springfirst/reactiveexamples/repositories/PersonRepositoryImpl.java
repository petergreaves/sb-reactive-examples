package com.springfirst.reactiveexamples.repositories;

import com.springfirst.reactiveexamples.domain.Person;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class PersonRepositoryImpl implements PersonRepository {


    Person mike = Person.builder().id(1).firstName("Mike").lastName("Myers").build();
    Person anne = Person.builder().id(2).firstName("Anne").lastName("Able").build();
    Person peter = Person.builder().id(3).firstName("Peter").lastName("Prune").build();
    Person sam = Person.builder().id(4).firstName("Sam").lastName("Antha").build();


    @Override
    public Mono<Person> getById(Integer id) {

        return findAll()
                .filter(p -> p.getId() == id)
                .next();


//                .doOnError(throwable -> {
//                    log.error("No such person with requested ID " + id + ", returning am empty Person");
//                })
//                .onErrorReturn(Person.builder().build());

    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(mike, anne, peter, sam);
    }
}
