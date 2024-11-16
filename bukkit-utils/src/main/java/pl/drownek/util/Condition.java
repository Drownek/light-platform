package pl.drownek.util;

import lombok.Data;

@Data(staticConstructor = "of")
public class Condition {

    private final boolean condition;
    private final String message;
}
