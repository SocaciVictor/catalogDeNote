import com.example.demo.persistence.connection.ParameterPair;
import com.example.demo.persistence.connection.database.DatabaseConnection;
import com.example.demo.persistence.dao.EntityDao;
import entities.TestEntity;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {
    private DatabaseConnection databaseConnection;
    private EntityDao<TestEntity> dbDao;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize DatabaseConnection with the test persistence unit
        databaseConnection = new DatabaseConnection("test_persistence");
        this.dbDao = new EntityDao<>(databaseConnection);
    }

    @AfterEach
    void tearDown() throws Exception {
        this.databaseConnection.close();
    }

    @Test
    void testSaveAndFindAll() throws Exception {
        // Create a test user
        TestEntity mock = new TestEntity();
        mock.setMock("Mock 1");

        TestEntity mock2 = new TestEntity();
        mock2.setMock("Mock 2");

        // Save the user to the database
        dbDao.save(mock);
        dbDao.save(mock2);

        // Retrieve all users and verify the saved user is present
        List<TestEntity> testEntities = databaseConnection.findAll(TestEntity.class);
        assertEquals(2, testEntities.size());
        assertEquals("Mock 1", testEntities.get(0).getMock());
        testEntities.forEach(System.out::println);
    }

    @Test
    void testFindFirstByParams()  throws Exception{
        String parameterValue = "TO BE SEARCHED";
        String parameterName = "mock";

        TestEntity toBeFound = new TestEntity();
        toBeFound.setMock(parameterValue);
        dbDao.save(toBeFound);

        ParameterPair<String, String> parameterPair = new ParameterPair<>(parameterName, parameterValue);
        TestEntity firstByParams = dbDao.findFirstByParams(TestEntity.class, parameterPair);

        assertEquals(parameterValue, firstByParams.getMock());
        System.out.println(firstByParams);
    }

    @Test
    void testFindAllByParams() throws Exception {
        String parameterValue = "TO BE SEARCHED";
        String parameterName = "mock";

        TestEntity entity1 = new TestEntity();
        entity1.setMock(parameterValue);
        dbDao.save(entity1);

        TestEntity entity2 = new TestEntity();
        entity2.setMock(parameterValue);
        dbDao.save(entity2);

        ParameterPair<String, String> parameterPair = new ParameterPair<>(parameterName, parameterValue);
        List<TestEntity> matchingEntities = dbDao.findAllByParams(TestEntity.class, parameterPair);

        assertEquals(2, matchingEntities.size());
        assertEquals(parameterValue, matchingEntities.get(0).getMock());
        assertEquals(parameterValue, matchingEntities.get(1).getMock());
    }
}
