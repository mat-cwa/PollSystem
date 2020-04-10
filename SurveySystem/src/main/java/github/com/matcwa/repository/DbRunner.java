package github.com.matcwa.repository;

import github.com.matcwa.model.Answer;
import github.com.matcwa.model.Poll;
import github.com.matcwa.model.Vote;
import github.com.matcwa.repository.PollRepository;
import github.com.matcwa.model.Question;
import github.com.matcwa.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DbRunner implements CommandLineRunner {
    PollRepository pollRepository;
    QuestionRepository questionRepository;
    VoteRepository voteRepository;


    @Autowired
    public DbRunner(PollRepository pollRepository,QuestionRepository questionRepository,VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.questionRepository=questionRepository;
        this.voteRepository=voteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Set<Question> questions = new HashSet<>();
        Set<Answer> answers=new HashSet<>();
        Set<Vote> votes = new HashSet<>();

        Question question=new Question("pierwsze pytanie dodane przez DbRunner");
        question.setAnswers(answers);
        Answer answer=new Answer("pierwsza odpowiedz przez DbRunner",votes,question);
        Vote vote=new Vote(answer);

        answers.add(answer);
        questions.add(question);
        votes.add(vote);

        Poll poll = new Poll("Ankieta tworzona przez DbRunner", questions);
        question.setPoll(poll);

        pollRepository.save(poll);
    }
}
