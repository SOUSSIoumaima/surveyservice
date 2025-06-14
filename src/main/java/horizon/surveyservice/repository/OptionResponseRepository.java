package horizon.surveyservice.repository;

import horizon.surveyservice.entity.OptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionResponseRepository extends JpaRepository<OptionResponse, UUID> {
    List<OptionResponse> findByQuestionResponseQuestionResponseId(UUID questionResponseId);
}
