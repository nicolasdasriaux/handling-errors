package pureerror.either;

import io.vavr.control.Either;
import org.immutables.value.Value;

import static pureerror.either.Validations.*;

@SuppressWarnings("CodeBlock2Expr")
@Value.Immutable
public interface ItemForm {
    @Value.Parameter
    String id();

    @Value.Parameter
    String name();

    static ItemForm of(final String id, final String name) {
        return ImmutableItemForm.of(id, name);
    }

    static Either<String, Item> validate(final ItemForm form) {
        return validatePositiveIntString(form.id())
                .mapLeft(error -> "id: " + error)
                .flatMap(id -> {
                    return validateString(form.name())
                            .mapLeft(error -> "name: " + error)
                            .map(name -> {
                                return Item.of(id, name);
                            });
                });
    }
}
