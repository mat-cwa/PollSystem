package github.com.matcwa.service;

import github.com.matcwa.api.dto.SuccessResponseDto;
import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.api.mapper.QuestionMapper;
import github.com.matcwa.model.entity.Question;
import github.com.matcwa.model.enums.Role;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.QuestionRepository;

import github.com.matcwa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService {
    private QuestionRepository questionRepository;
    private PollRepository pollRepository;
    private UserRepository userRepository;
    private TokenService tokenService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, PollRepository pollRepository, UserRepository userRepository, TokenService tokenService) {
        this.questionRepository = questionRepository;
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public ErrorHandling<PollDto, QuestionError> createNewQuestion(NewQuestionDto newQuestionDto, Long pollId, String token) {
        ErrorHandling<PollDto, QuestionError> response = new ErrorHandling<>();
        pollRepository.findById(pollId).ifPresentOrElse(poll -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                ErrorHandling<NewQuestionDto, QuestionError> newQuestion = validateNewQuestion(newQuestionDto);
                if (poll.getOwner() == user || tokenService.getRoleFromToken(token).equals("ADMIN")) {
                    if (newQuestion.getDto() != null) {
                        Question question = QuestionMapper.newToSource(newQuestionDto);
                        question.setPoll(poll);
                        poll.addQuestion(question);
                        questionRepository.save(question);
                        response.setDto(PollMapper.toDto(poll));
                    } else {
                        response.setError(newQuestion.getError());
                    }
                } else {
                    response.setError(QuestionError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(QuestionError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(QuestionError.POLL_NOT_FOUND_ERROR));
        return response;
    }

    @Transactional
    public ErrorHandling<QuestionDto, QuestionError> updateQuestion(NewQuestionDto newQuestionDto, Long id, String token) {
        ErrorHandling<QuestionDto, QuestionError> response = new ErrorHandling<>();
        questionRepository.findById(id).ifPresentOrElse(question -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                if (question.getPoll().getOwner() == user || tokenService.getRoleFromToken(token).equals(Role.ADMIN.name())) {
                    if (newQuestionDto.getDescription() != null && !newQuestionDto.getDescription().isEmpty()) {
                        question.setQuestionDescription(newQuestionDto.getDescription());
                        response.setDto(QuestionMapper.toDto(question));
                    } else {
                        response.setDto(QuestionMapper.toDto(question));
                    }
                } else {
                    response.setError(QuestionError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(QuestionError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(QuestionError.QUESTION_NOT_FOUND_ERROR));
        return response;
    }

    public ErrorHandling<SuccessResponseDto, QuestionError> deleteQuestion(Long id, String token) {
        ErrorHandling<SuccessResponseDto, QuestionError> response = new ErrorHandling<>();
        questionRepository.findById(id).ifPresentOrElse(question -> {
            userRepository.findByUsername(tokenService.getUsernameFromToken(token)).ifPresentOrElse(user -> {
                if (question.getPoll().getOwner() == user || tokenService.getRoleFromToken(token).equals("ADMIN")) {
                    questionRepository.deleteById(id);
                    response.setDto(new SuccessResponseDto("Successful!"));
                } else {
                    response.setError(QuestionError.AUTHORIZATION_ERROR);
                }
            }, () -> response.setError(QuestionError.USER_NOT_FOUND_ERROR));
        }, () -> response.setError(QuestionError.QUESTION_NOT_FOUND_ERROR));
        return response;
    }


    private ErrorHandling<NewQuestionDto, QuestionError> validateNewQuestion(NewQuestionDto newQuestionDto) {
        ErrorHandling<NewQuestionDto, QuestionError> question = new ErrorHandling<>();

        if (newQuestionDto.getDescription() == null || newQuestionDto.getDescription().isEmpty()) {
            question.setError(QuestionError.EMPTY_CONTENT_ERROR);
        } else {
            question.setDto(newQuestionDto);
        }
        return question;
    }

}
