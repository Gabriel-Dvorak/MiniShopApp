package at.spengergasse.Mini.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {super(message);}

    public static ServiceException ofNotFound(Long id) {
        return new ServiceException("Entity mit ID " + id + " nicht gefunden!");
    }
}