import com.example.demo.persistence.connection.FileSerializeConnection;
import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.dao.EntityDao;
import entities.TestEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileConnectionTest {
    private static final String TEST_FILE = "test_users.ser";
    private FileSerializeConnection fileConnection;
    private EntityDao<TestEntity> fileDao;

    @BeforeEach
    void setUp() throws Exception {
        this.fileConnection = new FileSerializeConnection(TEST_FILE);
        this.fileDao = new EntityDao<>(fileConnection);
    }

    @AfterEach
    void tearDown() throws Exception {
        fileConnection.close();
        // Delete the test file after each test
        new File(TEST_FILE).delete();
    }

    @Test
    void testSaveAndFindAll() throws Exception {
        // Create a test user
        TestEntity mock = new TestEntity();
        mock.setMock("Mock 1");

        TestEntity mock2 = new TestEntity();
        mock2.setMock("Mock 2");

        // Save the user to the file
        this.fileDao.save(mock);
        this.fileDao.save(mock2);

        // Retrieve all users and verify the saved user is present
        List<TestEntity> testEntities = this.fileDao.findAll(TestEntity.class);
        assertEquals(2, testEntities.size());
        assertEquals("Mock 1", testEntities.get(0).getMock());

        testEntities.forEach(System.out::println);
    }

    @Test
    void testFindAllEmptyFile() throws Exception {
        // Find all users in an empty file
        List<TestEntity> testEntities = this.fileDao.findAll(TestEntity.class);
        assertTrue(testEntities.isEmpty());
    }

    @Test
    void testFindAllByParams() throws Exception {
        String parameterValue = "SEARCH_VALUE";
        String parameterName = "mock";

        TestEntity entity1 = new TestEntity();
        entity1.setMock(parameterValue);
        this.fileDao.save(entity1);

        TestEntity entity2 = new TestEntity();
        entity2.setMock(parameterValue);
        this.fileDao.save(entity2);

        ParameterPair<String, String> parameterPair = new ParameterPair<>(parameterName, parameterValue);
        List<TestEntity> matchingEntities = this.fileDao.findAllByParams(TestEntity.class, parameterPair);

        assertEquals(2, matchingEntities.size());
        assertEquals(parameterValue, matchingEntities.get(0).getMock());
        assertEquals(parameterValue, matchingEntities.get(1).getMock());

        matchingEntities.forEach(System.out::println);
    }

}
