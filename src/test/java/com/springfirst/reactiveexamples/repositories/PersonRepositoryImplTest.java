package com.springfirst.reactiveexamples.repositories;

import com.springfirst.reactiveexamples.domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

class PersonRepositoryImplTest {

    private PersonRepository personRepository;


    @BeforeEach
    void setUp() {

        personRepository = new PersonRepositoryImpl();
    }

    @Test
    public void getByIdBlock() throws Exception {

        Mono<Person> personMono = personRepository.getById(1);

        Person p = personMono.block();
        System.out.println(p);
    }

    @Test
    public void getByIdSubscribe() throws Exception {

        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(p -> {
            System.out.println(p);
        });
    }

    @Test
    public void getByIdSubscribeWithMapTransform() throws Exception {

        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(person -> {
            return person.getFirstName().toUpperCase(Locale.ROOT);
        }).subscribe(f -> {
            System.out.println(f);
        });
    }

    @Test
    public void fluxFindFirstBlock() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        Person p = personFlux.blockFirst();

        System.out.println(p);
    }

    @Test
    public void fluxFindFirstSubscribe() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(p -> {

            System.out.println(p);
        });
    }

    @Test
    public void fluxFindWithMap() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(p -> {
            return p.getFirstName().toUpperCase(Locale.ROOT);
        }).subscribe(fn -> {
            System.out.println(fn);
        });
    }

    @Test
    public void fluxToListMono() throws Exception {

        Mono<List<Person>> personListMono = personRepository.findAll().collectList();

        personListMono.subscribe(list -> {
            list.forEach(p -> {

                System.out.println(p);
            });

        });
    }

    @Test
    public void findByFluxFilter_success() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 2;
        Mono<Person> personMono = personFlux.filter(p -> p.getId() == id).next(); //next, get next
        personMono.subscribe(p -> {
            System.out.println(p);
        });
    }

    @Test
    public void findByFluxFilterWithException_fail() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 5;
        Mono<Person> personMono = personFlux.filter(p -> p.getId() == id).single(); //expect and emit one, and only one

        personMono.doOnError(throwable -> {
            System.out.println("Boom!");
        })
                .onErrorReturn(Person.builder().id(id).build()).subscribe(p -> {
            System.out.println(p);
        });
    }
}