Feature: Checkout
  Scenario: Scan 1 bottle of Milk
    Given the price of "Milk" is $3.99
    When I scan 1 "Milk"
    Then the total price should be $3.99