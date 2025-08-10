package com.inventory.backend.services.impl;

import com.inventory.backend.dao.IDao;
import com.inventory.backend.entities.Employee;
import com.inventory.backend.services.IModelService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements IModelService<Employee, Long> {

    private IDao<Employee, Long> employeeDao;

    public EmployeeService(IDao<Employee, Long> employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Override
    public Optional<Employee> save(Employee employee) {
        employeeDao.create(employee);
        return Optional.of(employee);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeDao.findById(id);
    }

    @Override
    public List<Employee> findAll() {
        return employeeDao.findAll();
    }

    @Override
    public Optional<Employee> update(Employee employee, Long id) {
        employeeDao.update(employee, id);
        return Optional.of(employee);
    }

    @Override
    public void delete(Long id) {
        employeeDao.delete(id);
    }
    
}
