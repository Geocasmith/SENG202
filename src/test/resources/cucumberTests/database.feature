Feature: Database
  Scenario: Adding a record to a database
    Given I am connected to a database
    And I have a record
    When I add the record to the database
    Then the record should be in the database

  Scenario: Deleting a record from a database
    Given I am connected to a database
    And I have a record in the database
    When I delete a record from the database
    Then the record should not be in the database

  Scenario: Editing the date of a record
    Given I am connected to a database
    And I have a record in the database
    When I change the date of the record to "11/23/2020 03:05:00 PM"
    Then the date of the record in the database should be "11/23/2020 03:05:00 PM"

  Scenario: Filter crimes by ward
    Given I am connected to a database that contains records
    When I filter the crimes to only crimes in "ward" 5
    Then only crimes from "ward" 5 should be shown

  Scenario: Filter crimes by beat
    Given I am connected to a database that contains records
    When I filter the crimes to only crimes in "beat" 632
    Then only crimes from "beat" 632 should be shown

  Scenario: Filter crimes by type
    Given I am connected to a database that contains records
    When I filter the crimes to only crimes of type "arson"
    Then only crimes of type "arson" should be shown

  Scenario: Filter crimes by location description
    Given I am connected to a database that contains records
    When I filter the crimes to only crimes from a "barbershop"
    Then only crimes from a "barbershop" should be shown

  Scenario: Filter crimes by radius
    Given I am connected to a database that contains records
    When I filter the crimes to only crimes within 1000 meters of (41.93688201904297, 87.648193359375)
    Then only crimes within 1000 meters of (41.93688201904297, 87.648193359375) should be shown

