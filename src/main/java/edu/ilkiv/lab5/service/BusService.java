package edu.ilkiv.lab5.service;

/*
  @author Bodya
  @project lab5
  @class BusService
  version 1.0.0
  @since 18.04.2025 - 16:40
*/

import edu.ilkiv.lab5.model.Bus;
import edu.ilkiv.lab5.repository.BusRepository;
import edu.ilkiv.lab5.request.BusCreateRequest;
import edu.ilkiv.lab5.request.BusUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    List<Bus> buses = new ArrayList<>();
    {
        buses.add(new Bus("1", "1234 CE", "000001","description1"));
        buses.add(new Bus("2", "4323 AE", "000002","description3"));
        buses.add(new Bus("3", "9423 MO", "000003","description3"));
    }

    @PostConstruct
    void init() {
        busRepository.saveAll(buses);
    }
    //  CRUD   - create read update delete

    public List<Bus> getAll() {
        return busRepository.findAll();
    }

    public Bus getById(String id) {
        return busRepository.findById(id).orElse(null);
    }


    private Bus mapToBus(BusCreateRequest request) {
        Bus item = new Bus(request.boardNumber(), request.code(), request.description());
        return item;
    }

    public Bus create(BusCreateRequest request) {
        if (busRepository.existsByCode(request.code())) {
            return null;
        }
        Bus item = mapToBus(request);
        return busRepository.save(item);
    }

    public Bus create(Bus item) {

        return busRepository.save(item);
    }

    public  Bus update(Bus item) {
        return busRepository.save(item);
    }

    public Bus update(BusUpdateRequest request) {
        Bus bus = busRepository.findById(request.id()).orElse(new Bus());
        bus.setId(request.id());
        bus.setBoardNumber(request.boardNumber());
        bus.setCode(request.code());
        bus.setDescription(request.description());
        return busRepository.save(bus);
    }



    public void delById(String id) {
        busRepository.deleteById(id);
    }
}