package com.ruleengine.ruleapplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruleengine.ruleapplication.entity.Rule;
import com.ruleengine.ruleapplication.exception.DuplicateRuleNameException;
import com.ruleengine.ruleapplication.exception.RuleParsingException;
import com.ruleengine.ruleapplication.model.ASTNode;
import com.ruleengine.ruleapplication.model.OperatorNode;
import com.ruleengine.ruleapplication.parser.Ruleparser;
import com.ruleengine.ruleapplication.repository.RuleRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Rule createRule(String ruleName, String ruleString) {
        if (ruleRepository.existsByName(ruleName)) { // Check for duplicate rule name
            throw new DuplicateRuleNameException("Rule name already exists: " + ruleName);
        }

        try {
            Ruleparser parser = new Ruleparser();
            ASTNode ast = parser.parse(ruleString);  // Parse the rule string
            String astJson = objectMapper.writeValueAsString(ast);  // Convert AST to JSON

            Rule rule = new Rule();
            rule.setName(ruleName);  // Use the provided rule name
            rule.setRuleString(ruleString);
            rule.setAstJson(astJson);
            rule.setCreatedAt(LocalDateTime.now());
            rule.setUpdatedAt(LocalDateTime.now());

            return ruleRepository.save(rule);  // Save the rule to the database
        } catch (RuleParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create rule", e);
        }
    }

    public Rule combineRules(List<String> ruleNames, String combinedRuleName, String operator) {
      // Check for duplicate combined rule name
      if (ruleRepository.existsByName(combinedRuleName)) {
          throw new DuplicateRuleNameException("Combined rule name already exists: " + combinedRuleName);
      }
  
      try {
          List<Rule> rules = ruleRepository.findAllByNameIn(ruleNames);
          if (rules.isEmpty()) {
              throw new IllegalArgumentException("No rules found for provided names");
          }
  
          List<ASTNode> astNodes = new ArrayList<>();
          StringBuilder combinedRuleString = new StringBuilder();
  
          for (int i = 0; i < rules.size(); i++) {
              Rule rule = rules.get(i);
              ASTNode astNode = objectMapper.readValue(rule.getAstJson(), ASTNode.class);
              astNodes.add(astNode);
  
              // Append the rule string
              combinedRuleString.append("(").append(rule.getRuleString()).append(")");
              if (i < rules.size() - 1) { // Add the operator if not the last rule
                  combinedRuleString.append(" ").append(operator).append(" ");
              }
          }
  
          // Combine AST nodes using the specified operator
          ASTNode combinedAst = combineAstNodes(astNodes, operator);
          String astJson = objectMapper.writeValueAsString(combinedAst);
  
          Rule combinedRule = new Rule();
          combinedRule.setName(combinedRuleName); // Set the combined rule name from user input
          combinedRule.setRuleString(combinedRuleString.toString()); // Use the combined rule string
          combinedRule.setAstJson(astJson);
          combinedRule.setCreatedAt(LocalDateTime.now());
          combinedRule.setUpdatedAt(LocalDateTime.now());
  
          // Save the combined rule to the repository
          return ruleRepository.save(combinedRule);
      } catch (Exception e) {
          throw new RuntimeException("Failed to combine rules", e);
      }
  }
  
  
  private ASTNode combineAstNodes(List<ASTNode> astNodes, String operator) {
      ASTNode combined = astNodes.get(0);
      for (int i = 1; i < astNodes.size(); i++) {
          combined = new OperatorNode(operator, combined, astNodes.get(i)); // Use the operator passed from the user
      }
      return combined;
  }
  

    public List<Map<String, String>> getAllRules() {
        List<Rule> rules = ruleRepository.findAll(); // Fetch all rules from the repository
        return rules.stream()
            .map(rule -> Map.of(
                "name", rule.getName(),
                "ruleString", rule.getRuleString()
            ))
            .collect(Collectors.toList()); // Map to a list of maps containing name and ruleString
    }

    public Rule updateRule(String name, String newRuleString) {
        Optional<Rule> optionalRule = ruleRepository.findByName(name); // Find a rule by name
        if (!optionalRule.isPresent()) {
            throw new IllegalArgumentException("Rule not found with name: " + name); // Handle not found case
        }

        // Check for duplicate rule name if the name is being changed
        if (ruleRepository.existsByName(newRuleString) && !name.equals(newRuleString)) {
            throw new DuplicateRuleNameException("Rule name already exists: " + newRuleString);
        }

        Rule rule = optionalRule.get();
        rule.setRuleString(newRuleString); // Update the rule string
        // If you want to update the AST or any other fields, do that here
        return ruleRepository.save(rule); // Save the updated rule
    }
    
    public void deleteRuleByName(String name) {
      Optional<Rule> optionalRule = ruleRepository.findByName(name); // Fetch the rule by name
      if (optionalRule.isPresent()) {
          ruleRepository.delete(optionalRule.get()); // Delete the rule if it exists
      } else {
          throw new IllegalArgumentException("Rule not found with name: " + name); // Handle not found case
      }
  }
  public List<String> getAttributesByRuleName(String ruleName) {
    Optional<Rule> optionalRule = ruleRepository.findByName(ruleName);
    if (optionalRule.isPresent()) {
        Rule rule = optionalRule.get();
        // Assuming the attributes are stored in the rule string or can be derived from the AST
        return extractAttributesFromRule(rule);
    } else {
        throw new IllegalArgumentException("Rule not found with name: " + ruleName);
    }
}
  private List<String> extractAttributesFromRule(Rule rule) {
    String ruleString = rule.getRuleString();
    // Regex to match variable names in the rule string.
    // This example assumes variables are alphanumeric and can include underscores.
    // Adjust regex according to your rule string format.
    String regex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b";
    
    // Create a Set to avoid duplicate attributes
    Set<String> attributes = new HashSet<>();
    
    // Split the rule string by logical operators AND, OR, and also by comparison operators
    String[] parts = ruleString.split("(?i)\\s+(AND|OR)\\s+|(?<=\\s)(<|<=|>|>=|==|!=|=)(?=\\s)"); // Handles AND/OR and comparison operators

    for (String part : parts) {
        // Find matches using regex
        Matcher matcher = Pattern.compile(regex).matcher(part);
        while (matcher.find()) {
            attributes.add(matcher.group(1)); // Add attribute to the Set
        }
    }
    
    // Convert Set to List and return
    return new ArrayList<>(attributes);
}

}
