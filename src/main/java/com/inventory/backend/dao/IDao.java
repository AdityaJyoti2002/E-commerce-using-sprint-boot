package com.inventory.backend.dao;

import java.util.Optional;
import java.util.List;

public interface IDao <A, B> {

    public void create(A a);

    public Optional<A> findById(B id);

    public List<A> findAll();

    public void update(A a, B id);

    public void delete(B id);
    
}  
