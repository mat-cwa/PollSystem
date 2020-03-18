package github.com.matcwa.controller;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.NewAnswerDto;
import github.com.matcwa.api.dto.NewVoteDto;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.infrastructure.ResponseResolver;
import github.com.matcwa.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class AnswerController {
    private AnswerService answerService;
    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/question/{questionId}/newAnswer")
    public ResponseEntity createNewAnswer(@RequestBody NewAnswerDto newAnswerDto, @PathVariable Long questionId){
        ErrorHandling<NewAnswerDto, AnswerError> newAnswer = answerService.createNewAnswer(newAnswerDto, questionId);
        return ResponseResolver.resolve(newAnswer);
    }

    @DeleteMapping("answer/{id}")
    public HttpStatus deleteAnswerById(@PathVariable Long id){
        answerService.deleteAnswer(id);
        return HttpStatus.OK;
    }
    @PostMapping("/answer/{answerId}/addVote")
    public ResponseEntity addVoteToAnswer(@PathVariable Long answerId, @RequestBody NewVoteDto newVoteDto, HttpServletRequest httpRequest){
        ErrorHandling<AnswerDto, AnswerError> addVote = answerService.addVoteToAnswer(answerId, newVoteDto, httpRequest);
        return ResponseResolver.resolve(addVote);
    }
}
