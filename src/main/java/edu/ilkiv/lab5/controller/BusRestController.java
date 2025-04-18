package edu.ilkiv.lab5.controller;

import edu.ilkiv.lab5.model.Bus;
import edu.ilkiv.lab5.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
  @author Bodya
  @project lab5
  @class BusRestController
  version 1.0.0
  @since 18.04.2025 - 16:39 
*/

@RestController
@RequestMapping("api/v1/buses/")
@RequiredArgsConstructor
public class BusRestController {
    private final BusService busService;


    // CRUD   create read update delete

    // read all
    @RequestMapping
    public List<Bus> showAll() {
        return busService.getAll();
    }

    // read one
    @GetMapping("{id}")
    public Bus showOneById(@PathVariable String id) {
        String str = id;
        return busService.getById(id);
    }

    @PostMapping
    public Bus insert(@RequestBody Bus bus) {
        return busService.create(bus);
    }

    @PutMapping
    public Bus edit(@RequestBody Bus bus) {
        return busService.update(bus);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        busService.delById(id);
    }
}
