package com.angelo.certification_nlw.modules.students.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angelo.certification_nlw.modules.students.dto.StudentCertificationAnswersDTO;
import com.angelo.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.angelo.certification_nlw.modules.students.useCases.StundentCertificationAnswersUseCase;
import com.angelo.certification_nlw.modules.students.useCases.VerifyIfHasCertificationUseCase;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    @Autowired
    private StundentCertificationAnswersUseCase stundentCertificationAnswersUseCase;

    @PostMapping("/verifyIfHasCertification")
    public String verifyIfHasCertification(@RequestBody VerifyHasCertificationDTO verifyHasCertificationDTO){

        var result = this.verifyIfHasCertificationUseCase.execute(verifyHasCertificationDTO);

        if(result){
            return "Usuário já fez a prova";
        }
        return "Usuário pode fazer a prova";
    }

    @PostMapping("/certification/answer")
    public ResponseEntity<Object> certificationAnswers(@RequestBody StudentCertificationAnswersDTO dto) throws Exception{        
        try {
            var result = stundentCertificationAnswersUseCase.execute(dto);    
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}