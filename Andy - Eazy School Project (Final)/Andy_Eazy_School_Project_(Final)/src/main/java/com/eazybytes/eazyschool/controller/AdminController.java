package com.eazybytes.eazyschool.controller;

import com.eazybytes.eazyschool.model.Courses;
import com.eazybytes.eazyschool.model.EazyClass;
import com.eazybytes.eazyschool.model.Person;
import com.eazybytes.eazyschool.repository.CoursesRepository;
import com.eazybytes.eazyschool.repository.EazyClassRepository;
import com.eazybytes.eazyschool.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    EazyClassRepository eazyClassRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    CoursesRepository coursesRepository;

    @RequestMapping("/displayClasses")
    public ModelAndView displayClasses() {

        List<EazyClass> eazyClasses = eazyClassRepository.findAll();             /** Fetches all classes from the DB. */
        ModelAndView modelAndView = new ModelAndView("classes.html");   /** Creates the view. */
        modelAndView.addObject("eazyClasses", eazyClasses);          /** Populates the view with the existing classes: 'classes.html': <tr th:each="eazyClass: ${eazyClasses}">*/
        modelAndView.addObject("eazyClass", new EazyClass());        /** Needed fot the 'add new class' modal. ... th:object="${eazyClass}"> */
        return modelAndView;
    }

    @PostMapping("/addNewClass")
    public ModelAndView addNewClass (@ModelAttribute ("eazyClass") EazyClass eazyClass) {

        eazyClassRepository.save(eazyClass);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @RequestMapping("/deleteClass")
    public ModelAndView deleteClass (@RequestParam int id) {

        Optional<EazyClass> eazyClass = eazyClassRepository.findById(id);
        for(Person person : eazyClass.get().getPersons()){                  /**  Sets all persons classes to NULL */
            person.setEazyClass(null);                                      /**  beefore deleting the class  */
            personRepository.save(person);                                  /**  Saves it to the DB */
        }
        eazyClassRepository.deleteById(id);                                                       /**  Deletes the classes */
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");  /**  Creates the new ModelAndview */
        return modelAndView;                                                                      /**  to where it Redirects the user  */
    }

    @RequestMapping("/displayStudents")
    public ModelAndView displayStudents (@RequestParam int classId,
                                         HttpSession session,
                                         @RequestParam (value = "error", required = false) String error) {

        String errorMessage = null;

        ModelAndView modelAndView = new ModelAndView("students.html");
        Optional<EazyClass> eazyClass = eazyClassRepository.findById(classId);
        modelAndView.addObject("eazyClass", eazyClass.get());
        modelAndView.addObject("person", new Person());

        session.setAttribute("eazyClass", eazyClass.get());

        if (null != error) {
            errorMessage = "Invalid Email entered!";
            modelAndView.addObject("errorMessage", errorMessage);
            /** this is passed to the UI (students.html) to display the message: th:text="${errorMessage}"*/
        }
        return modelAndView;
    }


    @PostMapping("/addStudent")
    public ModelAndView addStudent (@ModelAttribute("person") Person person,
                                    HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        EazyClass eazyClass = (EazyClass) session.getAttribute("eazyClass"); // passed on the method 'displayStudents'
        Person personEntity = personRepository.readByEmail(person.getEmail());

        if (null == personEntity || !(personEntity.getPersonId() > 0)) {
            modelAndView.setViewName("redirect:/admin/displayStudents?classId=" + eazyClass.getClassId() + "&error=true");

            return modelAndView;
        }

        personEntity.setEazyClass(eazyClass);
        personRepository.save(personEntity);

        eazyClass.getPersons().add(personEntity);
        eazyClassRepository.save(eazyClass);

        modelAndView.setViewName("redirect:/admin/displayStudents?classId=" + eazyClass.getClassId());
        return modelAndView;

    }

    @GetMapping("/deleteStudent")
    public ModelAndView deleteStudent (@RequestParam int personId,
                                       HttpSession session) {

        EazyClass eazyClass = (EazyClass) session.getAttribute("eazyClass");
        Optional <Person> person = personRepository.findById(personId);

        person.get().setEazyClass(null);

        eazyClass.getPersons().remove(person.get());

        EazyClass eazyClassSaved = eazyClassRepository.save(eazyClass);
        session.setAttribute("eazyClass", eazyClassSaved);

        ModelAndView modelAndView = new ModelAndView(
                "redirect:/admin/displayStudents?classId=" + eazyClass.getClassId());

        return modelAndView;
    }

    @GetMapping("/displayCourses")
    public ModelAndView displayCourses () {
        // List<Courses> courses = coursesRepository.findByOrderByNameDesc();
        List<Courses> courses = coursesRepository.findAll(Sort.by("name").descending());

        ModelAndView modelAndView = new ModelAndView("courses_secure.html");
        modelAndView.addObject("courses", courses);
        modelAndView.addObject("course", new Courses());
        return modelAndView;

    }

    @PostMapping("/addNewCourse")
    public ModelAndView addNewCourse (@ModelAttribute("course") Courses course) {

        ModelAndView modelAndView = new ModelAndView();
        coursesRepository.save(course);
        modelAndView.setViewName("redirect:/admin/displayCourses");
        return modelAndView;
    }

    @GetMapping("/viewStudents")
    public ModelAndView viewStudents (@RequestParam int id,
                                      @RequestParam(/*value = "error", */required = false) String error,
                                      HttpSession session) {

        String errorMessage = null;

        ModelAndView modelAndView = new ModelAndView("course_students.html");
        Optional<Courses> courses = coursesRepository.findById(id);
        modelAndView.addObject("courses", courses.get());
        modelAndView.addObject("person", new Person());
        session.setAttribute("courses", courses.get());

        if (null != error) {
            errorMessage = "Invalid Email entered!";
            modelAndView.addObject("errorMessage", errorMessage);
        }

        return modelAndView;

    }

    @PostMapping("/addStudentToCourse")
    public ModelAndView addStudentToCourse (@ModelAttribute ("person") Person person,
                                            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        Courses courses = (Courses) session.getAttribute("courses");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if (null == personEntity || !(personEntity.getPersonId() > 0)) {
            modelAndView.setViewName(
                    "redirect:/admin/viewStudents?id="+courses.getCourseId()+"&error=true");
            return modelAndView;
        }

        personEntity.getCourses().add(courses);
        courses.getPersons().add(personEntity);
        personRepository.save(personEntity);
        session.setAttribute("courses", courses);
        modelAndView.setViewName("redirect:/admin/viewStudents?id="+courses.getCourseId());

        return modelAndView;
    }


    @GetMapping("/deleteStudentFromCourse")
    public ModelAndView deleteStudentFromCourse (@RequestParam int personId,
                                                 HttpSession session) {

        Courses courses = (Courses) session.getAttribute("courses");
        Optional<Person> person = personRepository.findById(personId);

        person.get().getCourses().remove(courses);
        courses.getPersons().remove(person);

        personRepository.save(person.get());

        session.setAttribute("courses", courses);

        ModelAndView modelAndView = new ModelAndView(
                "redirect:/admin/viewStudents?id="+courses.getCourseId());

        return modelAndView;
    }




}


/** Inside Spring MVC we have Model, ModelMap, and ModelAndView
 * which can be used to define a model in a Spring MVC application.

1) Model - defines a holder for model attributes and is primarily designed
 for adding attributes to the model. Using this we can send/accept data to/from the UI.

2) ModelMap - is an extension of Model with the ability to store attributes in a map
 and chain method calls. Using this we can send/accept data to/from the UI.

3) ModelAndView - is a holder for a model and a view; it allows to return both model and view in one return value.
 Using this we can send/accept data to/from the UI & also we can mention the view name that needs to be displayed.

Basically using Model& ModelMapwe can only share the data from UI to backend & viceversa.
Where as with ModelAndView, we can share both data and view information between UI & backend.

Now coming to your question, we are using Model as an input since we only want to accept the data
 where as the return type we used ModelAndView, since we want to send both data and view information.*/