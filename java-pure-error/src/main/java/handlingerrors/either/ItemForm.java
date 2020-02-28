package handlingerrors.either;

import org.immutables.value.Value;

@Value.Immutable
public interface ItemForm {
    String id();
    String name();
}

