package github.com.matcwa.service;

import github.com.matcwa.api.dto.SuccessResponseDto;
import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.QuestionError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.model.entity.Poll;
import github.com.matcwa.model.entity.Question;
import github.com.matcwa.model.Role;
import github.com.matcwa.model.entity.User;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.QuestionRepository;
import github.com.matcwa.repository.UserRepository;
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
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() { MockitoAnnotations.initMocks(this); }

    @Test
    void shouldCreateNewQuestionAndReturnNullErrorWhenUserIsPollsOwner() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        User user=new User("user","password");
        Poll poll=new Poll("anyName",user);
        Question question=new Question(newQuestion.getDescription(),poll);
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        ArgumentCaptor<Question> questionCaptor=ArgumentCaptor.forClass(Question.class);
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L,"token");
        //then
        verify(questionRepository,times(1)).save(questionCaptor.capture());
        assertNull(newQuestionResponse.getError());
        assertEquals(newQuestionResponse.getDto().getQuestions().size(),1);
        assertEquals(question,questionCaptor.getValue());
        assertEquals(poll,questionCaptor.getValue().getPoll());
    }
    @Test
    void shouldCreateNewQuestionAndReturnNullErrorWhenUserHasRoleAdmin() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        User user=new User("user","password");
        User owner=new User();
        Poll poll=new Poll("anyName",owner);
        Question question=new Question(newQuestion.getDescription(),poll);
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(tokenService.getRoleFromToken("token")).willReturn(Role.ADMIN.name());
        ArgumentCaptor<Question> questionCaptor=ArgumentCaptor.forClass(Question.class);
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L,"token");
        //then
        verify(questionRepository,times(1)).save(questionCaptor.capture());
        assertNull(newQuestionResponse.getError());
        assertEquals(newQuestionResponse.getDto().getQuestions().size(),1);
        assertEquals(question,questionCaptor.getValue());
        assertEquals(poll,questionCaptor.getValue().getPoll());
    }

    @Test
    void shouldReturnPollNotFoundErrorAndNullQuestionDTO() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        given(pollRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L,"token");
        //then
        assertNull(newQuestionResponse.getDto());
        assertEquals(newQuestionResponse.getError(),QuestionError.POLL_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnUserNotFoundErrorAndNullQuestionDTO() {
        //given
        NewQuestionDto newQuestion=new NewQuestionDto("newQuestion");
        Poll poll=new Poll();
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<PollDto, QuestionError> newQuestionResponse = questionService.createNewQuestion(newQuestion,1L,"token");
        //then
        assertNull(newQuestionResponse.getDto());
        assertEquals(newQuestionResponse.getError(),QuestionError.USER_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnEmptyContentErrorAndNullQuestionDTO() {
        //given
        NewQuestionDto emptyContent=new NewQuestionDto("");
        NewQuestionDto nullContent=new NewQuestionDto(null);
        User user=new User("user","password");
        Poll poll=new Poll("anyPoll",user);
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<PollDto, QuestionError> emptyContentResponse = questionService.createNewQuestion(emptyContent,1L,"token");
        ErrorHandling<PollDto, QuestionError> nullContentResponse = questionService.createNewQuestion(nullContent,1L,"token");
        //then
        assertNull(emptyContentResponse.getDto());
        assertEquals(emptyContentResponse.getError(),QuestionError.EMPTY_CONTENT_ERROR);

        assertNull(nullContentResponse.getDto());
        assertEquals(nullContentResponse.getError(),QuestionError.EMPTY_CONTENT_ERROR);
    }

    @Test
    void shouldReturnUpdatedQuestionAndNullQuestionErrorWhenUserIsPollsOwner() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyName",user);
        Question question=new Question("any",poll);
        question.setId(1L);
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        NewQuestionDto newQuestionDto=new NewQuestionDto("editedQuestionDescription");
        //when
        ErrorHandling<QuestionDto, QuestionError> response = questionService.updateQuestion(newQuestionDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getQuestionDescription(),newQuestionDto.getDescription());
    }
    @Test
    void shouldReturnUpdatedQuestionAndNullQuestionErrorWhenUserHasRoleAdmin() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyName",new User("newOwner","password"));
        Question question=new Question("any",poll);
        question.setId(1L);
        question.setQuestionDescription("currentQuestionDescription");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(tokenService.getRoleFromToken("token")).willReturn(Role.ADMIN.name());
        NewQuestionDto newQuestionDto=new NewQuestionDto("editedQuestionDescription");
        //when
        ErrorHandling<QuestionDto, QuestionError> response = questionService.updateQuestion(newQuestionDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getQuestionDescription(),newQuestionDto.getDescription());
    }

    @Test
    void shouldReturnNotUpdatedQuestionAndNullQuestionError() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("any",user);
        Question question=new Question("currentQuestionDescription",poll);
        question.setId(1L);
        NewQuestionDto emptyNameRequest=new NewQuestionDto("");
        NewQuestionDto nullNameRequest=new NewQuestionDto(null);
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<QuestionDto, QuestionError> emptyNameResponse = questionService.updateQuestion(emptyNameRequest,1L,"token");
        ErrorHandling<QuestionDto, QuestionError> nullNameResponse = questionService.updateQuestion(nullNameRequest,1L,"token");
        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getQuestionDescription(),question.getQuestionDescription());

        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getQuestionDescription(),question.getQuestionDescription());
    }

    @Test
    void shouldReturnQuestionNotFoundError() {
        //given
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        NewQuestionDto newQuestionDto=new NewQuestionDto("anyName");
        //when
        ErrorHandling<QuestionDto, QuestionError> questionUpdateResponse = questionService.updateQuestion(newQuestionDto,1L,"token");
        //then
        assertNull(questionUpdateResponse.getDto());
        assertEquals(questionUpdateResponse.getError(),QuestionError.QUESTION_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnUserNotFoundError() {
        //given
        NewQuestionDto newQuestionDto=new NewQuestionDto("anyName");
        given(questionRepository.findById(1L)).willReturn(Optional.of(new Question()));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<QuestionDto, QuestionError> questionUpdateResponse = questionService.updateQuestion(newQuestionDto,1L,"token");
        //then
        assertNull(questionUpdateResponse.getDto());
        assertEquals(questionUpdateResponse.getError(),QuestionError.USER_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnAuthorizationError() {
        //given
        User user=new User();
        User user2=new User();
        Poll poll=new Poll("anyName",user2);
        Question question=new Question("any",poll);
        NewQuestionDto newQuestionDto=new NewQuestionDto("anyName");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(tokenService.getRoleFromToken("token")).willReturn(Role.USER.name());
        //when
        ErrorHandling<QuestionDto, QuestionError> questionUpdateResponse = questionService.updateQuestion(newQuestionDto,1L,"token");
        //then
        assertNull(questionUpdateResponse.getDto());
        assertEquals(questionUpdateResponse.getError(),QuestionError.AUTHORIZATION_ERROR);
    }

    @Test
    void shouldReturnQuestionNotFoundErrorAndNullDeleteQuestionDto() {
        //given
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(1L, "token");
        //then
        assertEquals(response.getError(),QuestionError.QUESTION_NOT_FOUND_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnUserNotFoundErrorAndNullDeleteQuestionDto() {
        //given
        Question question=new Question();
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(1L, "token");
        //then
        assertEquals(response.getError(),QuestionError.USER_NOT_FOUND_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnAuthorizationErrorAndNullDeleteQuestionDto() {
        //given
        User user=new User("user","password");
        User user2=new User("user","password");
        Poll poll=new Poll("anyPoll",user2);
        Question question=new Question("any",poll);
        given(tokenService.getRoleFromToken("token")).willReturn("USER");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(1L, "token");
        //then
        assertEquals(response.getError(),QuestionError.AUTHORIZATION_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnDeleteSuccessResponseDtoAndNullQuestionErrorDtoWhenUserIsQuestionsOwner() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyPoll",user);
        Question question=new Question("any",poll);
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }
    @Test
    void shouldReturnDeleteSuccessResponseDtoAndNullQuestionErrorDtoWhenUserRoleIsAdmin() {
        //given
        User user=new User("user","password");
        User user2=new User("user2","password");
        Poll poll=new Poll("anyPoll",user2);
        Question question=new Question("any",poll);
        given(tokenService.getRoleFromToken("token")).willReturn("ADMIN");
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<SuccessResponseDto, QuestionError> response = questionService.deleteQuestion(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }

}