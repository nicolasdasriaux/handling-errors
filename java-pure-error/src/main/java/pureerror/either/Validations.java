package pureerror.either;

import io.vavr.control.Either;

class Validations {
    public static Either<String, String> validateString(final String s) {
        if (s == null) {
            return Either.left("Null string");
        } else {
            final String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                return Either.left("Blank string");
            } else {
                return Either.right(trimmed);
            }
        }
    }

    public static Either<String, Integer> validateInt(final String s) {
        try {
            if (s == null) {
                return Either.left("Null string");
            } else {
                return Either.right(Integer.parseInt(s.trim()));
            }
        } catch (final NumberFormatException ex) {
            return Either.left(String.format("Invalid integer string (%s)", s));
        }
    }

    public static Either<String, Integer> validatePositiveInt(final String s) {
        return validateInt(s)
                .filterOrElse(
                        i -> i > 0,
                        i -> String.format("Negative or zero int (%d)", i)
                );
    }
}
