package com.angelo.certification_nlw.modules.students.useCases;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelo.certification_nlw.modules.questions.entities.QuestionEntity;
import com.angelo.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.angelo.certification_nlw.modules.students.dto.StudentCertificationAnswersDTO;

@Service
public class StundentCertificationAnswersUseCase {

    @Autowired
    private QuestionRepository questionRepository;

    public StudentCertificationAnswersDTO execute(StudentCertificationAnswersDTO dto){        

        List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());

        dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
            var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionId()))
            .findFirst().get();

            var findCorrectAlternative = question.getAlternatives().stream()
            .filter(alternative -> alternative.isCorrect()).findFirst().get();

            if(findCorrectAlternative.getId().equals(questionAnswer.getAlternativeId())){
                questionAnswer.setIsCorrect(true);
            }else{
                questionAnswer.setIsCorrect(false);
            }            
        });
        return dto;
    }
}