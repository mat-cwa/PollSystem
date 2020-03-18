package github.com.matcwa.repository;

import github.com.matcwa.api.error.PollError;
import github.com.matcwa.model.Poll;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {

}
