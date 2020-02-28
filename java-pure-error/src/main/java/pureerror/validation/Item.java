package pureerror.validation;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import pureerror.StringValidation;

@Value.Immutable
public abstract class Item {
    @Value.Parameter
    public abstract int id();

    @Value.Parameter
    public abstract String name();

    public static Item of(final int id, String name) {
        return ImmutableItem.of(id, name);
    }

    @Value.Check
    protected void check() {
        Preconditions.checkState(
                id() >= 1,
                String.format("ID should be a least 1 (%d)", id()));

        Preconditions.checkState(
                StringValidation.isTrimmedAndNonEmpty(name()),
                String.format("Name should be trimmed and non empty (%s)", name()));
    }
}
