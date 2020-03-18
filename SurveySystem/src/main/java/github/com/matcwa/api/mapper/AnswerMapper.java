package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.NewAnswerDto;
import github.com.matcwa.model.Answer;
import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.api.dto.VoteDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class AnswerMapper {

    private static ModelMapper modelMapper=new ModelMapper();

    public static AnswerDto toDto(Answer answer){
        Set<VoteDto> voteDtos=answer.getVotes().stream().map(VoteMapper::toDto).collect(Collectors.toSet());
        QuestionDto question = modelMapper.map(answer.getQuestion(), QuestionDto.class);
        return new AnswerDto(answer.getId(),answer.getAnswerDescription(),voteDtos,question);
    }
    public static Answer newToSource(NewAnswerDto newAnswerDto) {
        return modelMapper.map(newAnswerDto,Answer.class);
    }
}
