package me.sallim.api.domain.appliance_type_question.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.appliance.ApplianceType;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "appliance_type_question")
public class ApplianceTypeQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "appliance_type", nullable = false)
    private ApplianceType applianceType;

    @Column(name = "question_content", nullable = false, length = 1024)
    private String questionContent;
}
