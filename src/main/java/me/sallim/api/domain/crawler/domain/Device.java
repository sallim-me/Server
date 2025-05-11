package me.sallim.api.domain.crawler.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String category;
    private String modelName;
    private String deviceName;
    private Integer price;

    @Column(name = "device_option")
    private String option;
    private String date;
}
