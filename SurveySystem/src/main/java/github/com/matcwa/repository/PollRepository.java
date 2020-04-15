package github.com.matcwa.repository;

import github.com.matcwa.model.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {

}
