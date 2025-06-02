package horizon.surveyservice.repository;

import horizon.surveyservice.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByQuestionQuestionId(UUID questionId);
}
