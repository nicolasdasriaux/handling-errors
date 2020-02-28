package pureerror.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.immutables.value.Value;

import static pureerror.validation.Validations.*;

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
@Value.Immutable
public interface OrderLineForm {
    @Value.Parameter
    String itemId();

    @Value.Parameter
    String quantity();

    static OrderLineForm of(final String itemId, final String quantity) {
        return ImmutableOrderLineForm.of(itemId, quantity);
    }

    static Validation<Seq<String>, OrderLine> validate(final OrderLineForm form) {
        return Validation
                .combine(
                        validatePositiveIntString(form.itemId())
                                .mapError(e -> "itemId: " + e),
                        validatePositiveIntString((form.quantity()))
                                .mapError(e -> "quantity: " + e)
                )
                .ap((itemId, quantity) -> {
                    return OrderLine.of(itemId, quantity);
                });
    }
}
