package edu.ilkiv.lab5.model;

/*
  @author Bodya
  @project lab5
  @class Bus
  version 1.0.0
  @since 18.04.2025 - 16:39 
*/

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document

public class Bus {
    private String id;
    private String boardNumber;
    private String code;
    private String description;

    public Bus(String boardNumber, String code, String description) {
        this.boardNumber = boardNumber;
        this.code = code;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bus bus = (Bus) o;
        return getId().equals(bus.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
