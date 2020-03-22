package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.api.mapper.QuestionMapper;
import github.com.matcwa.model.Question;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService {
    private QuestionRepository questionRepository;
    private PollRepository pollRepository;
    @Autowired
    public QuestionService(QuestionRepository questionRepository,PollRepository pollRepository) {
        this.questionRepository = questionRepository;
        this.pollRepository=pollRepository;
    }

    public ErrorHandling<NewQuestionDto,QuestionError> createNewQuestion(NewQuestionDto newQuestionDto, Long pollId){
        ErrorHandling<NewQuestionDto, QuestionError> newQuestion = validateNewQuestion(newQuestionDto);

        if (newQuestion.getDto()!=null){
                pollRepository.findById(pollId).ifPresentOrElse(poll -> {
                Question question = QuestionMapper.newToSource(newQuestionDto);
                question.setPoll(poll);
                poll.addQuestion(question);
                questionRepository.save(question);
            },
                ()-> newQuestion.setError(QuestionError.POLL_NOT_FOUND_ERROR));
        }
        return newQuestion;
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

}
