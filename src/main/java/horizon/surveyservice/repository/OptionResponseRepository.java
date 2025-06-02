package horizon.surveyservice.repository;

import horizon.surveyservice.entity.OptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionResponseRepository extends JpaRepository<OptionResponse, Long> {
    List<OptionResponse> findByQuestionResponseQuestionResponseId(Long questionResponseId);
}
