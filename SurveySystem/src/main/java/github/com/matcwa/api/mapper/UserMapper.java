package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.model.User;
import github.com.matcwa.api.dto.VoteDto;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
public static UserDto toDto(User user){
    Set<PollDto> polls = user.getPollSet().stream().map(PollMapper::toDto).collect(Collectors.toSet());
    Set<VoteDto> votes = user.getVotes().stream().map(VoteMapper::toDto).collect(Collectors.toSet());
    return new UserDto(user.getId(),user.getUsername(),polls,votes);
}
}
