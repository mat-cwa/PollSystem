package github.com.matcwa.service;

import github.com.matcwa.api.dto.DeleteSuccessResponseDto;
import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.PollError;
import github.com.matcwa.api.jwt.TokenService;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.model.Role;
import github.com.matcwa.model.User;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PollServiceTest {
    @Mock
    private PollRepository pollRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    PollService pollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateNewPollReturnPollDtoAndNullError() {
        //given
        NewPollDto userPollDto = new NewPollDto("User poll");
        NewPollDto adminPollDto = new NewPollDto("Admin poll");

        User user=new User("user","password");
        user.setRole(Role.USER);
        User admin=new User("admin","password");
        admin.setRole(Role.ADMIN);
        Poll userPoll=new Poll(userPollDto.getName(),user);
        Poll adminPoll=new Poll(adminPollDto.getName(),admin);
        given(tokenService.getRoleFromToken("userToken")).willReturn("USER");
        given(tokenService.getRoleFromToken("adminToken")).willReturn("ADMIN");
        given(tokenService.getUsernameFromToken("userToken")).willReturn("user");
        given(tokenService.getUsernameFromToken("adminToken")).willReturn("admin");
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(userRepository.findByUsername("admin")).willReturn(Optional.of(admin));
        ArgumentCaptor<Poll> pollCaptor=ArgumentCaptor.forClass(Poll.class);
        //when
        ErrorHandling<PollDto, PollError> userTokenResponse = pollService.addNewPoll(userPollDto,"userToken");
        ErrorHandling<PollDto, PollError> adminTokenResponse = pollService.addNewPoll(adminPollDto,"adminToken");
        //then
        verify(pollRepository,times(2)).save(pollCaptor.capture());
        assertNull(userTokenResponse.getError());
        assertNull(adminTokenResponse.getError());
        assertTrue(pollCaptor.getAllValues().contains(userPoll));
        assertTrue(pollCaptor.getAllValues().contains(adminPoll));
    }

    @Test
    void shouldReturnNullPollDtoAndWrongNameError() {
        //given
        User user=new User("user","password");
        NewPollDto emptyNameDTO = new NewPollDto("");
        NewPollDto nullNameDTO = new NewPollDto(null);
        given(tokenService.getRoleFromToken("token")).willReturn("USER");
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<PollDto, PollError> emptyName = pollService.addNewPoll(emptyNameDTO,"token");
        ErrorHandling<PollDto, PollError> nullName = pollService.addNewPoll(nullNameDTO,"token");
        //then
        assertNull(emptyName.getDto());
        assertNull(nullName.getDto());
        assertEquals(emptyName.getError(), PollError.WRONG_NAME_ERROR);
        assertEquals(nullName.getError(), PollError.WRONG_NAME_ERROR);
    }
    @Test
    void shouldReturnNullPollDtoAndAuthorizationError() {
        //given
        NewPollDto newPollDto = new NewPollDto("");
        given(tokenService.getRoleFromToken("token")).willReturn("Wrong Role");
        //when
        ErrorHandling<PollDto, PollError> emptyName = pollService.addNewPoll(newPollDto,"token");
        //then
        assertNull(emptyName.getDto());
        assertEquals(emptyName.getError(), PollError.AUTHORIZATION_ERROR);
    }

    @Test
    void shouldReturnListOfAllPolls() {
        //given
        given(pollRepository.findAll()).willReturn(prepareAccountData());
        //when
        List<PollDto> allPoll = pollService.getAll();
        List<PollDto> prepareDto = prepareAccountData().stream().map(PollMapper::toDto).collect(Collectors.toList());
        //then
        verify(pollRepository, times(1)).findAll();
        assertEquals(allPoll.size(), prepareAccountData().size());
        assertEquals(allPoll, prepareDto);
    }

    @Test
    void shouldFoundPollById() {
        //given
        Poll poll=new Poll();
        poll.setId(1L);
        poll.setName("anyPoll");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        //when
        ErrorHandling<PollDto, PollError> response = pollService.getPollById(1L);
        //then
        assertNull(response.getError());
        assertEquals(response.getDto(),PollMapper.toDto(poll));
    }

    @Test
    void shouldReturnPollNotFoundError() {
        //given
        given(pollRepository.findById(1L)).willReturn(Optional.empty());
        NewPollDto newPollDto=new NewPollDto("anyName");
        //when
        ErrorHandling<PollDto, PollError> getPollByIdResponse = pollService.getPollById(1L);
        ErrorHandling<PollDto, PollError> pollUpdateRespone = pollService.updatePoll(newPollDto,1L,"token");
        //then
        assertNull(getPollByIdResponse.getDto());
        assertEquals(getPollByIdResponse.getError(),PollError.POLL_NOT_FOUND_ERROR);

        assertNull(pollUpdateRespone.getDto());
        assertEquals(pollUpdateRespone.getError(),PollError.POLL_NOT_FOUND_ERROR);
    }

    @Test
    void shouldReturnUpdatedPollAndNullPollErrorWhenUserIsPollsOwner() {
        //given
        User user=new User("exampleUsername","password");
        Poll poll=new Poll("currentName",user);
        poll.setId(1L);
        NewPollDto newPollDto=new NewPollDto("editedName");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<PollDto, PollError> response = pollService.updatePoll(newPollDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getName(),newPollDto.getName());
    }
    @Test
    void shouldReturnUpdatedPollAndNullPollErrorWhenUserRoleIsAdmin() {
        //given
        User user=new User("exampleUsername","password");
        Poll poll=new Poll("currentName",new User("owner","password"));
        poll.setId(1L);
        NewPollDto newPollDto=new NewPollDto("editedName");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        given(tokenService.getRoleFromToken("token")).willReturn("ADMIN");
        //when
        ErrorHandling<PollDto, PollError> response = pollService.updatePoll(newPollDto,1L,"token");
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getName(),newPollDto.getName());
    }

    @Test
    void shouldReturnNotUpdatedPollAndNullPollError() {
        //given
        User user=new User("exampleUsername","password");
        Poll poll=new Poll("currentName",user);
        poll.setId(1L);
        NewPollDto emptyNameRequest=new NewPollDto("");
        NewPollDto nullNameRequest=new NewPollDto(null);
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<PollDto, PollError> emptyNameResponse = pollService.updatePoll(emptyNameRequest,1L,"token");
        ErrorHandling<PollDto, PollError> nullNameResponse = pollService.updatePoll(nullNameRequest,1L,"token");
        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getName(),poll.getName());
        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getName(),poll.getName());
    }
    @Test
    void shouldReturnPollNotFoundErrorAndNullDeletePollDto() {
        //given
        given(pollRepository.findById(1L)).willReturn(Optional.empty());
        //when
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = pollService.deletePoll(1L, "token");
        //then
        assertEquals(response.getError(),PollError.POLL_NOT_FOUND_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnUserNotFoundErrorAndNullDeletePollDto() {
        //given
        Poll poll=new Poll();
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.empty());
        //when
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = pollService.deletePoll(1L, "token");
        //then
        assertEquals(response.getError(),PollError.USER_NOT_FOUND);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnAuthorizationErrorAndNullDeletePollDto() {
        //given
        User user=new User("user","password");
        User user2=new User("user","password");
        Poll poll=new Poll("anyPoll",user2);
        given(tokenService.getRoleFromToken("token")).willReturn("USER");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = pollService.deletePoll(1L, "token");
        //then
        assertEquals(response.getError(),PollError.AUTHORIZATION_ERROR);
        assertNull(response.getDto());
    }
    @Test
    void shouldReturnDeletePollSuccessResponseDtoAndNullPollErrorDtoWhenUserIsPollsOwner() {
        //given
        User user=new User("user","password");
        Poll poll=new Poll("anyPoll",user);
        given(tokenService.getRoleFromToken("token")).willReturn("USER");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = pollService.deletePoll(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }
    @Test
    void shouldReturnDeleteSuccessResponseDtoAndNullPollErrorDtoWhenUserRoleIsAdmin() {
        //given
        User user=new User("user","password");
        User user2=new User("user2","password");
        Poll poll=new Poll("anyPoll",user2);
        given(tokenService.getRoleFromToken("token")).willReturn("ADMIN");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        given(userRepository.findByUsername(tokenService.getUsernameFromToken("token"))).willReturn(Optional.of(user));
        //when
        ErrorHandling<DeleteSuccessResponseDto, PollError> response = pollService.deletePoll(1L, "token");
        //then
        assertEquals(response.getDto().getResponse(),"Successful!");
        assertNull(response.getError());
    }

    private List<Poll> prepareAccountData() {
        Poll poll1 = new Poll();
        poll1.setName("poll1");
        Poll poll2 = new Poll();
        poll2.setName("poll2");
        Poll poll3 = new Poll();
        poll3.setName("poll3");
        return Arrays.asList(poll1, poll2, poll3);
    }
}