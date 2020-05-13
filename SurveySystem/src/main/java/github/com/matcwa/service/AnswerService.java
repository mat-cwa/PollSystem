package github.com.matcwa.service;

import github.com.matcwa.api.dto.*;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.AnswerMapper;
import github.com.matcwa.api.mapper.QuestionMapper;
import github.com.matcwa.model.entity.Answer;
import github.com.matcwa.model.enums.Role;
import github.com.matcwa.model.entity.Question;
import github.com.matcwa.model.entity.Vote;
import github.com.matcwa.repository.AnswerRepository;
import github.com.matcwa.repository.QuestionRepository;
import github.com.matcwa.repository.UserRepository;
import github.com.matcwa.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AnswerService {
    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;
    private VoteRepository voteRepository;
    private UserRepository userRepository;
    private TokenService tokenService;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository, VoteRepository voteRepository, UserRepository userRepository, TokenService tokenService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public ErrorHandling<QuestionDto, AnswerError> createNewAnswer(NewAnswerDto newAnswerDto, Long id, String token) {
        ErrorHandling<QuestionDto, AnswerError> response = new ErrorHandling<>();
        questionRepository.findById(id).ifPresentOrElse(question -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                ErrorHandling<NewAnswerDto, AnswerError> newAnswer = validateNewAnswer(newAnswerDto);
                if (question.getPoll().getOwner() == user || tokenService.getRoleFromToken(token).equals("ADMIN")) {
                    if (newAnswer.getDto() != null) {
                        saveAnswer(question,newAnswerDto);
                        response.setDto(QuestionMapper.toDto(question));
                    } else {
                        response.setError(newAnswer.getError());
                    }
                } else {
                    response.setError(AnswerError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(AnswerError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(AnswerError.QUESTION_NOT_FOUND_ERROR));
        return response;
    }

    @Transactional
    public ErrorHandling<AnswerDto, AnswerError> updateAnswer(NewAnswerDto newAnswerDto, long id, String token) {
        ErrorHandling<AnswerDto, AnswerError> response = new ErrorHandling<>();
        answerRepository.findById(id).ifPresentOrElse(answer -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                if (answer.getQuestion().getPoll().getOwner() == user || tokenService.getRoleFromToken(token).equals(Role.ADMIN.name())) {
                    if (newAnswerDto.getDescription() != null && !newAnswerDto.getDescription().isEmpty()) {
                        answer.setAnswerDescription(newAnswerDto.getDescription());
                        response.setDto(AnswerMapper.toDto(answer));
                    } else {
                        response.setDto(AnswerMapper.toDto(answer));
                    }
                } else {
                    response.setError(AnswerError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(AnswerError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(AnswerError.ANSWER_NOT_FOUND_ERROR));
        return response;
    }

    public ErrorHandling<SuccessResponseDto, AnswerError> deleteAnswer(Long id, String token) {
        ErrorHandling<SuccessResponseDto, AnswerError> response = new ErrorHandling<>();
        answerRepository.findById(id).ifPresentOrElse(answer -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                if (answer.getQuestion().getPoll().getOwner() == user || tokenService.getRoleFromToken(token).equals("ADMIN")) {
                    answerRepository.deleteById(id);
                    response.setDto(new SuccessResponseDto("Successful!"));
                } else {
                    response.setError(AnswerError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(AnswerError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(AnswerError.ANSWER_NOT_FOUND_ERROR));
        return response;
    }

    @Transactional
    public ErrorHandling<AnswerDto, AnswerError> addVoteToAnswer(Long id, HttpServletRequest httpServletRequest) {
        String remoteAddr = httpServletRequest.getRemoteAddr();
        ErrorHandling<AnswerDto, AnswerError> response = new ErrorHandling<>();
        answerRepository.findById(id).ifPresentOrElse(answer -> {
            if (answer.getQuestion().getPoll().isManyVotePerQuestionAllowed()) {
                if (!isVoteForThisIpAddressAlreadyAdded(remoteAddr, answer)) {
                    saveVote(answer,remoteAddr);
                    response.setDto(AnswerMapper.toDto(answer));
                } else {
                    response.setError(AnswerError.ONE_VOTE_PER_IP_ANSWER);
                }
            } else if (!isVoteForThisIpAddressAlreadyAdded(remoteAddr, answer)) {
                saveVote(answer,remoteAddr);
                response.setDto(AnswerMapper.toDto(answer));
            } else response.setError(AnswerError.ONE_VOTE_PER_QUESTION);
        }, () -> response.setError(AnswerError.ANSWER_NOT_FOUND_ERROR));
        return response;
    }


    private boolean isVoteForThisIpAddressAlreadyAdded(String ipAddress, Answer answer) {
        if (answer.getQuestion().getPoll().isManyVotePerQuestionAllowed())
            return answer.getIpSet().contains(ipAddress);
        else
            return answer.getQuestion().getIpSet().contains(ipAddress);
    }

    private ErrorHandling<NewAnswerDto, AnswerError> validateNewAnswer(NewAnswerDto newAnswerDto) {
        ErrorHandling<NewAnswerDto, AnswerError> newAnswer = new ErrorHandling<>();
        if (newAnswerDto.getDescription() == null || newAnswerDto.getDescription().isEmpty()) {
            newAnswer.setError(AnswerError.WRONG_NAME_ERROR);
        } else {
            newAnswer.setDto(newAnswerDto);
        }
        return newAnswer;
    }

    private void saveVote(Answer answer, String remoteAddr) {
        Vote vote = new Vote();
        vote.setAnswer(answer);
        voteRepository.save(vote);
        answer.addVote(vote);
        if (answer.getQuestion().getPoll().isManyVotePerQuestionAllowed())
            answer.addIpAddress(remoteAddr);
        else
            answer.getQuestion().addIpAddress(remoteAddr);
    }

    private void saveAnswer(Question question,NewAnswerDto newAnswerDto){
        Answer answer = AnswerMapper.newToSource(newAnswerDto);
        answer.setQuestion(question);
        question.addAnswer(answer);
        answerRepository.save(answer);
    }
}