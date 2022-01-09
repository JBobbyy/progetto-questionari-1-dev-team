package it.unimib.unimibmodules.model;

import java.util.Set;
import javax.persistence.*;

import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Rapresentation of a user in a general website way. 
 * @author Gianlorenzo Martini
 * @version 0.0.1
 */

@Entity
@Table(name = "user")
public class User {
    
    private int id;
    private String email;
    private String password;
    private String username;
    private String name;
    private String surname;
    
    @OneToMany(mappedBy = "user")
    private Set<Survey> surveysCreated;
    
    @ManyToMany
    @JoinTable(
        name = "survey_user", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "survey_id"))
    private Set<Survey> surveysCompiled;
    
    @OneToMany(mappedBy = "user")
    private Set<Question> questions;

    @OneToMany(mappedBy = "user")
    private Set<Answer> answers;

    public User() {}
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Set<Survey> getSurveysCreated() {
        return surveysCreated;
    }

    public void setSurveysCreated(Set<Survey> surveysCreated) {
        this.surveysCreated = surveysCreated;
    }

    public Set<Survey> getSurveysCompiled() {
        return surveysCompiled;
    }

    public void setSurveysCompiled(Set<Survey> surveysCompiled) {
        this.surveysCompiled = surveysCompiled;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
}