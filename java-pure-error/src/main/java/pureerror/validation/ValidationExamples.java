package pureerror.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import static pureerror.Examples.example;
import static pureerror.Examples.part;

public class ValidationExamples {
    public static void main(final String[] args) {
        part("Validation", () -> {
            example("Item Form validation", () -> {
                final Validation<Seq<String>, Item> success = ItemForm.validate(ImmutableItemForm.builder().id(" 1 ").name("  Ball  ").build());
                System.out.println("success = " + success);
                final Validation<Seq<String>, Item> failure1 = ItemForm.validate(ImmutableItemForm.builder().id(" a ").name(" Ball ").build());
                System.out.println("failure1 = " + failure1);
                final Validation<Seq<String>, Item> failure2 = ItemForm.validate(ImmutableItemForm.builder().id(" -1 ").name("    ").build());
                System.out.println("failure2 = " + failure2);
            });

            example("Order Form validation", () -> {
                final Validation<Seq<String>, Order> success = OrderForm.validate(
                        ImmutableOrderForm.builder()
                                .addOrderLine(
                                        OrderLineForm.of("1AAA", "3"),
                                        OrderLineForm.of("2", "1BBB")
                                )
                                .build()
                );

                System.out.println("success = " + success);
            });
        });
    }
}
