package pureerror.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.immutables.value.Value;

@SuppressWarnings("CodeBlock2Expr")
@Value.Immutable
public interface OrderForm {
    Seq<OrderLineForm> orderLines();

    static Validation<Seq<String>, Order> validate(final OrderForm orderForm) {
        final Validation<Seq<String>, Seq<OrderLine>> validatedOrderLines = Validation.traverse(
                orderForm.orderLines().zipWithIndex(),

                orderLineFormAndIndex -> {
                    final OrderLineForm orderLineForm = orderLineFormAndIndex._1;
                    final int index = orderLineFormAndIndex._2;

                    return OrderLineForm.validate(orderLineForm)
                            .mapError(errors -> errors.map(error -> "orderLine[" + index + "]:" + error));
                }
        );

        return validatedOrderLines.map(orderLines -> {
            return ImmutableOrder.builder().orderLines(orderLines).build();
        });
    }
}
