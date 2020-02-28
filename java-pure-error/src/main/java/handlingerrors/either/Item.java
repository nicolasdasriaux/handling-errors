package handlingerrors.either;

import com.google.common.base.Preconditions;
import handlingerrors.StringValidation;
import io.vavr.control.Either;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Item {
    public abstract int id();
    public abstract String name();

    @Value.Check
    protected void check() {
        Preconditions.checkState(
                id() >= 1,
                String.format("ID should be a least 1 (%d)", id()));

        Preconditions.checkState(
                StringValidation.isTrimmedAndNonEmpty(name()),
                String.format("Name should be trimmed and non empty (%s)", name()));
    }

    public static Either<String, Item> fromForm(final ItemForm form) {
        return Validations.validatePositiveInt(form.id()).mapLeft(e -> "id: " + e).flatMap(id -> {
            return Validations.validateString(form.name()).mapLeft(e -> "name: " + e).map(name -> {
                return ImmutableItem.builder()
                        .id(id)
                        .name(name)
                        .build();
            });
        });
    }
}
