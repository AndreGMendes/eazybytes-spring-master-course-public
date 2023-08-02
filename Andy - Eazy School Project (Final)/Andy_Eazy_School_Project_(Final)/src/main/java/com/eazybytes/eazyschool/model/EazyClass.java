package com.eazybytes.eazyschool.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;


/**
@Data - Can't be used here or it will throw a StackOverFlow error
        because Lombok uses a ToString Method which is going to create issues
        when we're trying to do these kind of mappings inside the SprindDataJPA or any other ORM Framework.
        So instead we will use @Getter and @Setter annotations in this EazyClass */
@Getter
@Setter
@Entity
@Table(name = "class")
public class EazyClass extends BaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private int classId;

    @NotBlank(message = "Name must not be blank")
    @Size (min = 3, message = "Name must be at least 3 characters long")
    private String name;

    @OneToMany(
            mappedBy = "eazyClass",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            targetEntity = Person.class)
    private Set<Person> persons;

}
