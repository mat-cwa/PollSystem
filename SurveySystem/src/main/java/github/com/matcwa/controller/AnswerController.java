package github.com.matcwa.controller;

import github.com.matcwa.api.dto.*;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.infrastructure.error.ResponseResolver;
import github.com.matcwa.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pollsystem")
public class AnswerController {
    private AnswerService answerService;
    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/question/{questionId}/newAnswer")
    public ResponseEntity createNewAnswer(@RequestBody NewAnswerDto newAnswerDto, @PathVariable Long questionId,@RequestHeader("Authorization") String token){
        ErrorHandling<QuestionDto, AnswerError> newAnswer = answerService.createNewAnswer(newAnswerDto, questionId,token);
        return ResponseResolver.resolve(newAnswer);
    }

    @PutMapping("answer/update/{id}")
    public ResponseEntity updateAnswer(@RequestBody NewAnswerDto newAnswerDto, @PathVariable Long id,@RequestHeader("Authorization") String token){
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto, id,token);
        return ResponseResolver.resolve(response);
    }

    @DeleteMapping("answer/{id}")
    public ResponseEntity deletePollById(@PathVariable Long id,@RequestHeader("Authorization") String token) {
        ErrorHandling<SuccessResponseDto, AnswerError> response = answerService.deleteAnswer(id, token);
        return ResponseResolver.resolve(response);
    }
    @PostMapping("/answer/{answerId}/addVote")
    public ResponseEntity addVoteToAnswer(@PathVariable Long answerId, HttpServletRequest httpRequest){
        ErrorHandling<AnswerDto, AnswerError> addVote = answerService.addVoteToAnswer(answerId, httpRequest);
        return ResponseResolver.resolve(addVote);
    }
}
