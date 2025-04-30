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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Bus create(Bus bus) {
        return busRepository.save(bus);
    }

    public  Bus update(Bus bus) {
        return busRepository.save(bus);
    }

    public void delById(String id) {
        busRepository.deleteById(id);
    }
}
