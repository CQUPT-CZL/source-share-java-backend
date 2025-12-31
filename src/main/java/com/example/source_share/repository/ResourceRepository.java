package com.example.source_share.repository;

import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.ResourceNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceNode, Long> {
    boolean existsByCategoryCode(CategoryCode categoryCode);
}
