package github.com.matcwa.api.mapper;

import github.com.matcwa.api.dto.AnswerDto;
import github.com.matcwa.api.dto.NewQuestionDto;
import github.com.matcwa.api.dto.QuestionDto;
import github.com.matcwa.model.Question;
import org.modelmapper.ModelMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class QuestionMapper {
    private static ModelMapper modelMapper = new ModelMapper();

    public static QuestionDto toDto(Question question) {
        return modelMapper.map(question,QuestionDto.class);
    }

    public static Question newToSource(NewQuestionDto newQuestionDto) {
        Question question = modelMapper.map(newQuestionDto, Question.class);

        return new Question(newQuestionDto.getQuestionDescription(), question.getAnswers(), question.getPoll());
    }
    public static Question dtoToSource(QuestionDto questionDto){
        return modelMapper.map(questionDto,Question.class);
    }

}
