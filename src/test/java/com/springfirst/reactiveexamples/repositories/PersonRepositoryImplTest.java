package com.springfirst.reactiveexamples.repositories;

import com.springfirst.reactiveexamples.domain.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

class PersonRepositoryImplTest {

    private PersonRepository personRepository;


    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    public void getByIdBlock() {

        Mono<Person> personMono = personRepository.getById(1);

        Person p = personMono.block();
        System.out.println(p);
    }

    @Test
    public void getByIdSubscribe() {

        Mono<Person> personMono = personRepository.getById(1);

        personMono.subscribe(System.out::println);
    }

    @Test
    public void getByIdSubscribeWithMapTransform() {

        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(person -> {
            return person.getFirstName().toUpperCase(Locale.ROOT);
        }).subscribe(System.out::println);
    }

    @Test
    public void fluxFindFirstBlock() {

        Flux<Person> personFlux = personRepository.findAll();

        Person p = personFlux.blockFirst();

        System.out.println(p);
    }

    @Test
    public void fluxFindFirstSubscribe() {

        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxFindWithMap() throws Exception {

        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(p -> {
            return p.getFirstName().toUpperCase(Locale.ROOT);
        }).subscribe(System.out::println);
    }

    @Test
    public void fluxToListMono() {

        Mono<List<Person>> personListMono = personRepository.findAll().collectList();

        StepVerifier.create(personListMono).expectNextCount(1).verifyComplete();

        personListMono.subscribe(list -> {
            list.forEach(System.out::println);

        });
    }

    @Test
    public void findByFluxFilter_success() {

        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        final Integer id = 2;
        Mono<Person> personMono = personFlux.filter(p -> p.getId() == id).next(); //next, get next, fails silently

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.subscribe(System.out::println);
    }

    @Test
    public void findByFluxFilterWithException_fail() {

        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 5;
        Mono<Person> personMono = personFlux.filter(p -> p.getId() == id).single(); //expect and emit one, and only one.
        //throws a java.util.NoSuchElementException if filter returns not exactly 1

        StepVerifier.create(personMono).expectError(NoSuchElementException.class);

        personMono.doOnError(throwable -> {
            System.out.println("Boom! with a " + throwable.getClass());
        })
                .onErrorReturn(Person.builder().id(id).build()).subscribe(System.out::println);
    }

    @Test
    @DisplayName("Flux returns right number")
    public void findAllReturnsFour() {

        final int expected = 4;
        Flux<Person> allPeople = personRepository.findAll();
        StepVerifier.create(allPeople).expectNextCount(expected).verifyComplete();
    }


    @Test
    public void findByIDWhereIDExists() {
        final Integer id = 3;

        Mono<Person> person = personRepository.getById(id);

        Person peter = Person.builder().id(3).firstName("Peter").lastName("Prune").build();

        StepVerifier.create(person).expectNext(peter).verifyComplete();
        person.subscribe(p -> {
            Assertions.assertSame("Peter", p.getFirstName());
        });

    }

    @Test
    @DisplayName("Search by Id with no match returns an empty Person")
    public void findByIDWhereIDDoesNotExist() {
        final Integer id = 6;

        Mono<Person> person = personRepository.getById(id);

        StepVerifier.create(person).expectNextCount(0).verifyComplete();
        // or StepVerifier.create(person).verifyComplete();
        person.subscribe(p -> {
            Assertions.assertAll(
                    () -> Assertions.assertNull(p.getId()),
                    () -> Assertions.assertNull(p.getFirstName()),
                    () -> Assertions.assertNull(p.getLastName()));
        });
    }
}