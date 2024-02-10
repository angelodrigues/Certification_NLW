package com.angelo.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelo.certification_nlw.modules.questions.entities.QuestionEntity;
import com.angelo.certification_nlw.modules.questions.repositories.QuestionRepository;
import com.angelo.certification_nlw.modules.students.dto.StudentCertificationAnswersDTO;
import com.angelo.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.angelo.certification_nlw.modules.students.entities.AnswersCertificationsEntity;
import com.angelo.certification_nlw.modules.students.entities.CertificationStudentEntity;
import com.angelo.certification_nlw.modules.students.entities.StudentEntity;
import com.angelo.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import com.angelo.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StundentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository; 

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswersDTO dto) throws Exception{   
        
        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(),dto.getTechnology()));

        if(hasCertification){
            throw new Exception("Você já tirou sua certificação.");
        }

        List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();
        
        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
            var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionId()))
            .findFirst().get();

            var findCorrectAlternative = question.getAlternatives().stream()
            .filter(alternative -> alternative.isCorrect()).findFirst().get();

            if(findCorrectAlternative.getId().equals(questionAnswer.getAlternativeId())){
                questionAnswer.setIsCorrect(true);
                correctAnswers.incrementAndGet();
            }else{
                questionAnswer.setIsCorrect(false);
            }  
            
            var answerCertificationsEntity = AnswersCertificationsEntity.builder()
            .answerID(questionAnswer.getAlternativeId())
            .questionID(questionAnswer.getQuestionId())
            .isCorrect(questionAnswer.getIsCorrect()).build();


            answersCertifications.add(answerCertificationsEntity);
        });

        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentID;
        if(student.isEmpty()){
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        }else{
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = 
            CertificationStudentEntity.builder()
            .technology(dto.getTechnology())
            .studentID(studentID)
            .grade(correctAnswers.get())
            .build();

        var certificationStudentRepositoryCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answersCertification -> {
            answersCertification.setCertificationID(certificationStudentEntity.getId());
            answersCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertifications);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentRepositoryCreated;
    }
}