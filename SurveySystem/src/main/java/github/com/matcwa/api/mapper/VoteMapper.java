package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.NewVoteDto;
import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.api.dto.VoteDto;
import github.com.matcwa.model.entity.Vote;
import org.modelmapper.ModelMapper;

public class VoteMapper {
    private static ModelMapper modelMapper=new ModelMapper();
   public static VoteDto toDto(Vote vote){
       UserDto userDto = UserMapper.toDto(vote.getOwner());
       AnswerDto answerDto = AnswerMapper.toDto(vote.getAnswer());
       return new VoteDto(vote.getId(),userDto,answerDto);
    }
    public static Vote newToSource(NewVoteDto newVoteDto){
       return modelMapper.map(newVoteDto,Vote.class);
    }
}
