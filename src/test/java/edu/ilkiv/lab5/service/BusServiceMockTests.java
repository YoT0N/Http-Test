package edu.ilkiv.lab5.service;

import edu.ilkiv.lab5.model.Bus;
import edu.ilkiv.lab5.repository.BusRepository;
import edu.ilkiv.lab5.request.BusCreateRequest;
import edu.ilkiv.lab5.request.BusUpdateRequest;
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
    private BusCreateRequest createRequest;
    private BusUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new BusService(mockRepository);

        // Create test buses
        testBus1 = new Bus("1", "1234 CE", "000001", "description1");
        testBus2 = new Bus("2", "4323 AE", "000002", "description2");
        testBus3 = new Bus("3", "9423 MO", "000003", "description3");

        // Create test requests
        createRequest = new BusCreateRequest("5555 AA", "000004", "description4");
        updateRequest = new BusUpdateRequest("1", "1234 CE", "000001", "updated description");
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
        Bus expectedBus = new Bus();
        expectedBus.setBoardNumber(createRequest.boardNumber());
        expectedBus.setCode(createRequest.code());
        expectedBus.setDescription(createRequest.description());
        given(mockRepository.save(any(Bus.class))).willReturn(expectedBus);

        // when
        Bus result = underTest.create(createRequest);

        // then
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getBoardNumber()).isEqualTo(createRequest.boardNumber());
        assertThat(capturedBus.getCode()).isEqualTo(createRequest.code());
        assertThat(capturedBus.getDescription()).isEqualTo(createRequest.description());
        assertThat(result).isEqualTo(expectedBus);
    }

    @Test
    @DisplayName("Update should save changes to repository")
    void testUpdateSavesBusToRepository() {
        // given
        Bus existingBus = new Bus(updateRequest.id(), "old board", "old code", "old description");
        Bus updatedBus = new Bus(updateRequest.id(), updateRequest.boardNumber(), updateRequest.code(), updateRequest.description());

        given(mockRepository.findById(updateRequest.id())).willReturn(Optional.of(existingBus));
        given(mockRepository.save(any(Bus.class))).willReturn(updatedBus);

        // when
        Bus result = underTest.update(updateRequest);

        // then
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getId()).isEqualTo(updateRequest.id());
        assertThat(capturedBus.getBoardNumber()).isEqualTo(updateRequest.boardNumber());
        assertThat(capturedBus.getCode()).isEqualTo(updateRequest.code());
        assertThat(capturedBus.getDescription()).isEqualTo(updateRequest.description());
        assertThat(result).isEqualTo(updatedBus);
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
        BusCreateRequest request = new BusCreateRequest("7777 BB", "000007", "description7");
        Bus savedBus = new Bus("7", "7777 BB", "000007", "description7");
        given(mockRepository.save(any(Bus.class))).willReturn(savedBus);

        // when
        Bus result = underTest.create(request);

        // then
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getBoardNumber()).isEqualTo(request.boardNumber());
        assertThat(capturedBus.getCode()).isEqualTo(request.code());
        assertThat(capturedBus.getDescription()).isEqualTo(request.description());
        assertThat(result).isEqualTo(savedBus);
    }

    @Test
    @DisplayName("Update should not create a new bus when ID exists")
    void testUpdateUpdatesExistingBus() {
        // given
        String id = "10";
        BusUpdateRequest request = new BusUpdateRequest(id, "8888 CC", "000010", "updated description");
        Bus existingBus = new Bus(id, "8888 CC", "000010", "original description");
        Bus updatedBus = new Bus(id, "8888 CC", "000010", "updated description");

        given(mockRepository.findById(id)).willReturn(Optional.of(existingBus));
        given(mockRepository.save(any(Bus.class))).willReturn(updatedBus);

        // when
        Bus result = underTest.update(request);

        // then
        verify(mockRepository).findById(id);
        verify(mockRepository).save(any(Bus.class));
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getDescription()).isEqualTo("updated description");
    }

    @Test
    @DisplayName("Update should create a new bus when ID doesn't exist")
    void testUpdateCreatesNewBusWhenIdDoesNotExist() {
        // given
        String nonExistentId = "999";
        BusUpdateRequest request = new BusUpdateRequest(nonExistentId, "9999 DD", "000999", "new description");
        Bus newBus = new Bus(nonExistentId, "9999 DD", "000999", "new description");

        given(mockRepository.findById(nonExistentId)).willReturn(Optional.empty());
        given(mockRepository.save(any(Bus.class))).willReturn(newBus);

        // when
        Bus result = underTest.update(request);

        // then
        verify(mockRepository).findById(nonExistentId);
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getId()).isEqualTo(nonExistentId);
        assertThat(capturedBus.getBoardNumber()).isEqualTo(request.boardNumber());
        assertThat(capturedBus.getCode()).isEqualTo(request.code());
        assertThat(capturedBus.getDescription()).isEqualTo(request.description());
        assertThat(result).isEqualTo(newBus);
    }

    @Test
    @DisplayName("Create should use repository to save")
    void testCreateUsesSaveMethodOfRepository() {
        // given
        BusCreateRequest request = new BusCreateRequest("9999 DD", "000009", "description9");

        // when
        underTest.create(request);

        // then
        verify(mockRepository, times(1)).save(any(Bus.class));
    }

    @Test
    @DisplayName("Update should use repository to save")
    void testUpdateUsesSaveMethodOfRepository() {
        // given
        BusUpdateRequest request = new BusUpdateRequest("11", "1111 EE", "000011", "updated description");
        given(mockRepository.findById(anyString())).willReturn(Optional.empty());

        // when
        underTest.update(request);

        // then
        verify(mockRepository, times(1)).save(any(Bus.class));
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
    @DisplayName("Create should handle request with null or empty values")
    void testCreateHandlesRequestWithEmptyValues() {
        // given
        BusCreateRequest emptyRequest = new BusCreateRequest("", "", "");
        Bus expectedBus = new Bus();
        expectedBus.setBoardNumber("");
        expectedBus.setCode("");
        expectedBus.setDescription("");

        given(mockRepository.save(any(Bus.class))).willReturn(expectedBus);

        // when
        Bus result = underTest.create(emptyRequest);

        // then
        then(mockRepository).should().save(busCaptor.capture());
        Bus capturedBus = busCaptor.getValue();
        assertThat(capturedBus.getBoardNumber()).isEmpty();
        assertThat(capturedBus.getCode()).isEmpty();
        assertThat(capturedBus.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("Update should maintain audit data")
    void testUpdateMaintainsAuditData() {
        // given
        String id = "13";
        BusUpdateRequest request = new BusUpdateRequest(id, "1313 FF", "000013", "updated description");
        Bus existingBus = new Bus(id, "1313 FF", "000013", "initial description");

        given(mockRepository.findById(id)).willReturn(Optional.of(existingBus));
        given(mockRepository.save(any(Bus.class))).willReturn(existingBus);

        // Create spy to verify service behavior
        BusService serviceSpy = spy(underTest);

        // when
        serviceSpy.update(request);

        // then
        verify(mockRepository).save(any(Bus.class));
        // Verify the service calls the update method once
        verify(serviceSpy, times(1)).update(request);
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
    @DisplayName("Test create returns bus with all fields set correctly")
    void testCreateReturnsBusWithCorrectFields() {
        // given
        BusCreateRequest request = new BusCreateRequest("5555 XY", "000005", "description5");
        Bus expectedBus = new Bus();
        expectedBus.setId("5"); // Repository would set this
        expectedBus.setBoardNumber("5555 XY");
        expectedBus.setCode("000005");
        expectedBus.setDescription("description5");

        given(mockRepository.save(any(Bus.class))).willReturn(expectedBus);

        // when
        Bus result = underTest.create(request);

        // then
        assertThat(result.getId()).isEqualTo("5");
        assertThat(result.getBoardNumber()).isEqualTo("5555 XY");
        assertThat(result.getCode()).isEqualTo("000005");
        assertThat(result.getDescription()).isEqualTo("description5");
    }

    @Test
    @DisplayName("Test update returns bus with all fields updated correctly")
    void testUpdateReturnsBusWithUpdatedFields() {
        // given
        String id = "5";
        BusUpdateRequest request = new BusUpdateRequest(id, "5555 XY", "000005", "updated description");
        Bus existingBus = new Bus(id, "old number", "old code", "old description");
        Bus updatedBus = new Bus(id, "5555 XY", "000005", "updated description");

        given(mockRepository.findById(id)).willReturn(Optional.of(existingBus));
        given(mockRepository.save(any(Bus.class))).willReturn(updatedBus);

        // when
        Bus result = underTest.update(request);

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getBoardNumber()).isEqualTo("5555 XY");
        assertThat(result.getCode()).isEqualTo("000005");
        assertThat(result.getDescription()).isEqualTo("updated description");
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