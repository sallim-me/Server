package me.sallim.api.domain.appliance_type_question.controller;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.appliance_type_question.model.ApplianceTypeQuestion;
import me.sallim.api.domain.appliance_type_question.repository.ApplianceTypeQuestionRepository;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appliance-questions")
@RequiredArgsConstructor
public class ApplianceTypeQuestionController {

    private final ApplianceTypeQuestionRepository questionRepository;

    @GetMapping("/{applianceType}")
    public ResponseEntity<ApiResponse<List<ApplianceTypeQuestion>>> getQuestions(@PathVariable ApplianceType applianceType) {
        List<ApplianceTypeQuestion> questions = questionRepository.findByApplianceType(applianceType);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
}