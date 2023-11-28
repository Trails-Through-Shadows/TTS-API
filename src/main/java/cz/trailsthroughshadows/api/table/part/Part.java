package cz.trailsthroughshadows.api.table.part;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Part")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(length = 30)
    private String tag;
}

