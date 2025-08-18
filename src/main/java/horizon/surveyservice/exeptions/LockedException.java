package horizon.surveyservice.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LockedException extends RuntimeException{
    public LockedException(String message){
        super (message);
    }
}
