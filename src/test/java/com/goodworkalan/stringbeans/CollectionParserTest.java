package com.goodworkalan.stringbeans;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.goodworkalan.diffuse.Diffuser;

public class CollectionParserTest {
    @Test
    public void create() {
        Person person = new Person();
        person.firstName = "Alan";
        person.lastName = "Gutierrez";
        person.addresses = new ArrayList<Address>();
        
        Address home = new Address();
        home.street = "3019 Ponce de Leon St";
        home.city = "New Orleans";
        home.state = "LA";
        home.zip = "70119";
        
        person.addresses.add(home);
        
        Converter converter = new Converter();
        converter.setBean(Person.class);
        converter.setBean(Address.class);
        CollectionParser parser = new CollectionParser(converter, false);
        
        person = parser.create(Person.class, (Map<?, ?>) new Diffuser().diffuse(person, "*"));
        
        assertEquals(person.firstName, "Alan");
        assertEquals(person.lastName, "Gutierrez");
        assertEquals(person.addresses.get(0).street, "3019 Ponce de Leon St");
        assertEquals(person.addresses.get(0).city, "New Orleans");
        assertEquals(person.addresses.get(0).state, "LA");
        assertEquals(person.addresses.get(0).zip, "70119");
    }
    
    @Test
    public void populate() {
        Person person = new Person();
        person.firstName = "Alan";
        person.lastName = "Gutierrez";
        person.addresses = new ArrayList<Address>();
        
        Address home = new Address();
        home.street = "3019 Ponce de Leon St";
        home.city = "New Orleans";
        home.state = "LA";
        home.zip = "70119";
        
        person.addresses.add(home);
        
        Converter converter = new Converter();
        converter.setBean(Person.class);
        converter.setBean(Address.class);
        CollectionParser parser = new CollectionParser(converter, false);
        
        Object diffused = new Diffuser().diffuse(person, "*");
        person = new Person();
        
        parser.populate(person, (Map<?, ?>) diffused);
        
        assertEquals(person.firstName, "Alan");
        assertEquals(person.lastName, "Gutierrez");
        assertEquals(person.addresses.get(0).street, "3019 Ponce de Leon St");
        assertEquals(person.addresses.get(0).city, "New Orleans");
        assertEquals(person.addresses.get(0).state, "LA");
        assertEquals(person.addresses.get(0).zip, "70119");
    }
    
    @SuppressWarnings("unchecked")
    private Map<Object, Object> toObjectMap(Object map) {
        return (Map) map;
    }
    
    @Test
    public void missing() {
        Person person = new Person();
        person.firstName = "Alan";
        person.lastName = "Gutierrez";
        person.addresses = new ArrayList<Address>();
        
        Address home = new Address();
        home.street = "3019 Ponce de Leon St";
        home.city = "New Orleans";
        home.state = "LA";
        home.zip = "70119";
        
        person.addresses.add(home);
        
        Converter converter = new Converter();
        converter.setBean(Address.class);
        converter.setBean(Person.class);
        CollectionParser parser = new CollectionParser(converter, true);
        
        Map<Object, Object> diffused = new HashMap<Object, Object>(toObjectMap(new Diffuser().diffuse(person, "*")));
        
        diffused.put("missing", "foo");
        
        person = parser.create(Person.class, diffused);
        
        assertEquals(person.firstName, "Alan");
        assertEquals(person.lastName, "Gutierrez");
        assertEquals(person.addresses.get(0).street, "3019 Ponce de Leon St");
        assertEquals(person.addresses.get(0).city, "New Orleans");
        assertEquals(person.addresses.get(0).state, "LA");
        assertEquals(person.addresses.get(0).zip, "70119");
    }
}
