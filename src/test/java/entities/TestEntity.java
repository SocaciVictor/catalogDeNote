package entities;

import com.example.demo.persistence.entities.PersistableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "test", schema = "public", catalog = "demo_test")
public class TestEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Basic
    @Column(name = "mock")
    private String mock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mockField) {
        this.mock = mockField;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id=" + id +
                ", mock='" + mock + '\'' +
                '}';
    }
}
