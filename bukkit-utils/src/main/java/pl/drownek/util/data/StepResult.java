package pl.drownek.util.data;

public record StepResult(boolean isExit, boolean isSuccess, String message) {

    public static StepResult success() {
        return new StepResult(false, true, null);
    }

    public static StepResult fail(String message) {
        return new StepResult(false, false, message);
    }

    public static StepResult exit(String message) {
        return new StepResult(true, false, message);
    }

}
