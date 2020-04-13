package github.com.matcwa.service;

import github.com.matcwa.api.dto.DeleteSuccessResponseDto;
import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.error.*;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.model.Role;
import github.com.matcwa.model.User;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PollService {
    private PollRepository pollRepository;
    private UserRepository userRepository;
    private TokenService tokenService;

    @Autowired
    public PollService(PollRepository pollRepository, UserRepository userRepository, TokenService tokenService) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }


    public List<PollDto> getAll() {
        List<Poll> all = pollRepository.findAll();
        return all.stream().map(PollMapper::toDto).collect(Collectors.toList());
    }


    public ErrorHandling<PollDto, PollError> getPollById(Long id) {
        ErrorHandling<PollDto, PollError> response = new ErrorHandling<>();
        pollRepository.findById(id).ifPresentOrElse(poll -> {
            PollDto pollDto = PollMapper.toDto(poll);
            response.setDto(pollDto);
        }, () -> response.setError(PollError.POLL_NOT_FOUND_ERROR));
        return response;
    }

    @Transactional
    public ErrorHandling<PollDto, PollError> addNewPoll(NewPollDto newPollDto, String token) {
        ErrorHandling<PollDto, PollError> pollDto = new ErrorHandling<>();
        if (tokenService.getRoleFromToken(token).equals(Role.USER.name()) || tokenService.getRoleFromToken(token).equals(Role.ADMIN.name())) {
            pollDto = validatePoll(newPollDto);
            if (pollDto.getError() == null) {
                Optional<User> userOptional = userRepository.findByUsername(tokenService.getUsernameFromToken(token));
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    Poll pollSource = PollMapper.newToSource(newPollDto);
                    pollSource.setOwner(user);
                    user.addPoll(pollSource);
                    pollRepository.save(pollSource);
                    pollDto.setDto(PollMapper.toDto(pollSource));
                } else {
                    pollDto.setError(PollError.USER_NOT_FOUND);
                }
            }
        } else {
            pollDto.setError(PollError.AUTHORIZATION_ERROR);
        }
        return pollDto;
    }

    public ErrorHandling<PollDto, PollError> updatePoll(NewPollDto newPollDto, Long id, String token) {
        ErrorHandling<PollDto, PollError> response = new ErrorHandling<>();
        pollRepository.findById(id).ifPresentOrElse(poll -> {
            Optional<User> userOptional = userRepository.findByUsername(tokenService.getUsernameFromToken(token));
            if (userOptional.isPresent()) {
                if (poll.getOwner() == userOptional.get() || tokenService.getRoleFromToken(token).equals(Role.ADMIN.name())) {
                    if (newPollDto.getName() != null && !newPollDto.getName().isEmpty()) {
                        poll.setName(newPollDto.getName());
                        response.setDto(PollMapper.toDto(poll));
                    } else {
                        response.setDto(PollMapper.toDto(poll));
                    }
                } else {
                    response.setError(PollError.AUTHORIZATION_ERROR);
                }
            } else {
                response.setError(PollError.USER_NOT_FOUND);
            }
        }, () -> response.setError(PollError.POLL_NOT_FOUND_ERROR));
        return response;
    }


    public ErrorHandling<DeleteSuccessResponseDto, PollError> deletePoll(Long id, String token) {
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = new ErrorHandling<>();
        pollRepository.findById(id).ifPresentOrElse(poll -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                if (poll.getOwner() == user || tokenService.getRoleFromToken(token).equals("ADMIN")) {
                    pollRepository.deleteById(id);
                    response.setDto(new DeleteSuccessResponseDto("Successful!"));
                } else {
                    response.setError(PollError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(PollError.USER_NOT_FOUND));
        }, () -> response.setError(PollError.POLL_NOT_FOUND_ERROR));
    return response;
    }


    private ErrorHandling<PollDto, PollError> validatePoll(NewPollDto newPollDto) {
        ErrorHandling<PollDto, PollError> poll = new ErrorHandling<>();
        if (newPollDto.getName() == null || newPollDto.getName().isEmpty()) {
            poll.setError(PollError.WRONG_NAME_ERROR);
        }
        return poll;
    }
}