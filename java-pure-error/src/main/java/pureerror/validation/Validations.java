package pureerror.validation;

import io.vavr.control.Validation;

class Validations {
    public static Validation<String, String> validateString(final String s) {
        if (s == null) {
            return Validation.invalid("Null string");
        } else {
            final String trimmed = s.trim();

            if (trimmed.isEmpty()) {
                return Validation.invalid("Blank string");
            } else {
                return Validation.valid(trimmed);
            }
        }
    }

    public static Validation<String, Integer> validateIntString(final String s) {
        return validateString(s)
                .flatMap(trimmed -> {
                    try {
                        return Validation.valid(Integer.parseInt(trimmed));
                    } catch (final NumberFormatException ex) {
                        return Validation.invalid(String.format("Invalid integer string (%s)", trimmed));
                    }
                });
    }

    public static Validation<String, Integer> validatePositiveIntString(final String s) {
        return validateIntString(s)
                .flatMap(i -> {
                    if (i > 0) {
                        return Validation.valid(i);
                    } else {
                        return Validation.invalid(String.format("Negative or zero int (%d)", i));
                    }
                });
    }
}
