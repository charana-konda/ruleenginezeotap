
# Rule Engine with Abstract Syntax Tree (AST)

## Overview
This project is a 3-tier rule engine application designed to evaluate user eligibility based on defined attributes (e.g., age, department, salary, experience). The rule engine uses an **Abstract Syntax Tree (AST)** to represent complex conditional rules, allowing for dynamic creation, modification, and evaluation of these rules. By leveraging AST, the rule engine efficiently organizes and processes logical operations, comparisons, and even supports combining multiple rules for advanced conditions.

## Table of Contents
- [Project Features](#project-features)
- [Technologies Used](#technologies-used)
- [Data Structure](#data-structure)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Rule Examples](#rule-examples)
- [How to Run](#how-to-run)
- [Testing and Validation](#testing-and-validation)
- [Future Enhancements](#future-enhancements)

## Working Model Demo: 
https://drive.google.com/file/d/1SGNQTpNUgkd7Y2HJAhnRT6yvwo_UPq-J/view?usp=sharing

## Project Features
- **Rule Creation:** Define rules with conditions such as age, department, income, and experience.
- **AST Representation:** Each rule is parsed into an AST structure, which allows efficient storage and evaluation.
- **Rule Combination:** Support for combining multiple rules to create complex eligibility logic.
- **Rule Evaluation:** Evaluate rules against user data to determine eligibility.
- **Dynamic Rule Modification:** Update operators, operands, or sub-expressions in existing rules.
- **Error Handling and Validations:** Validates rule syntax, prevents undefined attribute usage, and handles invalid formats gracefully.

## Technologies Used
- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java 17, Spring Boot, Maven
- **Database**: PostgreSQL

## Data Structure
The AST data structure consists of `Node` objects representing either operators (AND, OR) or operands (conditions). Each `Node` can have the following fields:
- `type`: Indicates the type of the node ("operator" or "operand").
- `left`: Reference to the left child node (only for operator nodes).
- `right`: Reference to the right child node (only for operator nodes).
- `value`: Holds a value for operand nodes (e.g., numerical values or comparison values).

Example Node structure in Java:
```java
class Node {
    String type;       // "operator" or "operand"
    Node left;         // left child node
    Node right;        // right child node
    String value;      // specific value (used for operand nodes)
}
```

## Database Schema
### Database Choice: SQL Database (e.g., PostgreSQL)

The database stores rules and metadata associated with each rule, allowing persistence and management of multiple rules over time.

**Tables:**
1. **Rules Table**
   - `id`: Primary key
   - `rule_string`: Original string representation of the rule
   - `ast_json`: JSON representation of the AST
   - `created_at`: Timestamp of creation
   - `updated_at`: Timestamp of last modification

2. **Metadata Table (Optional)**
   - `attribute`: Name of the attribute (e.g., `age`, `department`)
   - `description`: Description of the attribute for rule definition reference

**Sample Entry:**
| id | rule_string                                      | ast_json                          | created_at         | updated_at         |
|----|--------------------------------------------------|-----------------------------------|--------------------|--------------------|
| 1  | "(age > 30 AND department = 'Sales')"            | `{"type": "operator", ...}`       | 2024-01-01 10:00  | 2024-01-01 10:00  |

---

## API Endpoints

### 1. `POST /rules/create_rule`
- **Description**: Creates a new rule based on a given rule name and rule string, then returns the rule's AST.
- **Request Body**:
  ```json
  {
    "ruleName": "rule1",
    "ruleString": "((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)"
  }
  ```
- **Response**: JSON representation of the created rule.

### 2. `POST /rules/combine_rules`
- **Description**: Combines multiple existing rules into a single rule using a specified operator (AND/OR).
- **Request Body**:
  ```json
  {
    "ruleNames": ["rule1", "rule2"],
    "combinedRuleName": "combinedRule",
    "operator": "AND"
  }
  ```
- **Response**: JSON representation of the combined rule.

### 3. `POST /rules/{name}/evaluate_rule`
- **Description**: Evaluates the specified rule against provided user data.
- **Path Parameter**: `name` (The name of the rule to evaluate)
- **Request Body**:
  ```json
  {
    "age": 35,
    "department": "Sales",
    "salary": 60000,
    "experience": 3
  }
  ```
- **Response**: Boolean result of the evaluation (`true` if the rule conditions match, `false` otherwise).

### 4. `GET /rules/all`
- **Description**: Retrieves a list of all stored rules with their names and rule strings.
- **Response**:
  ```json
  [
    {
      "ruleName": "rule1",
      "ruleString": "((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000 OR experience > 5)"
    },
    {
      "ruleName": "rule2",
      "ruleString": "(age > 30 AND department = 'Marketing') AND (salary > 20000 OR experience > 5)"
    }
  ]
  ```

### 5. `POST /rules/update_rule/{name}`
- **Description**: Updates an existing rule's conditions by providing a new rule string.
- **Path Parameter**: `name` (The name of the rule to update)
- **Request Body**:
  ```json
  {
    "ruleString": "(age > 35 AND department = 'HR') AND (salary > 70000)"
  }
  ```
- **Response**: JSON representation of the updated rule.

### 6. `DELETE /rules/{name}`
- **Description**: Deletes a rule by its name.
- **Path Parameter**: `name` (The name of the rule to delete)
- **Response**: 204 No Content on successful deletion.

### 7. `GET /rules/{name}/attributes`
- **Description**: Retrieves a list of attributes involved in a specified rule.
- **Path Parameter**: `name` (The name of the rule)
- **Response**:
  ```json
  ["age", "department", "salary", "experience"]
  ```

---

## How to Run
1. **Clone the Repository**
   ```bash
   git clone https://github.com/example/rule-engine-ast.git
   cd rule-engine-ast
   ```
2. **Build the Project**
   ```bash
   mvn clean install
   ```
3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access APIs**: Use a tool like Postman or cURL to interact with the endpoints.

## Testing and Validation
1. **Test Case 1**: Create individual rules and verify AST representation.
2. **Test Case 2**: Combine rules and verify the resulting AST.
3. **Test Case 3**: Provide sample user data to `evaluate_rule` and check eligibility result.
4. **Error Handling Tests**: Ensure invalid rule strings and data formats raise appropriate errors.

## Future Enhancements
- **Support for User-Defined Functions**: Allow advanced custom conditions in rule definitions.
- **Extended Rule Modification**: Enable more detailed updates to AST structure, such as changing specific nodes or values.
- **Caching Mechanism**: To speed up frequent rule evaluations for the same set of data.


---

