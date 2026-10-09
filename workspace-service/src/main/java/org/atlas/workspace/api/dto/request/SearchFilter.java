package org.atlas.workspace.api.dto.request;

public interface SearchFilter<T> {

    boolean applyOn(T entity);

}
