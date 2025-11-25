package by.bsu.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import jakarta.persistence.*;
import by.bsu.entity.Address;
import by.bsu.entity.Course;
import by.bsu.entity.Student;

public class StudentsDAO {
    private static final String ENTITY_MANAGER_FACTORY_NAME = "simpleFactory2";
    private EntityManagerFactory factory;
    private static StudentsDAO instance = null;

    public static StudentsDAO getInstance() {
        if (instance == null) {
            instance = new StudentsDAO();
        }
        return instance;
    }

    public StudentsDAO() {
        factory = Persistence.createEntityManagerFactory("simpleFactory2");
    }

    // Обновим создание данных, добавив Города
    public void createDemoStudent() {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            
            Student s1 = new Student();
            s1.setFirstName("Sergey");
            s1.setLastName("Sidorov");
            s1.setStartDate(Calendar.getInstance().getTime());
            s1.setAddress(new Address("Grodno", "Lenina"));

            Student s2 = new Student();
            s2.setFirstName("Ivan");
            s2.setLastName("Petrov");
            s2.setStartDate(Calendar.getInstance().getTime());
            s2.setAddress(new Address("Molodechno", "Mira"));

            Course cGeo = new Course();
            cGeo.setLector("Ivanov");
            cGeo.setName("Geometry");

            List<Student> students = new ArrayList<>();
            List<Course> courses = new ArrayList<>();
            
            List<Course> c1 = new ArrayList<>(); c1.add(cGeo);
            s1.setCourses(c1);
            
            List<Course> c2 = new ArrayList<>(); c2.add(cGeo);
            s2.setCourses(c2);
            
            cGeo.setStudents(new ArrayList<>());
            cGeo.getStudents().add(s1);
            cGeo.getStudents().add(s2);

            entityManager.persist(s1);
            entityManager.persist(s2);
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public List<Object[]> getStudentNamesAndDates() {
        EntityManager em = factory.createEntityManager();
        try {
            Query q = em.createQuery("SELECT s.lastName, s.startDate FROM Student s");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsByCity(String city) {
        EntityManager em = factory.createEntityManager();
        try {
            TypedQuery<Student> q = em.createQuery(
                "SELECT s FROM Student s WHERE s.address.city = :city", Student.class);
            q.setParameter("city", city);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsWithCourseAndCity(String city) {
        EntityManager em = factory.createEntityManager();
        try {
            TypedQuery<Student> q = em.createQuery(
                "SELECT DISTINCT s FROM Student s JOIN s.courses c WHERE s.address.city = :city", Student.class);
            q.setParameter("city", city);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> loadStudentsInCourse(String courseName) {
        EntityManager em = factory.createEntityManager();
        try {
            Query query = em.createNamedQuery("studentsInCourse");
            query.setParameter("courseName", courseName);
            return (List<Student>) query.getResultList();
        } finally {
            em.close();
        }
    }
}
