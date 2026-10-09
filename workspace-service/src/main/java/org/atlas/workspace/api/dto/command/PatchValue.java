package org.atlas.workspace.api.dto.command;

public sealed interface PatchValue<T>
        permits PatchValue.Unchanged, PatchValue.Set, PatchValue.Clear  {

    record Unchanged<T>() implements PatchValue<T> {}

    record Set<T>(T value) implements PatchValue<T> {}

    record Clear<T>() implements PatchValue<T> {}
}
