package com.ruleengine.ruleapplication.parser;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {

  private TokenType type;
  private String value;

  public enum TokenType {
    OPERATOR,
    COMPARATOR,
    PARENTHESIS,
    ATTRIBUTE,
    VALUE,
    LOGICAL,
  }
}