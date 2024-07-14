package backend.authModule.exception;

public class InvalidOldPasswordException extends Exception{
    public InvalidOldPasswordException(String message){
        super(message);
    }

}
