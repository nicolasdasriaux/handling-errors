package pureerror.either;

import io.vavr.control.Either;

import static pureerror.Examples.example;
import static pureerror.Examples.part;

public class EitherExamples {
    public static void main(final String[] args) {
        part("Either", () -> {
            example("Success / Failure", () -> {
                final Either<String, Integer> success = Either.right(1);
                System.out.println("success = " + success);
                final Either<String, Integer> failure = Either.left("FAILURE");
                System.out.println("failure = " + failure);
            });

            example("map", () -> {
                final Either<String, Integer> success =
                        Either.<String, Integer>right(1)
                                .map(i -> i * 10);

                System.out.println("success = " + success);

                final Either<String, Integer> failure =
                        Either.<String, Integer>left("FAILURE")
                                .map(i -> i * 10);

                System.out.println("failure = " + failure);
            });

            example("flatMap", () -> {
                final Either<String, Integer> successSuccess =
                        Either.<String, Integer>right(1)
                                .flatMap(i -> Either.right(i * 10));

                System.out.println("successSuccess = " + successSuccess);

                final Either<String, Integer> failureSuccess =
                        Either.<String, Integer>left("FAILURE 1")
                                .flatMap(i -> Either.right(i * 10));

                System.out.println("failureSuccess = " + failureSuccess);

                final Either<String, Integer> successFailure =
                        Either.<String, Integer>right(1)
                                .flatMap(i -> Either.left(String.format("FAILURE 2 (%d)", i)));

                System.out.println("successFailure = " + successFailure);

                final Either<String, Integer> failureFailure =
                        Either.<String, Integer>left("FAILURE 1")
                                .flatMap(i -> Either.left(String.format("FAILURE 2 (%d)", i)));

                System.out.println("failureFailure = " + failureFailure);
            });

            example("String extraction", () -> {
                final Either<String, Point> success = Point.fromString("1, 3");
                System.out.println("success = " + success);
                final Either<String, Point> failure1 = Point.fromString("1,2,3");
                System.out.println("failure1 = " + failure1);
                final Either<String, Point> failure2 = Point.fromString("a,2");
                System.out.println("failure2 = " + failure2);
                final Either<String, Point> failure3 = Point.fromString("1,b");
                System.out.println("failure3 = " + failure3);
            });

            example("Form validation", () -> {
                final Either<String, Item> success = Item.fromForm(ImmutableItemForm.builder().id(" 1 ").name("  Ball  ").build());
                System.out.println("success = " + success);
                final Either<String, Item> failure1 = Item.fromForm(ImmutableItemForm.builder().id(" a ").name("  Ball  ").build());
                System.out.println("failure1 = " + failure1);
                final Either<String, Item> failure2 = Item.fromForm(ImmutableItemForm.builder().id(" -1 ").name("  Ball  ").build());
                System.out.println("failure2 = " + failure2);
                final Either<String, Item> failure3 = Item.fromForm(ImmutableItemForm.builder().id(" 1 ").name("   ").build());
                System.out.println("failure3 = " + failure3);
                final Either<String, Item> failure4 = Item.fromForm(ImmutableItemForm.builder().id("   ").name(" Ball ").build());
                System.out.println("failure4 = " + failure4);
            });
        });
    }
}

