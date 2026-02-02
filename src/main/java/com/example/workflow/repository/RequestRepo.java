package com.example.workflow.repository;

import com.example.workflow.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<RequestEntity,Long> {}