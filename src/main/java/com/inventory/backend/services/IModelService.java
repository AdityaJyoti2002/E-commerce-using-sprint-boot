package com.inventory.backend.services;

import java.util.List;
import java.util.Optional;

public interface IModelService <A, B> {
    
    public Optional<A> save(A a);

    public Optional<A> findById(B id);

    public List<A> findAll();

    public Optional<A> update(A a, B id);

    public void delete(B id);

}
