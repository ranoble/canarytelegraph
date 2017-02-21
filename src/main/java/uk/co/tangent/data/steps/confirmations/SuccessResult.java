package uk.co.tangent.data.steps.confirmations;

public class SuccessResult extends Result {

    public SuccessResult(Confirmation confirmation, String message) {
        this.confirmation = confirmation;
        this.message = message;
        successful = true;
    }

}
