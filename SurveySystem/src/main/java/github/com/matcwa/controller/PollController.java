package github.com.matcwa.controller;

import github.com.matcwa.api.dto.SuccessResponseDto;
import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.error.*;
import github.com.matcwa.infrastructure.ResponseResolver;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pollsystem")
public class PollController {
    private PollService pollService;


    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }



    @PostMapping("/newPoll")
    public ResponseEntity createNewPoll(@RequestBody NewPollDto newPollDto,@RequestHeader("Authorization") String token) {
        ErrorHandling<PollDto, PollError> poll = pollService.addNewPoll(newPollDto,token);
        return ResponseResolver.resolve(poll);
    }

    @PutMapping("poll/update/{id}")
    public ResponseEntity updatePoll(@RequestBody NewPollDto newPollDto, @PathVariable Long id,@RequestHeader("Authorization") String token){
        ErrorHandling<PollDto, PollError> response = pollService.updatePoll(newPollDto, id,token);
        return ResponseResolver.resolve(response);
    }

    @GetMapping("poll/all")
    public List<PollDto> getAll() {
        return pollService.getAll();
    }


    @GetMapping("/poll/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        ErrorHandling<PollDto, PollError> pollById = pollService.getPollById(id);
        return ResponseResolver.resolve(pollById);
    }


    @DeleteMapping("poll/{id}")
    public ResponseEntity deletePollById(@PathVariable Long id,@RequestHeader("Authorization") String token) {
        ErrorHandling<SuccessResponseDto, PollError> response = pollService.deletePoll(id, token);
        return ResponseResolver.resolve(response);
    }
}

