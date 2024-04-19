package cz.trailsthroughshadows.api.table.playerdata.license;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "License")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class License {
    @Id
    private Integer id;

    @Column(nullable = false, length = 20)
    private String key;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime activated;

}
