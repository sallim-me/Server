package me.sallim.api.domain.appliance_type_question.repository;

import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.appliance_type_question.model.ApplianceTypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplianceTypeQuestionRepository extends JpaRepository<ApplianceTypeQuestion, Long> {
    List<ApplianceTypeQuestion> findByApplianceType(ApplianceType applianceType);
}
