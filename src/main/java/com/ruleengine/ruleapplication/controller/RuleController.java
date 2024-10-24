
package com.ruleengine.ruleapplication.controller;

import com.ruleengine.ruleapplication.entity.Rule;
import com.ruleengine.ruleapplication.exception.DuplicateRuleNameException;
import com.ruleengine.ruleapplication.service.EvaluationService;
import com.ruleengine.ruleapplication.service.RuleService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rules")
public class RuleController {

  @Autowired
  private RuleService ruleService;

  @Autowired
  private EvaluationService evaluationService;

  @PostMapping("/create_rule")
  public ResponseEntity<?> createRule(
      @RequestBody Map<String, String> request
  ) {
      try {
          String ruleName = request.get("ruleName");  
          String ruleString = request.get("ruleString");  
          Rule rule = ruleService.createRule(ruleName, ruleString);  
          return ResponseEntity.ok(rule);
      } catch (DuplicateRuleNameException e) {
          return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // Return 400 with message
      } catch (IllegalArgumentException e) {
          return ResponseEntity.notFound().build(); // Handle not found case
      }
  }


 
  @PostMapping("/combine_rules")
public ResponseEntity<Rule> combineRules(
    @RequestBody Map<String, Object> request
) {
    @SuppressWarnings("unchecked")
    List<String> ruleNames = (List<String>) request.get("ruleNames"); // List of rule names
    String combinedRuleName = (String) request.get("combinedRuleName"); // Combined rule name from the request
    Rule combinedRule = ruleService.combineRules(ruleNames, combinedRuleName); // Pass both to the service
    return ResponseEntity.ok(combinedRule);
}

  

@PostMapping("/{name}/evaluate_rule")
public ResponseEntity<Map<String, Boolean>> evaluateRule(
    @PathVariable String name, // Change UUID id to String name
    @RequestBody Map<String, Object> data
) {
    boolean result = evaluationService.evaluateRuleByName(name, data); // Call the new service method
    return ResponseEntity.ok(Map.of("result", result));
}

  @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllRules() {
        List<Map<String, String>> rules = ruleService.getAllRules(); // Call the service method to fetch all rules
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/update_rule/{name}")
    public ResponseEntity<?> updateRule(
        @PathVariable String name,
        @RequestBody Map<String, String> request
    ) {
        try {
            String newRuleString = request.get("ruleString");
            Rule updatedRule = ruleService.updateRule(name, newRuleString);
            return ResponseEntity.ok(updatedRule);
        } catch (DuplicateRuleNameException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // Return 400 with message
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Handle not found case
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRule(@PathVariable String name) {
        try {
            ruleService.deleteRuleByName(name);
            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // Handle not found case
        }
    }
}