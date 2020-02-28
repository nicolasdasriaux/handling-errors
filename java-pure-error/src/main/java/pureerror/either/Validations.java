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

    public static Either<String, Integer> validateIntString(final String s) {
        return validateString(s)
                .flatMap(trimmed -> {
                    try {
                        return Either.right(Integer.parseInt(trimmed));
                    } catch (final NumberFormatException ex) {
                        return Either.left(String.format("Invalid integer string (%s)", trimmed));
                    }
                });
    }

    public static Either<String, Integer> validatePositiveIntString(final String s) {
        return validateIntString(s)
                .flatMap(i -> {
                    if (i > 0) {
                        return Either.right(i);
                    } else {
                        return Either.left(String.format("Negative or zero int (%d)", i));
                    }
                });
    }
}
