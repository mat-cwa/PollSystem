package github.com.matcwa.controller;

import github.com.matcwa.api.dto.SuccessResponseDto;
import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.infrastructure.error.ResponseResolver;
import github.com.matcwa.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pollsystem")
public class QuestionController {
    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }


    @PostMapping("/poll/{pollId}/newQuestion")
    public ResponseEntity createNewQuestion(@RequestBody NewQuestionDto newQuestionDto, @PathVariable Long pollId,@RequestHeader("Authorization") String token){
        ErrorHandling<PollDto, QuestionError> newQuestion = questionService.createNewQuestion(newQuestionDto,pollId,token);
        return ResponseResolver.resolve(newQuestion);
    }

    @PutMapping("question/update/{id}")
    public ResponseEntity updateQuestion(@RequestBody NewQuestionDto newQuestionDto, @PathVariable Long id,@RequestHeader("Authorization") String token){
        ErrorHandling<QuestionDto, QuestionError> response = questionService.updateQuestion(newQuestionDto, id,token);
        return ResponseResolver.resolve(response);
    }

    @DeleteMapping("question/{id}")
    public ResponseEntity deletePollById(@PathVariable Long id,@RequestHeader("Authorization") String token) {
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(id, token);
        return ResponseResolver.resolve(response);
    }
}
