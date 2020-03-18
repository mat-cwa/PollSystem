package github.com.matcwa.repository;

import github.com.matcwa.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {

}
