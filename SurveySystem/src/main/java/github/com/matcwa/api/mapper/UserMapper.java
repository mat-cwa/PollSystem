package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.PollDto;
import github.com.matcwa.api.dto.UserDto;
import github.com.matcwa.model.entity.User;
import github.com.matcwa.api.dto.VoteDto;
import org.modelmapper.ModelMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    private static ModelMapper modelMapper = new ModelMapper();

    public static UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
