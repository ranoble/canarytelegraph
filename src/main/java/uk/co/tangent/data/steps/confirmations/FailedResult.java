package uk.co.tangent.data.steps.confirmations;

public class FailedResult extends Result {

    public FailedResult(Confirmation confirmation, Throwable e) {
        this(confirmation, e.getMessage());
        setSuccessful(false);
    }

    public FailedResult(Confirmation confirmation, String message) {
        setConfirmation(confirmation);
        setMessage(message);
    }

}
