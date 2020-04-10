package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.PollError;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.repository.PollRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PollServiceTest {
    @Mock
    PollRepository pollRepository;

    @InjectMocks
    PollService pollService = new PollService(pollRepository);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnPollDtoAndNullError() {
        //given
        NewPollDto newPollDto = new NewPollDto("Example poll");
        //when
        ErrorHandling<NewPollDto, PollError> response = pollService.addNewPoll(newPollDto);
        //then
        assertNull(response.getError());
        assertEquals(response.getDto(), newPollDto);
    }

    @Test
    void shouldReturnNullPollDtoAndWrongNameError() {
        //given
        NewPollDto emptyNameDTO = new NewPollDto("");
        NewPollDto nullNameDTO = new NewPollDto(null);
        //when
        ErrorHandling<NewPollDto, PollError> emptyName = pollService.addNewPoll(emptyNameDTO);
        ErrorHandling<NewPollDto, PollError> nullName = pollService.addNewPoll(nullNameDTO);
        //then
        assertNull(emptyName.getDto());
        assertNull(nullName.getDto());
        assertEquals(emptyName.getError(), PollError.WRONG_NAME_ERROR);
        assertEquals(nullName.getError(), PollError.WRONG_NAME_ERROR);
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
        ErrorHandling<PollDto, PollError> pollUpdateRespone = pollService.updatePoll(newPollDto,1L);
        //then
        assertNull(getPollByIdResponse.getDto());
        assertEquals(getPollByIdResponse.getError(),PollError.POLL_NOT_FOUND_ERROR);

        assertNull(pollUpdateRespone.getDto());
        assertEquals(pollUpdateRespone.getError(),PollError.POLL_NOT_FOUND_ERROR);
    }

    @Test
    void shouldReturnUpdatedPollAndNullPollError() {
        //given
        Poll poll=new Poll();
        poll.setId(1L);
        poll.setName("currentName");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        NewPollDto newPollDto=new NewPollDto("editedName");
        //when
        ErrorHandling<PollDto, PollError> response = pollService.updatePoll(newPollDto,1L);
        //then
        assertNull(response.getError());
        assertEquals(response.getDto().getName(),newPollDto.getName());
    }

    @Test
    void shouldReturnNotUpdatedPollAndNullPollError() {
        //given
        Poll poll=new Poll();
        poll.setId(1L);
        poll.setName("currentName");
        given(pollRepository.findById(1L)).willReturn(Optional.of(poll));
        NewPollDto emptyNameRequest=new NewPollDto("");
        NewPollDto nullNameRequest=new NewPollDto(null);
        //when
        ErrorHandling<PollDto, PollError> emptyNameResponse = pollService.updatePoll(emptyNameRequest,1L);
        ErrorHandling<PollDto, PollError> nullNameResponse = pollService.updatePoll(nullNameRequest,1L);

        //then
        assertNull(emptyNameResponse.getError());
        assertEquals(emptyNameResponse.getDto().getName(),poll.getName());

        assertNull(nullNameResponse.getError());
        assertEquals(nullNameResponse.getDto().getName(),poll.getName());
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