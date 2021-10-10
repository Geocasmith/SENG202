Feature: Database
  Scenario: Adding to database
    Given I am connected to a database
    And I have a record
    When I add the record to the database
    Then the record should be in the database