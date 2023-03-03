package ru.clevertec.zabalotcki.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Product {
    private int age;
    private String name;
    private List<Person> people;
    private Person[] people1;
    private int[] ints;
    private char[] chars;

    public Product(int age, String name, List<Person> people, Person[] people1, int[] ints, char[] chars) {
        this.age = age;
        this.name = name;
        this.people = people;
        this.people1 = people1;
        this.ints = ints;
        this.chars = chars;
    }

    public Product() {
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public Person[] getPeople1() {
        return people1;
    }

    public void setPeople1(Person[] people1) {
        this.people1 = people1;
    }

    public int[] getInts() {
        return ints;
    }

    public void setInts(int[] ints) {
        this.ints = ints;
    }

    public char[] getChars() {
        return chars;
    }

    public void setChars(char[] chars) {
        this.chars = chars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return age == product.age &&
                Objects.equals(name, product.name) &&
                Objects.equals(people, product.people) &&
                Arrays.equals(people1, product.people1) &&
                Arrays.equals(ints, product.ints) &&
                Arrays.equals(chars, product.chars);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(age, name, people);
        result = 31 * result + Arrays.hashCode(people1);
        result = 31 * result + Arrays.hashCode(ints);
        result = 31 * result + Arrays.hashCode(chars);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", people=" + people +
                ", people1=" + Arrays.toString(people1) +
                ", ints=" + Arrays.toString(ints) +
                ", chars=" + Arrays.toString(chars) +
                '}';
    }
}
