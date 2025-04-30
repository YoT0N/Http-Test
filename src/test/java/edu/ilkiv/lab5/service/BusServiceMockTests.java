package edu.ilkiv.lab5.service;

import edu.ilkiv.lab5.model.Bus;
import edu.ilkiv.lab5.repository.BusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
class BusServiceTests {
    @Mock
    private BusRepository mockRepository;

    private BusService underTest;

    @Captor
    private ArgumentCaptor<Bus> busCaptor;

    private Bus testBus1;
    private Bus testBus2;
    private Bus testBus3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new BusService(mockRepository);

        // Create test buses
        testBus1 = new Bus("1", "1234 CE", "000001", "description1");
        testBus2 = new Bus("2", "4323 AE", "000002", "description2");
        testBus3 = new Bus("3", "9423 MO", "000003", "description3");
    }

    @AfterEach
    void tearDown() {
        // No need to clean up with mocks
    }

    @Test
    @DisplayName("Test initializing buses on startup")
    void testInitMethodSavesBusesToRepository() {
        // when
        underTest.init();

        // then
        verify(mockRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("GetAll should return all buses from repository")
    void testGetAllReturnsBusesFromRepository() {
        // given
        List<Bus> expectedBuses = Arrays.asList(testBus1, testBus2, testBus3);
        given(mockRepository.findAll()).willReturn(expectedBuses);

        // when
        List<Bus> actualBuses = underTest.getAll();

        // then
        assertThat(actualBuses).isEqualTo(expectedBuses);
        verify(mockRepository).findAll();
    }

    @Test
    @DisplayName("GetById should return bus when it exists")
    void testGetByIdReturnsBusWhenExists() {
        // given
        String id = "1";
        given(mockRepository.findById(id)).willReturn(Optional.of(testBus1));

        // when
        Bus result = underTest.getById(id);

        // then
        assertThat(result).isEqualTo(testBus1);
        verify(mockRepository).findById(id);
    }

    @Test
    @DisplayName("GetById should return null when bus doesn't exist")
    void testGetByIdReturnsNullWhenBusDoesNotExist() {
        // given
        String id = "999";
        given(mockRepository.findById(id)).willReturn(Optional.empty());

        // when
        Bus result = underTest.getById(id);

        // then
        assertNull(result);
        verify(mockRepository).findById(id);
    }

    @Test
    @DisplayName("Create should save bus to repository")
    void testCreateSavesBusToRepository() {
        // given
        Bus busToCreate = new Bus("4", "5555 AA", "000004", "description4");
        given(mockRepository.save(busToCreate)).willReturn(busToCreate);

        // when
        Bus result = underTest.create(busToCreate);

        // then
        assertThat(result).isEqualTo(busToCreate);
        verify(mockRepository).save(busToCreate);
    }

    @Test
    @DisplayName("Update should save changes to repository")
    void testUpdateSavesBusToRepository() {
        // given
        Bus busToUpdate = new Bus("1", "1234 CE", "000001", "updated description");
        given(mockRepository.save(busToUpdate)).willReturn(busToUpdate);

        // when
        Bus result = underTest.update(busToUpdate);

        // then
        assertThat(result).isEqualTo(busToUpdate);
        verify(mockRepository).save(busToUpdate);
    }

    @Test
    @DisplayName("Delete should remove bus by id")
    void testDeleteRemovesBusById() {
        // given
        String id = "1";

        // when
        underTest.delById(id);

        // then
        verify(mockRepository).deleteById(id);
    }

    @Test
    @DisplayName("GetAll should return empty list when repository is empty")
    void testGetAllReturnsEmptyListWhenRepositoryIsEmpty() {
        // given
        given(mockRepository.findAll()).willReturn(List.of());

        // when
        List<Bus> result = underTest.getAll();

        // then
        assertThat(result).isEmpty();
        verify(mockRepository).findAll();
    }

    @Test
    @DisplayName("Create should assign correct fields to bus")
    void testCreateAssignsCorrectFieldsToBus() {
        // given
        Bus busToCreate = new Bus(null, "7777 BB", "000007", "description7");
        Bus savedBus = new Bus("7", "7777 BB", "000007", "description7");
        given(mockRepository.save(any(Bus.class))).willReturn(savedBus);

        // when
        underTest.create(busToCreate);

        // then
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getBoardNumber()).isEqualTo("7777 BB");
        assertThat(capturedBus.getCode()).isEqualTo("000007");
        assertThat(capturedBus.getDescription()).isEqualTo("description7");
    }

    @Test
    @DisplayName("Update should not change id of bus")
    void testUpdateDoesNotChangeId() {
        // given
        Bus originalBus = new Bus("10", "8888 CC", "000010", "original description");
        Bus updatedBus = new Bus("10", "8888 CC", "000010", "updated description");
        given(mockRepository.save(updatedBus)).willReturn(updatedBus);

        // when
        Bus result = underTest.update(updatedBus);

        // then
        assertThat(result.getId()).isEqualTo(originalBus.getId());
        verify(mockRepository).save(updatedBus);
    }

    @Test
    @DisplayName("Create should use repository to save")
    void testCreateUsesSaveMethodOfRepository() {
        // given
        Bus busToCreate = new Bus(null, "9999 DD", "000009", "description9");

        // when
        underTest.create(busToCreate);

        // then
        verify(mockRepository, times(1)).save(busToCreate);
    }

    @Test
    @DisplayName("Update should use repository to save")
    void testUpdateUsesSaveMethodOfRepository() {
        // given
        Bus busToUpdate = new Bus("11", "1111 EE", "000011", "updated description");

        // when
        underTest.update(busToUpdate);

        // then
        verify(mockRepository, times(1)).save(busToUpdate);
    }

    @Test
    @DisplayName("DelById should use repository to delete")
    void testDelByIdUsesDeleteByIdOfRepository() {
        // given
        String id = "12";

        // when
        underTest.delById(id);

        // then
        verify(mockRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("GetAll should return in the same order as repository")
    void testGetAllPreservesOrderFromRepository() {
        // given
        List<Bus> expectedBuses = Arrays.asList(testBus1, testBus2, testBus3);
        given(mockRepository.findAll()).willReturn(expectedBuses);

        // when
        List<Bus> actualBuses = underTest.getAll();

        // then
        assertThat(actualBuses).containsExactly(testBus1, testBus2, testBus3);
    }

    @Test
    @DisplayName("Create should handle bus with all null values")
    void testCreateHandlesBusWithNullValues() {
        // given
        Bus busWithNulls = new Bus();

        // when
        underTest.create(busWithNulls);

        // then
        verify(mockRepository).save(busWithNulls);
    }

    @Test
    @DisplayName("Update should maintain audit data")
    void testUpdateMaintainsAuditData() {
        // given
        Bus busToUpdate = new Bus("13", "1313 FF", "000013", "initial description");

        // Create spy to verify service behavior
        BusService serviceSpy = spy(underTest);

        // when
        serviceSpy.update(busToUpdate);

        // then
        verify(mockRepository).save(busToUpdate);
        // Verify the service doesn't modify the bus before saving
        verify(serviceSpy, times(1)).update(busToUpdate);
    }

    @Test
    @DisplayName("GetById should call repository with correct id")
    void testGetByIdCallsRepositoryWithCorrectId() {
        // given
        String id = "14";
        given(mockRepository.findById(anyString())).willReturn(Optional.empty());

        // when
        underTest.getById(id);

        // then
        verify(mockRepository).findById(id);
    }

    @Test
    @DisplayName("Initial bus list should contain three elements")
    void testInitialBusListHasThreeElements() {
        // when
        List<Bus> initialBuses = underTest.buses;

        // then
        assertThat(initialBuses).hasSize(3);
    }

    @Test
    @DisplayName("PostConstruct init method should call saveAll")
    void testPostConstructInitMethodCallsSaveAll() {
        // when
        underTest.init();

        // then
        verify(mockRepository).saveAll(underTest.buses);
    }
    

    @Test
    @DisplayName("Service should handle empty input when creating bus")
    void testCreateHandlesEmptyInput() {
        // given
        Bus emptyBus = new Bus("", "", "");

        // when
        underTest.create(emptyBus);

        // then
        verify(mockRepository).save(emptyBus);
    }

    @Test
    @DisplayName("Service should handle null input when creating bus")
    void testCreateHandlesNullInput() {
        // when
        assertDoesNotThrow(() -> underTest.create(null));
    }

    @Test
    @DisplayName("Service should handle null input when updating bus")
    void testUpdateHandlesNullInput() {
        // when
        assertDoesNotThrow(() -> underTest.update(null));
    }

    @Test
    @DisplayName("Service should handle null input when getting bus by id")
    void testGetByIdHandlesNullInput() {
        // when
        Bus result = underTest.getById(null);

        // then
        assertNull(result);
        verify(mockRepository).findById(null);
    }

    @Test
    @DisplayName("Test initial bus list has expected board numbers")
    void testInitialBusListHasExpectedBoardNumbers() {
        // when
        List<String> boardNumbers = underTest.buses.stream()
                .map(Bus::getBoardNumber)
                .toList();

        // then
        assertThat(boardNumbers).containsExactly("1234 CE", "4323 AE", "9423 MO");
    }

    @Test
    @DisplayName("Test create returns the same bus that was saved")
    void testCreateReturnsSameBusThatWasSaved() {
        // given
        Bus busToCreate = new Bus("5", "5555 XY", "000005", "description5");
        given(mockRepository.save(any(Bus.class))).willAnswer(i -> i.getArgument(0));

        // when
        Bus result = underTest.create(busToCreate);

        // then
        assertThat(result).isSameAs(busToCreate);
    }

    @Test
    @DisplayName("Test update returns the same bus that was saved")
    void testUpdateReturnsSameBusThatWasSaved() {
        // given
        Bus busToUpdate = new Bus("5", "5555 XY", "000005", "updated description");
        given(mockRepository.save(any(Bus.class))).willAnswer(i -> i.getArgument(0));

        // when
        Bus result = underTest.update(busToUpdate);

        // then
        assertThat(result).isSameAs(busToUpdate);
    }

    @Test
    @DisplayName("Test delById does nothing for non-existent ID")
    void testDelByIdDoesNothingForNonExistentId() {
        // given
        String nonExistentId = "999";
        doThrow(new RuntimeException("ID not found")).when(mockRepository).deleteById(nonExistentId);

        // when & then
        assertThrows(RuntimeException.class, () -> underTest.delById(nonExistentId));
    }
}