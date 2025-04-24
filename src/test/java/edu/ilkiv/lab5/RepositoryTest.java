package edu.ilkiv.lab5;

/*
  @author Bodya
  @project lab5
  @class RepositoryTest
  version 1.0.0
  @since 24.04.2025 - 20:24 
*/

import edu.ilkiv.lab5.model.Bus;
import edu.ilkiv.lab5.repository.BusRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    BusRepository underTest;

    @BeforeEach
    void setUp() {
        Bus freddy = new Bus("1", "1234 CE", "000001","###test");
        Bus paul = new Bus("2", "4323 AE", "000002","###test");
        Bus mick = new Bus("3", "9423 MO", "000003","###test");
        underTest.saveAll(List.of(freddy, paul, mick));
    }

    @AfterEach
    void tearDown() {
        List<Bus> busesToDelete = underTest.findAll().stream()
                .filter(bus -> bus.getDescription().contains("###test"))
                .toList();
        underTest.deleteAll(busesToDelete);
    }

    @AfterAll
    void afterAll() {}

    @Test
    void testSetShouldContains_3_Records_ToTest(){
        List<Bus> busesToDelete = underTest.findAll().stream()
                .filter(bus -> bus.getDescription().contains("###test"))
                .toList();
        assertEquals(3,busesToDelete.size());
    }

    @Test
    void shouldGiveIdForNewRecord() {
        // given
        Bus john = new Bus("5454 AK", "000004","###test");
        // when
        underTest.save(john);
        Bus busFromDb = underTest.findAll().stream()
                .filter(bus -> bus.getBoardNumber().equals("5454 AK"))
                .findFirst().orElse(null);
        // then
        assertFalse(busFromDb.getId() == john.getId());
        assertNotNull(busFromDb);
        assertNotNull(busFromDb.getId());
        assertFalse(busFromDb.getId().isEmpty());
        assertEquals(24, busFromDb.getId().length());
    }


}
