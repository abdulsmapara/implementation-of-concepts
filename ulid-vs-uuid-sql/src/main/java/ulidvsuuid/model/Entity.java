package ulidvsuuid.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Getter
@jakarta.persistence.Entity
@Table
@NoArgsConstructor
public class Entity implements Serializable {
    @Id
    String id;

    public Entity(String id) {
        this.id = id;
    }

}
