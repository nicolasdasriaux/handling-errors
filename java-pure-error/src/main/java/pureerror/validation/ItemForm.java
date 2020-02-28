package pureerror.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.immutables.value.Value;

import static pureerror.validation.Validations.validatePositiveIntString;
import static pureerror.validation.Validations.validateString;

@SuppressWarnings("Convert2MethodRef")
@Value.Immutable
public interface ItemForm {
    String id();
    String name();

    static Validation<Seq<String>, Item> validate(final ItemForm itemForm) {
        return Validation
                .combine(
                        validatePositiveIntString(itemForm.id()).mapError(e -> "id: " + e),
                        validateString(itemForm.name()).mapError(e -> "name: " + e)
                )
                .ap((id, name) -> Item.of(id, name));
    }
}
