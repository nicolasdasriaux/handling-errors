package pureerror.either;

import io.vavr.control.Either;
import org.immutables.value.Value;

import static pureerror.either.Validations.*;

@SuppressWarnings("CodeBlock2Expr")
@Value.Immutable
public interface ItemForm {
    String id();
    String name();

    static Either<String, Item> validate(final ItemForm form) {
        return validatePositiveIntString(form.id()).mapLeft(e -> "id: " + e).flatMap(id -> {
            return validateString(form.name()).mapLeft(e -> "name: " + e).map(name -> {
                return Item.of(id, name);
            });
        });
    }
}
