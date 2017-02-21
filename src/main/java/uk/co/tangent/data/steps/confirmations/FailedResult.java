package uk.co.tangent.data.steps.confirmations;

public class FailedResult extends Result {

    public FailedResult(Confirmation confirmation, Throwable e) {
        this(confirmation, e.getMessage());
        successful = false;
    }

    public FailedResult(Confirmation confirmation, String message) {
        this.confirmation = confirmation;
        this.message = message;
    }

}
