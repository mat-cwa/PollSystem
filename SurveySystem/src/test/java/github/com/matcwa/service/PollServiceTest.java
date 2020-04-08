package github.com.matcwa.service;

import github.com.matcwa.api.dto.NewPollDto;
import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.error.ErrorHandling;
import github.com.matcwa.api.error.PollError;
import github.com.matcwa.api.mapper.PollMapper;
import github.com.matcwa.model.Poll;
import github.com.matcwa.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
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
        assertEquals(response.getDto().getName(), newPollDto.getName());
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
        assertEquals(allPoll.get(0).getName(), prepareDto.get(0).getName());
        assertEquals(allPoll.get(1).getName(), prepareDto.get(1).getName());
        assertEquals(allPoll.get(2).getName(), prepareDto.get(2).getName());
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