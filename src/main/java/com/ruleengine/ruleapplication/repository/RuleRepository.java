

package com.ruleengine.ruleapplication.repository;

import com.ruleengine.ruleapplication.entity.Rule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {
    
    // New method to find all rules by their names
    List<Rule> findAllByNameIn(List<String> ruleNames);
    Optional<Rule> findByName(String name);
    boolean existsByName(String name); 
    
}
