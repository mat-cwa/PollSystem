package github.com.matcwa.service;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.NewAnswerDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.model.Answer;
import github.com.matcwa.model.Question;
import github.com.matcwa.repository.AnswerRepository;
import github.com.matcwa.repository.QuestionRepository;
import github.com.matcwa.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private VoteRepository voteRepository;
    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnAnswerWrongNameErrorAndNullDTO() {
        //given
        NewAnswerDto emptyDescription=new NewAnswerDto("");
        NewAnswerDto nullDescription=new NewAnswerDto(null);
        //when
        ErrorHandling<QuestionDto, AnswerError> emptyContentResponse = answerService.createNewAnswer(emptyDescription, 1L);
        ErrorHandling<QuestionDto, AnswerError> nullContentResponse = answerService.createNewAnswer(nullDescription, 1L);
        //then
        assertNull(emptyContentResponse.getDto());
        assertEquals(emptyContentResponse.getError(),AnswerError.WRONG_NAME_ERROR);

        assertNull(nullContentResponse.getDto());
        assertEquals(nullContentResponse.getError(),AnswerError.WRONG_NAME_ERROR);
    }
    @Test
    void shouldReturnQuestionNotFoundErrorAndNullAnswerDTO() {
        //given
        NewAnswerDto newAnswer=new NewAnswerDto("newAnswer");
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<QuestionDto, AnswerError> newAnswerResponse = answerService.createNewAnswer(newAnswer,1L);
        //then
        assertNull(newAnswerResponse.getDto());
        assertEquals(newAnswerResponse.getError(),AnswerError.QUESTION_NOT_FOUND_ERROR);
    }
    @Test
    void shouldCreateNewAnswerAndSetBidirectionalRelationAndReturnNullError() {
        //given
        NewAnswerDto newAnswer=new NewAnswerDto("newAnswer");
        Question sourceQuestion=new Question();
        Answer answer=new Answer(newAnswer.getDescription(),null,null);
        answer.setQuestion(sourceQuestion);
        given(questionRepository.findById(1L)).willReturn(Optional.of(sourceQuestion));
        ArgumentCaptor<Answer> answerCaptor=ArgumentCaptor.forClass(Answer.class);
        //when
        ErrorHandling<QuestionDto, AnswerError> newAnswerResponse = answerService.createNewAnswer(newAnswer,1L);
        //then
        verify(answerRepository,times(1)).save(answerCaptor.capture());
        assertNull(newAnswerResponse.getError());
        assertEquals(newAnswerResponse.getDto().getAnswers().size(),1);
        assertEquals(answer,answerCaptor.getValue());
        assertEquals(sourceQuestion,answerCaptor.getValue().getQuestion());
    }
    @Test
    void shouldReturnAnswerNotFoundError() {
        //given
        given(answerRepository.findById(1L)).willReturn(Optional.empty());
        NewAnswerDto newAnswerDto=new NewAnswerDto("anyName");
        //when
        ErrorHandling<AnswerDto, AnswerError> answerUpdateResponse = answerService.updateAnswer(newAnswerDto,1L);
        //then
        assertNull(answerUpdateResponse.getDto());
        assertEquals(answerUpdateResponse.getError(),AnswerError.ANSWER_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnUpdateAnswerAndNullAnswerError() {
        //given
        Answer answer=new Answer();
        answer.setId(1L);
        answer.setAnswerDescription("currentAnswerDescription");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        NewAnswerDto newAnswerDto=new NewAnswerDto("editedAnswerDescription");
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto,1L);
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getAnswerDescription(),newAnswerDto.getDescription());
    }
    @Test
    void shouldReturnNotUpdatedQuestionAndNullQuestionError() {
        //given
        Answer answer=new Answer();
        answer.setId(1L);
        answer.setAnswerDescription("currentAnswerDescription");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        NewAnswerDto emptyNameRequest=new NewAnswerDto("");
        NewAnswerDto nullNameRequest=new NewAnswerDto(null);
        //when
        ErrorHandling<AnswerDto, AnswerError> emptyNameResponse = answerService.updateAnswer(emptyNameRequest,1L);
        ErrorHandling<AnswerDto, AnswerError> nullNameResponse = answerService.updateAnswer(nullNameRequest,1L);
        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getAnswerDescription(),answer.getAnswerDescription());

        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getAnswerDescription(),answer.getAnswerDescription());
    }

}