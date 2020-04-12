package github.com.matcwa.service;

import github.com.matcwa.api.dto.*;
import github.com.matcwa.api.error.AnswerError;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.AnswerMapper;
import github.com.matcwa.model.*;
import github.com.matcwa.repository.AnswerRepository;
import github.com.matcwa.repository.QuestionRepository;
import github.com.matcwa.repository.UserRepository;
import github.com.matcwa.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnAnswerWrongNameErrorAndNullDTO() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyPoll",user);
        Question question=new Question("any",poll);
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        NewAnswerDto emptyDescription = new NewAnswerDto("");
        NewAnswerDto nullDescription = new NewAnswerDto(null);
        //when
        ErrorHandling<QuestionDto, AnswerError> emptyContentResponse = answerService.createNewAnswer(emptyDescription, 1L,"token");
        ErrorHandling<QuestionDto, AnswerError> nullContentResponse = answerService.createNewAnswer(nullDescription, 1L,"token");
        //then
        assertNull(emptyContentResponse.getDto());
        assertEquals(emptyContentResponse.getError(), AnswerError.WRONG_NAME_ERROR);

        assertNull(nullContentResponse.getDto());
        assertEquals(nullContentResponse.getError(), AnswerError.WRONG_NAME_ERROR);
    }

    @Test
    void shouldReturnQuestionNotFoundErrorAndNullAnswerDTO() {
        //given
        NewAnswerDto newAnswer = new NewAnswerDto("newAnswer");
        given(questionRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<QuestionDto, AnswerError> newAnswerResponse = answerService.createNewAnswer(newAnswer, 1L,"token");
        //then
        assertNull(newAnswerResponse.getDto());
        assertEquals(newAnswerResponse.getError(), AnswerError.QUESTION_NOT_FOUND_ERROR);
    }

    @Test
    void shouldCreateNewAnswerAndReturnNullError() {
        //given
        NewAnswerDto newAnswer = new NewAnswerDto("newAnswer");
        User user=new User("user","password");
        Poll poll=new Poll("anyName",user);
        Question question = new Question("any",poll);
        Answer answer=new Answer("newAnswer",question);
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(questionRepository.findById(1L)).willReturn(Optional.of(question));
        ArgumentCaptor<Answer> answerCaptor = ArgumentCaptor.forClass(Answer.class);
        //when
        ErrorHandling<QuestionDto, AnswerError> newAnswerResponse = answerService.createNewAnswer(newAnswer, 1L,"token");
        //then
        verify(answerRepository, times(1)).save(answerCaptor.capture());
        assertNull(newAnswerResponse.getError());
        assertEquals(newAnswerResponse.getDto().getAnswers().size(), 1);
        assertEquals(answer, answerCaptor.getValue());
        assertEquals(question, answerCaptor.getValue().getQuestion());
    }

    @Test
    void shouldReturnAnswerNotFoundError() {
        //given
        given(answerRepository.findById(1L)).willReturn(Optional.empty());
        NewAnswerDto newAnswerDto = new NewAnswerDto("anyName");
        //when
        ErrorHandling<AnswerDto, AnswerError> answerUpdateResponse = answerService.updateAnswer(newAnswerDto, 1L,"token");
        //then
        assertNull(answerUpdateResponse.getDto());
        assertEquals(answerUpdateResponse.getError(), AnswerError.ANSWER_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnUserNotFoundError() {
        //given
        NewAnswerDto newAnswerDto=new NewAnswerDto("anyName");
        given(answerRepository.findById(1L)).willReturn(Optional.of(new Answer()));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto,1L,"token");
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),AnswerError.USER_NOT_FOUND_ERROR);
    }
    @Test
    void shouldReturnAuthorizationError() {
        //given
        User user=new User();
        User user2=new User();
        Poll poll=new Poll("anyName",user2);
        Question question=new Question("any",poll);
        Answer answer=new Answer("answer",question);
        NewAnswerDto newAnswerDto=new NewAnswerDto("anyName");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(tokenService.getRoleFromToken("token")).willReturn(Role.USER.name());
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto,1L,"token");
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),AnswerError.AUTHORIZATION_ERROR);
    }

    @Test
    void shouldReturnUpdatedAnswerAndNullAnswerErrorWhenUserIsAnswersOwner() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyName",user);
        Question question=new Question("any",poll);
        question.setId(1L);
        Answer answer=new Answer("any",question);
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        NewAnswerDto newAnswerDto=new NewAnswerDto("editedAnswerDescription");
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getAnswerDescription(),newAnswerDto.getDescription());
    }
    @Test
    void shouldReturnUpdatedAnswerAndNullAnswerErrorWhenUserHasRoleAdmin() {
        //given
        User user=new User("user","password");
        User owner=new User("any","any");
        Poll poll=new Poll("anyName",owner);
        Question question=new Question("any",poll);
        question.setId(1L);
        Answer answer=new Answer("any",question);
        given(tokenService.getRoleFromToken("token")).willReturn(Role.ADMIN.name());
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        NewAnswerDto newAnswerDto=new NewAnswerDto("editedAnswerDescription");
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.updateAnswer(newAnswerDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getAnswerDescription(),newAnswerDto.getDescription());
    }

    @Test
    void shouldReturnNotUpdatedQuestionAndNullQuestionError() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("any",user);
        Question question=new Question("currentQuestionDescription",poll);
        question.setId(1L);
        Answer answer = new Answer("currentAnswerDescription",question);
        answer.setId(1L);
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        NewAnswerDto emptyNameRequest = new NewAnswerDto("");
        NewAnswerDto nullNameRequest = new NewAnswerDto(null);
        //when
        ErrorHandling<AnswerDto, AnswerError> emptyNameResponse = answerService.updateAnswer(emptyNameRequest, 1L,"token");
        ErrorHandling<AnswerDto, AnswerError> nullNameResponse = answerService.updateAnswer(nullNameRequest, 1L,"token");
        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getAnswerDescription(), answer.getAnswerDescription());

        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getAnswerDescription(), answer.getAnswerDescription());
    }
    @Test
    void shouldReturnAnswerNotFoundErrorWhenTryVoteAdd() {
        //given
        HttpServletRequest httpServletRequest=mock(HttpServletRequest.class);
        given(answerRepository.findById(1L)).willReturn(Optional.empty());
        given(httpServletRequest.getRemoteAddr()).willReturn("anyAddress");
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.addVoteToAnswer(1L, httpServletRequest);
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),AnswerError.ANSWER_NOT_FOUND_ERROR);
    }

    @Test
    void shouldReturnOneVotePerIpError() {
        //given
        HttpServletRequest httpServletRequest=mock(HttpServletRequest.class);
        Answer answer=new Answer();
        answer.setIpSet(prepareAccountData());
        given(httpServletRequest.getRemoteAddr()).willReturn("123.456.789");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.addVoteToAnswer(1L, httpServletRequest);
        //then
        assertNull(response.getDto());
        assertEquals(response.getError(),AnswerError.ONE_VOTE_PER_IP_ERROR);
    }
    @Test
    void shouldCreateNewVoteAndAddVoteToAnswer() {
        //given
        HttpServletRequest httpServletRequest=mock(HttpServletRequest.class);
        Answer answer=new Answer();
        answer.setIpSet(prepareAccountData());
        given(httpServletRequest.getRemoteAddr()).willReturn("987.654.321");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        ArgumentCaptor<Vote> voteCaptor=ArgumentCaptor.forClass(Vote.class);
        //when
        ErrorHandling<AnswerDto, AnswerError> response = answerService.addVoteToAnswer(1L, httpServletRequest);
        //then
        verify(voteRepository,times(1)).save(voteCaptor.capture());
        assertNull(response.getError());
        assertEquals(response.getDto(), AnswerMapper.toDto(answer));
        assertEquals(voteCaptor.getValue().getAnswer(),answer);
    }
    @Test
    void shouldReturnAnswerNotFoundErrorAndNullDeleteAnswerDto() {
        //given
        given(answerRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<DeleteSuccessResponseDto, AnswerError> response = answerService.deleteAnswer(1L, "token");
        //then
        assertEquals(response.getError(),AnswerError.ANSWER_NOT_FOUND_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnUserNotFoundErrorAndNullDeleteAnswerDto() {
        //given
        Answer answer=new Answer();
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<DeleteSuccessResponseDto, AnswerError> response = answerService.deleteAnswer(1L, "token");
        //then
        assertEquals(response.getError(),AnswerError.USER_NOT_FOUND_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnAuthorizationErrorAndNullDeleteAnswerDto() {
        //given
        User user=new User("user","password");
        User user2=new User("user","password");
        Poll poll=new Poll("anyPoll",user2);
        Question question=new Question("any",poll);
        Answer answer=new Answer("answerDescription",question);
        given(tokenService.getRoleFromToken("token")).willReturn("USER");
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, AnswerError> response = answerService.deleteAnswer(1L, "token");
        //then
        assertEquals(response.getError(),AnswerError.AUTHORIZATION_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnDeleteSuccessResponseDtoAndNullAnswerErrorDtoWhenUserIsAnswersOwner() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyPoll",user);
        Question question=new Question("any",poll);
        Answer answer=new Answer("answerDescription",question);
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, AnswerError> response = answerService.deleteAnswer(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }
    @Test
    void shouldReturnDeleteSuccessResponseDtoAndNullAnswerErrorDtoWhenUserHasRoleAdmin() {
        //given
        User user=new User("user","password");
        User owner=new User("owner","pass");
        Poll poll=new Poll("anyPoll",owner);
        Question question=new Question("any",poll);
        Answer answer=new Answer("answerDescription",question);
        given(tokenService.getRoleFromToken("token")).willReturn(Role.ADMIN.name());
        given(answerRepository.findById(1L)).willReturn(Optional.of(answer));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, AnswerError> response = answerService.deleteAnswer(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }

    private Set<String> prepareAccountData() {
        Set<String> set=new HashSet<>();
        String ipAddress = "123.456.789";
        set.add(ipAddress);
        return set;
    }
}