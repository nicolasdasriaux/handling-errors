package pureerror;

import io.vavr.Function1;

public class StringFunctions {
    public static Function1<String, String> prefixWith(final String prefix) {
        return s -> prefix + s;
    }
}
