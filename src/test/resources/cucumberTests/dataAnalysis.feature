Feature: Analyse
  Scenario: Analysing time between two records
    Given I have two records that occurred 20 minutes apart
    When I analyse these two records time difference
    Then the analysis should say the records occurred 20 minutes apart

  Scenario: Analysing distance between two records
    Given I have two records that occurred 16 kilometers apart
    When I analyse these two records location difference
    Then the analysis should say the records occurred 16 kilometers apart

  Scenario: Sorting crime types by descending frequency
    Given I have a list of records
    When I sort the records by descending crime type frequency
    Then the crime types should be in descending frequency order

  Scenario: Sorting blocks by descending frequency
    Given I have a list of records
    When I sort the records by descending block frequency
    Then the blocks should be in descending frequency order