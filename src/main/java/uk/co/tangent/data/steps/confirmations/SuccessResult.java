package uk.co.tangent.data.steps.confirmations;

public class SuccessResult extends Result {

    public SuccessResult(Confirmation confirmation, String message) {
        setConfirmation(confirmation);
        setMessage(message);
        setSuccessful(true);
    }

}
