package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.api.mapper.QuestionMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.model.Question;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class QuestionService {
    private QuestionRepository questionRepository;
    private PollRepository pollRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, PollRepository pollRepository) {
        this.questionRepository = questionRepository;
        this.pollRepository = pollRepository;
    }

    public ErrorHandling<PollDto, QuestionError> createNewQuestion(NewQuestionDto newQuestionDto, Long pollId) {
        ErrorHandling<NewQuestionDto, QuestionError> newQuestion = validateNewQuestion(newQuestionDto);
        ErrorHandling<PollDto, QuestionError> response = new ErrorHandling<>();
        if (newQuestion.getDto() != null) {
            pollRepository.findById(pollId).ifPresentOrElse(poll -> {
                        Question question = QuestionMapper.newToSource(newQuestionDto);
                        question.setPoll(poll);
                        poll.addQuestion(question);
                        questionRepository.save(question);
                        response.setDto(PollMapper.toDto(poll));
                    },
                    () -> response.setError(QuestionError.POLL_NOT_FOUND_ERROR));
        } else {
            response.setError(newQuestion.getError());
        }
        return response;
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }


    private ErrorHandling<NewQuestionDto, QuestionError> validateNewQuestion(NewQuestionDto newQuestionDto) {
        ErrorHandling<NewQuestionDto, QuestionError> question = new ErrorHandling<>();

        if (newQuestionDto.getQuestionDescription() == null || newQuestionDto.getQuestionDescription().isEmpty()) {
            question.setError(QuestionError.EMPTY_CONTENT_ERROR);
        } else {
            question.setDto(newQuestionDto);
        }
        return question;
    }

    public ErrorHandling<QuestionDto, QuestionError> updateQuestion(NewQuestionDto newQuestionDto, Long id) {
        ErrorHandling<QuestionDto, QuestionError> response = new ErrorHandling<>();
        questionRepository.findById(id).ifPresentOrElse(question -> {
            if (newQuestionDto.getQuestionDescription() != null && !newQuestionDto.getQuestionDescription().isEmpty()) {
                question.setQuestionDescription(newQuestionDto.getQuestionDescription());
                response.setDto(QuestionMapper.toDto(question));
            } else {
                response.setDto(QuestionMapper.toDto(question));
            }
        }, () -> response.setError(QuestionError.QUESTION_NOT_FOUND_ERROR));
        return response;
    }

}
