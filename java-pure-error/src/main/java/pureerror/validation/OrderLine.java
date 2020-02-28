package pureerror.validation;

import org.immutables.value.Value;

@Value.Immutable
public abstract class OrderLine {
    @Value.Parameter
    public abstract int itemId();

    @Value.Parameter
    public abstract int quantity();

    public static OrderLine of(final int itemId, final int quantity) {
        return ImmutableOrderLine.of(itemId, quantity);
    }
}
