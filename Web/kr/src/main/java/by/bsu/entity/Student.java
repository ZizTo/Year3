package by.bsu.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "STUDENT")
@NamedQueries({
    @NamedQuery(name = "studentByLastName", 
           query = "select st from Student st where st.lastName = :lastName and st.startDate > :date"),
    @NamedQuery(name = "studentsInCourse", 
           query = "select st from Student st join st.courses c where c.name = :courseName" )
})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private int id;
    
    @Column(name = "FIRST_NAME")
    private String firstName;
    
    @Column(name = "LAST_NAME")
    private String lastName;

    @Basic
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "ADDR_CITY")),
        @AttributeOverride(name = "street", column = @Column(name = "ADDR_STREET"))
    })
    private Address address;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "COURSE_STUDENT",
        joinColumns = {@JoinColumn(name = "STUDENT_ID")},
        inverseJoinColumns={@JoinColumn(name="COURSE_ID")}
    )
    private List<Course> courses;

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
