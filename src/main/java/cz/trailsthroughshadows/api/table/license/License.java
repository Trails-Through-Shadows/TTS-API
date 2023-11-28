package cz.trailsthroughshadows.api.table.license;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "License")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "key", nullable = false, length = 20)
    private String key;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "activated")
    private LocalDateTime activated;

}
