package com.ruleengine.ruleapplication.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruleengine.ruleapplication.entity.Rule;
import com.ruleengine.ruleapplication.model.ASTNode;
import com.ruleengine.ruleapplication.repository.RuleRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EvaluationService {

  @Autowired
  private RuleRepository ruleRepository;

  @Autowired
  private ObjectMapper objectMapper;

  public boolean evaluateRuleByName(String ruleName, Map<String, Object> data) {
    try {
        Rule rule = ruleRepository.findByName(ruleName) // You'll need to implement this method in the repository
            .orElseThrow(() -> new IllegalArgumentException("Rule not found"));

        ASTNode astNode = objectMapper.readValue(
            rule.getAstJson(),
            ASTNode.class
        );
        return astNode.evaluate(data);
    } catch (Exception e) {
        throw new RuntimeException("Failed to evaluate rule", e);
    }
}

}