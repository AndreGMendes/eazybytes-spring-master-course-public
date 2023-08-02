package com.eazybytes.eazyschool.model;

import com.eazybytes.eazyschool.annotation.FieldsValueMatch;
import com.eazybytes.eazyschool.annotation.PasswordValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;


/**
@Data - Can't be used here or it will throw a StackOverFlow error
        because Lombok uses a ToString Method which is going to create issues
        when we're trying to do these kind of mappings inside the SprindDataJPA or any other ORM Framework.
        So instead we will use @Getter and @Setter annotations in this EazyClass*/
@Getter
@Setter
@Entity
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "email",
                fieldMatch = "confirmEmail",
                message = "Email addresses do not match!"
        ),
        @FieldsValueMatch(
                field = "pwd",
                fieldMatch = "confirmPwd",
                message = "Passwords do not match!"
        )
})
public class Person extends BaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private int personId;

    @NotBlank(message="Name must not be blank")
    @Size(min=3, message="Name must be at least 3 characters long")
    private String name;

    @NotBlank(message="Mobile number must not be blank")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotBlank(message="Email must not be blank")
    @Email(message = "Please provide a valid email address" )
    private String email;

    @NotBlank(message="Confirm Email must not be blank")
    @Email(message = "Please provide a valid confirm email address" )
    @Transient
    @JsonIgnore
    private String confirmEmail;

    @NotBlank(message="Password must not be blank")
    @Size(min=5, message="Password must be at least 5 characters long")
    @PasswordValidator
    @JsonIgnore
    private String pwd;

    @NotBlank(message="Confirm Password must not be blank")
    @Size(min=5, message="Confirm Password must be at least 5 characters long")
    @Transient
    @JsonIgnore
    private String confirmPwd;

    /**
     CascadeType.PERSIST ----> Saving the parent table cascades to child table record associated.
     CascadeType.REMOVE -----> Removing the parent table record does NOT remove the child table record associated.
     CascadeType.ALL --------> All actions on the parent table record also cascade to the child table record.
     CascadeType.DETACH -----> When detaching parent object from the current Session detach the child entity also
     CascadeType.REFRESH ----> When reloading the parent entity also reload the child
     CascadeType.MERGE ------> Similar to an Update operation

     FetchType.LAZY ---------> Loads on demand (ex: if a method is called that belongs to this associated class)
     FetchType.EAGER --------> Loads all the related variable/table data (Takes more time)

     Sometimes you have two entities and there's a relationship between them.
     For example, you might have an entity called University and another entity called Student
     and a University might have many Students:
     The University entity might have some basic properties such as id, name, address, etc.
     as well as a collection property called students that returns the list of students for a given university:

         public class University {
             private String id;
             private String name;
             private String address;
             private List<Student> students;

         // setters and getters
         }

     Now when you load a University from the database, JPA loads its id, name, and address fields for you.
     But you have two options for how students should be loaded:

     1) To load it together with the rest of the fields (i.e. eagerly), or
     2) To load it on-demand (i.e. lazily) when you call the university's getStudents() method.
     When a university has many students it is not efficient to load all of its students
     together with it, especially when they are not needed and in suchlike cases
     you can declare that you want students to be loaded when they are actually needed.
     This is called lazy loading.
    */

    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST,
            targetEntity = Roles.class)
    @JoinColumn(
            name = "role_id",
            referencedColumnName = "roleId",
            nullable = false)
    private Roles roles;

    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL /**, 'targetEntity = Address.class' - Not really needed*/)
    @JoinColumn(
            name = "address_id",
            referencedColumnName = "addressId",
            nullable = true)
    private Address address;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = true)
    @JoinColumn(
            name = "class_id",
            referencedColumnName = "classId",
            nullable = true)
    private EazyClass eazyClass;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "person_courses",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "personId")},
            inverseJoinColumns = {@JoinColumn(name = "course_id", referencedColumnName = "courseId")})
    private Set<Courses> courses = new HashSet<>();

}
