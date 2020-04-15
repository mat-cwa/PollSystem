package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.NewAnswerDto;
import github.com.matcwa.model.entity.Answer;
import github.com.matcwa.api.dto.AnswerDto;
import org.modelmapper.ModelMapper;

public class AnswerMapper {

    private static ModelMapper modelMapper = new ModelMapper();

    public static AnswerDto toDto(Answer answer) {
        return modelMapper.map(answer, AnswerDto.class);
    }

    public static Answer newToSource(NewAnswerDto newAnswerDto) {
        return modelMapper.map(newAnswerDto, Answer.class);
    }
}
