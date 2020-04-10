package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.error.*;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PollService {
    private PollRepository pollRepository;

    @Autowired
    public PollService(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }


    public List<PollDto> getAll() {
        List<Poll> all = pollRepository.findAll();
        return all.stream().map(PollMapper::toDto).collect(Collectors.toList());
    }


    public ErrorHandling<PollDto, PollError> getPollById(Long id) {
        ErrorHandling<PollDto,PollError> response=new ErrorHandling<>();
        pollRepository.findById(id).ifPresentOrElse(poll -> {
            PollDto pollDto = PollMapper.toDto(poll);
            response.setDto(pollDto);
            },()-> response.setError(PollError.POLL_NOT_FOUND_ERROR));
    return response;
    }

    @Transactional
    public ErrorHandling<NewPollDto, PollError> addNewPoll(NewPollDto newPollDto) {
        ErrorHandling<NewPollDto, PollError> pollDto = validatePoll(newPollDto);
        if (pollDto.getDto() != null) {
            Poll pollSource = PollMapper.newToSource(newPollDto);
            pollRepository.save(pollSource);
        }
        return pollDto;
    }

    public ErrorHandling<PollDto, PollError> updatePoll(NewPollDto newPollDto, Long id){
        ErrorHandling<PollDto, PollError> response=new ErrorHandling<>();
        pollRepository.findById(id).ifPresentOrElse(poll -> {
        if (newPollDto.getName()!=null && !newPollDto.getName().isEmpty()){
            poll.setName(newPollDto.getName());
            response.setDto(PollMapper.toDto(poll));
        }
        else response.setDto(PollMapper.toDto(poll));
        },()->response.setError(PollError.POLL_NOT_FOUND_ERROR));
        return response;
    }


    public void deletePoll(Long id) {
        pollRepository.deleteById(id);
    }


    private ErrorHandling<NewPollDto, PollError> validatePoll(NewPollDto newPollDto) {
        ErrorHandling<NewPollDto, PollError> poll = new ErrorHandling<>();
        if (newPollDto.getName() == null || newPollDto.getName().isEmpty() ) {
            poll.setError(PollError.WRONG_NAME_ERROR);
        } else {
            poll.setDto(newPollDto);
        }
        return poll;
    }
}