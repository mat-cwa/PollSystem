package github.com.matcwa.controller;

import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.error.*;
import github.com.matcwa.infrastructure.ResponseResolver;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PollController {
    private PollService pollService;


    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;

    }
    @PostMapping("/newPoll")
    public ResponseEntity createNowPoll(@RequestBody NewPollDto newPollDto) {
        ErrorHandling<NewPollDto, PollError> poll = pollService.addNewPoll(newPollDto);
        return ResponseResolver.resolve(poll);
    }

    @GetMapping("/all")
    public List<PollDto> getAll(){
        return pollService.getAll();
    }


    @GetMapping("/poll/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        Optional<PollDto> pollById = pollService.getPollById(id);
        return ResponseResolver.resolve(pollById);
    }


    @DeleteMapping("poll/{id}")
    public HttpStatus deletePollById(@PathVariable Long id){
        pollService.deletePoll(id);
        return HttpStatus.OK;
    }
}

