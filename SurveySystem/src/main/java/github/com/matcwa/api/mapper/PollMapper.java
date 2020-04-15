package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.*;
import github.com.matcwa.model.entity.Poll;
import org.modelmapper.ModelMapper;

public class PollMapper {
    private static ModelMapper modelMapper = new ModelMapper();

    public static PollDto toDto(Poll poll) {
       return modelMapper.map(poll,PollDto.class);
    }

    public static Poll newToSource(NewPollDto newPollDto) {
        return modelMapper.map(newPollDto,Poll.class);
    }

}
