package backend.authModule.exception;

public class ProfessionnelSanteNotFoundException  extends  Exception{
    public ProfessionnelSanteNotFoundException(String message) {
        super(message);
    }

    public ProfessionnelSanteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
