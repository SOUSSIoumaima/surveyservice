package horizon.surveyservice.repository;

import horizon.surveyservice.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestionQuestionId(Long questionId);
}
