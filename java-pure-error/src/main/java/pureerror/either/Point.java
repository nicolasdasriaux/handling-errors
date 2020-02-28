package pureerror.either;

import io.vavr.collection.Array;
import io.vavr.control.Either;
import org.immutables.value.Value;

import static pureerror.either.Validations.validateIntString;

@SuppressWarnings("CodeBlock2Expr")
@Value.Immutable
interface Point {
    @Value.Parameter
    int x();

    @Value.Parameter
    int y();

    static Point of(final int x, final int y) {
        return ImmutablePoint.of(x, y);
    }

    static Either<String, Point> fromString(final String s) {
        final Array<String> parts = Array.of(s.split(",", -1));

        if (parts.size() == 2) {
            return validateIntString(parts.get(0)).flatMap(x -> {
                return validateIntString(parts.get(1)).map(y -> {
                    return Point.of(x, y);
                });
            });
        } else {
            return Either.left("Expecting 2 parts");
        }
    }
}
