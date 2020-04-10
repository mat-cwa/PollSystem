package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.model.Poll;
import github.com.matcwa.model.Question;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private PollRepository pollRepository;
    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() { MockitoAnnotations.initMocks(this); }

    @Test
    void shouldReturnEmptyContentErrorAndNullQuestionDTO() {
        //given
        NewQuestionDto emptyContent=new NewQuestionDto("");
        NewQuestionDto nullContent=new NewQuestionDto(null);
        //when
        ErrorHandling<PollDto, QuestionError> emptyContentResponse = questionService.createNewQuestion(emptyContent,1L);
        ErrorHandling<PollDto, QuestionError> nullContentResponse = questionService.createNewQuestion(emptyContent,1L);
        //then
        assertNull(emptyContentResponse.getDto());
        assertEquals(emptyContentResponse.getError(),QuestionError.EMPTY_CONTENT_ERROR);

        assertNull(nullContentResponse.getDto());
        assertEquals(nullContentResponse.getError(),QuestionError.EMPTY_CONTENT_ERROR);
    }

    @Test
    void shouldReturnPollNotFoundErrorAndNullQuestionDTO() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        given(pollRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L);
        //then
        assertNull(newQuestionResponse.getDto());
        assertEquals(newQuestionResponse.getError(),QuestionError.POLL_NOT_FOUND_ERROR);
    }
    @Test
    void shouldCreateNewQuestionAndSetBidirectionalRelationAndReturnNullError() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        Poll poll=new Poll();
        Question question=new Question(newQuestion.getQuestionDescription());
        question.setPoll(poll);
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        ArgumentCaptor<Question> questionCaptor=ArgumentCaptor.forClass(Question.class);
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L);
        //then
        verify(questionRepository,times(1)).save(questionCaptor.capture());
        assertNull(newQuestionResponse.getError());
        assertEquals(newQuestionResponse.getDto().getQuestions().size(),1);
        assertEquals(question,questionCaptor.getValue());
        assertEquals(poll,questionCaptor.getValue().getPoll());
    }

    @Test
    void shouldReturnQuestionNotFoundError() {
        //given
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        NewQuestionDto newQuestionDto=new NewQuestionDto("anyName");
        //when
        ErrorHandling<QuestionDto, QuestionError> questionUpdateResponse = questionService.updateQuestion(newQuestionDto,1L);
        //then
        assertNull(questionUpdateResponse.getDto());
        assertEquals(questionUpdateResponse.getError(),QuestionError.QUESTION_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnUpdatedQuestionAndNullQuestionError() {
        //given
        Question question=new Question();
        question.setId(1L);
        question.setQuestionDescription("currentQuestionDescription");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        NewQuestionDto newQuestionDto=new NewQuestionDto("editedQuestionDescription");
        //when
        ErrorHandling<QuestionDto, QuestionError> response = questionService.updateQuestion(newQuestionDto,1L);
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getQuestionDescription(),newQuestionDto.getQuestionDescription());
    }

    @Test
    void shouldReturnNotUpdatedQuestionAndNullQuestionError() {
        //given
        Question question=new Question();
        question.setId(1L);
        question.setQuestionDescription("currentQuestionDescription");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        NewQuestionDto emptyNameRequest=new NewQuestionDto("");
        NewQuestionDto nullNameRequest=new NewQuestionDto(null);
        //when
        ErrorHandling<QuestionDto, QuestionError> emptyNameResponse = questionService.updateQuestion(emptyNameRequest,1L);
        ErrorHandling<QuestionDto, QuestionError> nullNameResponse = questionService.updateQuestion(nullNameRequest,1L);
        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getQuestionDescription(),question.getQuestionDescription());

        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getQuestionDescription(),question.getQuestionDescription());
    }

}