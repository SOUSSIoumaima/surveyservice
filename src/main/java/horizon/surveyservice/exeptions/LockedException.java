package horizon.surveyservice.exeptions;

public class LockedException extends RuntimeException{
    public LockedException(String message){
        super (message);
    }
}
