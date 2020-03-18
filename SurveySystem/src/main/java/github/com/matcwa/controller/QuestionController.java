package github.com.matcwa.controller;

import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.infrastructure.ResponseResolver;
import github.com.matcwa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class QuestionController {
    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }


    @PostMapping("/poll/{pollId}/newQuestion")
    public ResponseEntity createNewQuestion(@RequestBody NewQuestionDto newQuestionDto, @PathVariable Long pollId){
        ErrorHandling<NewQuestionDto, QuestionError> newQuestion = questionService.createNewQuestion(newQuestionDto,pollId);
        return ResponseResolver.resolve(newQuestion);
    }

    @DeleteMapping("question/{id}")
    public HttpStatus deleteQuestionById(@PathVariable Long id){
        questionService.deleteQuestion(id);
        return HttpStatus.OK;
    }
}
