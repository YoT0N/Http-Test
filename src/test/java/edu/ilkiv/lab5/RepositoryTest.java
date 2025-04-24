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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    void shouldFindBusById() {
        // Given
        Bus bus = new Bus("7777 AA", "111111", "###test");
        Bus saved = underTest.save(bus);

        // When
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("7777 AA", found.getBoardNumber());
    }

    @Test
    void shouldUpdateBus() {
        // Given
        Bus bus = new Bus("8888 BB", "222222", "###test");
        Bus saved = underTest.save(bus);

        // When
        saved.setCode("999999");
        underTest.save(saved);
        Bus updated = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(updated);
        assertEquals("999999", updated.getCode());
    }

    @Test
    void shouldDeleteBus() {
        // Given
        Bus bus = new Bus("9999 CC", "333333", "###test");
        Bus saved = underTest.save(bus);

        // When
        underTest.delete(saved);
        boolean exists = underTest.findById(saved.getId()).isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldFindAllBusesWithTestDescription() {
        // Given
        // (buses with ###test already added in @BeforeEach)

        // When
        List<Bus> buses = underTest.findAll().stream()
                .filter(b -> "###test".equals(b.getDescription()))
                .toList();

        // Then
        assertFalse(buses.isEmpty());
        assertTrue(buses.size() >= 3);
    }

    @Test
    void shouldNotFindNonExistentBus() {
        // Given
        String nonExistentId = "nonexistent-id";

        // When
        boolean exists = underTest.findById(nonExistentId).isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldSaveMultipleBuses() {
        // Given
        List<Bus> buses = List.of(
                new Bus("1111 DD", "444444", "###test"),
                new Bus("2222 EE", "555555", "###test")
        );

        // When
        underTest.saveAll(buses);
        List<Bus> result = underTest.findAll().stream()
                .filter(b -> "###test".equals(b.getDescription()))
                .toList();

        // Then
        assertTrue(result.size() >= 5); // 3 from setup + 2 new ones
    }

    @Test
    void shouldAssignDifferentIdsToBuses() {
        // Given
        Bus bus1 = new Bus("3333 FF", "666666", "###test");
        Bus bus2 = new Bus("4444 GG", "777777", "###test");

        // When
        Bus saved1 = underTest.save(bus1);
        Bus saved2 = underTest.save(bus2);

        // Then
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
    }

    @Test
    void shouldFindBusByBoardNumber() {
        // Given
        String boardNumber = "5678 AB";
        Bus bus = new Bus(boardNumber, "000987", "###test");
        underTest.save(bus);

        // When
        List<Bus> found = underTest.findAll().stream()
                .filter(b -> boardNumber.equals(b.getBoardNumber()))
                .toList();

        // Then
        assertFalse(found.isEmpty());
        assertEquals(boardNumber, found.get(0).getBoardNumber());
    }

    @Test
    void shouldClearDatabaseAfterEachTest() {
        // Given
        // No buses manually saved in this test

        // When
        List<Bus> testBuses = underTest.findAll().stream()
                .filter(b -> "###test".equals(b.getDescription()))
                .toList();

        // Then
        assertTrue(testBuses.isEmpty() || testBuses.size() <= 3);
    }

    @Test
    void shouldGenerateUniqueIds() {
        // Given
        List<Bus> buses = List.of(
                new Bus("6666 HH", "888888", "###test"),
                new Bus("7777 II", "999999", "###test"),
                new Bus("8888 JJ", "101010", "###test")
        );

        // When
        List<Bus> savedBuses = underTest.saveAll(buses);
        List<String> ids = savedBuses.stream().map(Bus::getId).toList();

        // Then
        assertEquals(3, ids.stream().distinct().count());
    }

    @Test
    void shouldUpdateDescriptionField() {
        // Given
        Bus bus = new Bus("9999 KK", "121212", "###test");
        Bus saved = underTest.save(bus);

        // When
        saved.setDescription("###test-updated");
        underTest.save(saved);
        Bus updated = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(updated);
        assertEquals("###test-updated", updated.getDescription());
    }

    @Test
    void shouldFindBusByCode() {
        // Given
        String uniqueCode = "ABC123";
        Bus bus = new Bus("1010 LL", uniqueCode, "###test");
        underTest.save(bus);

        // When
        List<Bus> found = underTest.findAll().stream()
                .filter(b -> uniqueCode.equals(b.getCode()))
                .toList();

        // Then
        assertFalse(found.isEmpty());
        assertEquals(uniqueCode, found.get(0).getCode());
    }

    @Test
    void shouldSaveBusWithSpecialCharactersInBoardNumber() {
        // Given
        Bus bus = new Bus("AB-123", "131313", "###test");

        // When
        Bus saved = underTest.save(bus);
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("AB-123", found.getBoardNumber());
    }

    @Test
    void shouldNotDeleteOtherBusesWhenDeletingOne() {
        // Given
        int initialCount = (int) underTest.count();
        Bus bus = new Bus("1212 MM", "141414", "###test");
        Bus saved = underTest.save(bus);

        // When
        underTest.delete(saved);
        int finalCount = (int) underTest.count();

        // Then
        assertEquals(initialCount, finalCount);
    }

    @Test
    void shouldSaveBusWithLongDescription() {
        // Given
        String longDescription = "This is a very long description for testing purposes. " +
                "It contains multiple sentences to ensure that MongoDB can handle long text fields properly. " +
                "We want to make sure that there are no limitations or truncation issues when storing longer texts. ###test";
        Bus bus = new Bus("1313 NN", "151515", longDescription);

        // When
        Bus saved = underTest.save(bus);
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(longDescription, found.getDescription());
    }

    @Test
    void shouldUpdateMultipleFieldsAtOnce() {
        // Given
        Bus bus = new Bus("1414 OO", "161616", "###test");
        Bus saved = underTest.save(bus);

        // When
        saved.setBoardNumber("9876 ZZ");
        saved.setCode("999888");
        saved.setDescription("###test-multi-update");
        underTest.save(saved);
        Bus updated = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(updated);
        assertEquals("9876 ZZ", updated.getBoardNumber());
        assertEquals("999888", updated.getCode());
        assertEquals("###test-multi-update", updated.getDescription());
    }

    @Test
    void shouldDeleteAllBusesWithTestMarker() {
        // Given
        List<Bus> testBuses = underTest.findAll().stream()
                .filter(b -> b.getDescription().contains("###test"))
                .toList();

        int initialCount = testBuses.size();
        assertTrue(initialCount > 0);

        // When
        underTest.deleteAll(testBuses);

        // Then
        List<Bus> remaining = underTest.findAll().stream()
                .filter(b -> b.getDescription().contains("###test"))
                .toList();
        assertEquals(0, remaining.size());
    }

    @Test
    void shouldCountBusesCorrectly() {
        // Given
        long initialCount = underTest.count();

        // When
        underTest.save(new Bus("1717 RR", "191919", "###test"));
        underTest.save(new Bus("1818 SS", "202020", "###test"));

        // Then
        assertEquals(initialCount + 2, underTest.count());
    }

    @Test
    void shouldFindBusesWithDescriptionContainingText() {
        // Given
        String uniqueMarker = "UNIQUE_MARKER_" + System.currentTimeMillis();
        Bus bus1 = new Bus("1919 TT", "212121", "###test " + uniqueMarker);
        Bus bus2 = new Bus("2020 UU", "222222", "###test different text");
        underTest.saveAll(List.of(bus1, bus2));

        // When
        List<Bus> found = underTest.findAll().stream()
                .filter(b -> b.getDescription().contains(uniqueMarker))
                .toList();

        // Then
        assertEquals(1, found.size());
        assertTrue(found.get(0).getDescription().contains(uniqueMarker));
    }

    @Test
    void shouldDeleteBusByBoardNumber() {
        // Given
        String uniqueBoardNumber = "UNIQUE_" + System.currentTimeMillis();
        Bus bus = new Bus(uniqueBoardNumber, "232323", "###test");
        underTest.save(bus);

        // When
        Bus toDelete = underTest.findAll().stream()
                .filter(b -> uniqueBoardNumber.equals(b.getBoardNumber()))
                .findFirst()
                .orElse(null);
        assertNotNull(toDelete);
        underTest.delete(toDelete);

        // Then
        boolean exists = underTest.findAll().stream()
                .anyMatch(b -> uniqueBoardNumber.equals(b.getBoardNumber()));
        assertFalse(exists);
    }

    @Test
    void shouldExistsBusById() {
        // Given
        Bus bus = new Bus("2121 VV", "242424", "###test");
        Bus saved = underTest.save(bus);
        String id = saved.getId();

        // When
        boolean exists = underTest.existsById(id);

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldNotExistsBusById() {
        // Given
        String nonExistentId = "non-existent-id-" + System.currentTimeMillis();

        // When
        boolean exists = underTest.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }


    @Test
    void shouldHandleConcurrentSaves() {
        // Given
        List<Bus> buses = IntStream.range(0, 10)
                .mapToObj(i -> new Bus("CONCURRENT-" + i, "C" + i, "###test-concurrent"))
                .collect(Collectors.toList());

        // When
        // Using parallel stream to simulate concurrent operations
        buses.parallelStream().forEach(underTest::save);

        // Then
        List<Bus> saved = underTest.findAll().stream()
                .filter(b -> b.getDescription() != null && b.getDescription().equals("###test-concurrent"))
                .toList();
        assertEquals(10, saved.size());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Given
        Bus bus = new Bus("", "", "###test-empty");

        // When
        Bus saved = underTest.save(bus);
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("", found.getBoardNumber());
        assertEquals("", found.getCode());
    }


    @Test
    void shouldHandleLargeNumberOfInserts() {
        // Given
        int batchSize = 100;
        List<Bus> largeBatch = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            largeBatch.add(new Bus("LARGE-" + i, "L" + i, "###test-large"));
        }

        // When
        List<Bus> saved = underTest.saveAll(largeBatch);

        // Then
        assertEquals(batchSize, saved.size());

        // Clean up specific test data to keep test isolation
        underTest.deleteAll(saved);
    }

    @Test
    void shouldReplaceExistingEntityOnSaveWithSameId() {
        // Given
        Bus original = new Bus("ORIGINAL", "O001", "###test-replace");
        Bus saved = underTest.save(original);
        String id = saved.getId();

        // Create new entity with same ID but different data
        Bus replacement = new Bus("REPLACED", "R001", "###test-replace");
        replacement.setId(id);

        // When
        underTest.save(replacement);
        Bus found = underTest.findById(id).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("REPLACED", found.getBoardNumber());
        assertEquals("R001", found.getCode());
    }

    @Test
    void shouldHandleIdsWithSpecialCharacters() {
        // Given
        Bus bus = new Bus("SPECIAL-ID", "S001", "###test-special");
        bus.setId("special/id#with@characters!");

        // When
        Bus saved = underTest.save(bus);

        // Then
        assertNotNull(saved);
        Bus found = underTest.findById("special/id#with@characters!").orElse(null);
        assertNotNull(found);
        assertEquals("SPECIAL-ID", found.getBoardNumber());
    }

    @Test
    void shouldDeleteByIdCorrectly() {
        // Given
        Bus bus = new Bus("DELETE-BY-ID", "D001", "###test-delete-by-id");
        Bus saved = underTest.save(bus);
        String id = saved.getId();

        // When
        underTest.deleteById(id);

        // Then
        assertFalse(underTest.existsById(id));
    }

    @Test
    void shouldDeleteAllClearRepository() {
        // Given
        List<Bus> allBeforeDelete = underTest.findAll();
        int initialCount = allBeforeDelete.size();
        assertTrue(initialCount > 0);

        // When
        underTest.deleteAll();

        // Then
        List<Bus> allAfterDelete = underTest.findAll();
        assertEquals(0, allAfterDelete.size());

        // Restore test data for other tests
        underTest.saveAll(allBeforeDelete);
    }

    @Test
    void shouldHandleEmojiInDescription() {
        // Given
        String descriptionWithEmoji = "Bus with emoji 🚌 🔧 🛣️ ###test";
        Bus bus = new Bus("EMOJI-BUS", "E001", descriptionWithEmoji);

        // When
        Bus saved = underTest.save(bus);
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(descriptionWithEmoji, found.getDescription());
    }

    @Test
    void shouldHandleUnicodeCharactersInFields() {
        // Given
        Bus bus = new Bus("ЮНІКОД", "Ю001", "Опис автобуса з українською мовою ###test");

        // When
        Bus saved = underTest.save(bus);
        Bus found = underTest.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("ЮНІКОД", found.getBoardNumber());
        assertEquals("Ю001", found.getCode());
        assertEquals("Опис автобуса з українською мовою ###test", found.getDescription());
    }

    @Test
    void shouldPreserveFieldOrderAfterMultipleSaves() {
        // Given
        Bus bus = new Bus("ORDER-TEST", "OT001", "###test-field-order");

        // When
        Bus firstSave = underTest.save(bus);
        firstSave.setBoardNumber("UPDATED-ORDER");
        Bus secondSave = underTest.save(firstSave);

        // Then
        Bus found = underTest.findById(secondSave.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("UPDATED-ORDER", found.getBoardNumber());
        assertEquals("OT001", found.getCode());
    }
}
