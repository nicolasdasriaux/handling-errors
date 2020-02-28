package pureerror.validation;

import io.vavr.collection.Seq;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Order {
    @Value.Parameter
    public abstract Seq<OrderLine> orderLines();

    public static Order of(final Seq<OrderLine> orderLines) {
        return ImmutableOrder.of(orderLines);
    }
}
