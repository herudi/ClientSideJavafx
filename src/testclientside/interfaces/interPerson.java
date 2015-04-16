/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testclientside.interfaces;

import javafx.collections.ObservableList;
import testclientside.model.Person;

/**
 *
 * @author Herudi
 */
public interface interPerson {
    void save(Person p);
    void update(Person p);
    void delete(Person p);
    ObservableList<Person> search(String a);
    String maxID();
    ObservableList<Person> select();
}
