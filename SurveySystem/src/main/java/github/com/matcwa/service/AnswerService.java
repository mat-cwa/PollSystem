package github.com.matcwa.service;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.NewAnswerDto;
import github.com.matcwa.api.dto.NewVoteDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.mapper.AnswerMapper;
import github.com.matcwa.api.mapper.QuestionMapper;
import github.com.matcwa.api.mapper.VoteMapper;
import github.com.matcwa.model.Answer;
import github.com.matcwa.model.Question;
import github.com.matcwa.model.Vote;
import github.com.matcwa.repository.AnswerRepository;
import github.com.matcwa.repository.QuestionRepository;
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

    @Autowired
    public AnswerService(AnswerRepository answerRepository, QuestionRepository questionRepository, VoteRepository voteRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
    }

    public ErrorHandling<QuestionDto, AnswerError> createNewAnswer(NewAnswerDto newAnswerDto, Long id) {
        ErrorHandling<NewAnswerDto, AnswerError> newAnswer = validateNewAnswer(newAnswerDto);
        ErrorHandling<QuestionDto, AnswerError> response = new ErrorHandling<>();

        if (newAnswer.getDto() != null) {
            questionRepository.findById(id).ifPresentOrElse(question -> {
                Answer answer = AnswerMapper.newToSource(newAnswerDto);
                answer.setQuestion(question);
                question.addAnswer(answer);
                answerRepository.save(answer);
                response.setDto(QuestionMapper.toDto(question));

            }, () -> response.setError(AnswerError.QUESTION_NOT_FOUND_ERROR));
        } else {
            response.setError(newAnswer.getError());
        }
        return response;
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

    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    public ErrorHandling<AnswerDto, AnswerError> addVoteToAnswer(Long id, NewVoteDto newVoteDto, HttpServletRequest httpServletRequest) {
        String remoteAddr = httpServletRequest.getRemoteAddr();
        ErrorHandling<AnswerDto, AnswerError> addVote = validateVote(remoteAddr, id);
        answerRepository.findById(id).ifPresent(answer -> {
            if (addVote.getDto() != null) {
                Vote vote = VoteMapper.newToSource(newVoteDto);
                answer.addVote(vote, remoteAddr);
                vote.setAnswer(answer);
                voteRepository.save(vote);
            }
        });
        return addVote;
    }


    public ErrorHandling<AnswerDto, AnswerError> updateAnswer(NewAnswerDto newAnswerDto, long id) {
        ErrorHandling<AnswerDto, AnswerError> response = new ErrorHandling<>();
        answerRepository.findById(id).ifPresentOrElse(answer -> {
            if (newAnswerDto.getDescription() != null && !newAnswerDto.getDescription().isEmpty()) {
                answer.setAnswerDescription(newAnswerDto.getDescription());
                response.setDto(AnswerMapper.toDto(answer));
            } else {
                response.setDto(AnswerMapper.toDto(answer));
            }
        }, () -> response.setError(AnswerError.ANSWER_NOT_FOUND_ERROR));
        return response;
    }

    private ErrorHandling<AnswerDto, AnswerError> validateVote(String ipAddress, Long id) {
        ErrorHandling<AnswerDto, AnswerError> addVote = new ErrorHandling<>();
        answerRepository.findById(id).ifPresentOrElse(answer -> {
            if (answer.getIpSet().contains(ipAddress)) {
                addVote.setError(AnswerError.ONE_VOTE_PER_IP_ERROR);
            } else {
                addVote.setDto(AnswerMapper.toDto(answer));
            }
        }, () -> addVote.setError(AnswerError.ANSWER_NOT_FOUND_ERROR));
        return addVote;
    }
}