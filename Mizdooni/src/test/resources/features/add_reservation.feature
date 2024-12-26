Feature: Add Reservation to a User

  Scenario: Successfully add a reservation
    Given a user with username "User1" and role "client"
    And a restaurant named "restaurant1" managed by "manager1"
    And a table with 4 seats in "restaurant1"
    And a reservation for "2024-12-31T18:00:00"
    When the reservation is added to the user
    Then the reservation list size should be 1
    And the reservation number should be 0
    And the table should have 1 reservations

  Scenario: Add multiple reservations to a user
    Given a user with username "user2" and role "client"
    And a restaurant named "restaurant2" managed by "manager2"
    And a table with 6 seats in "restaurant2"
    And add reservations:
      | 2024-12-30T12:00:00 |
      | 2024-12-31T20:00:00 |
    When the reservations are added to the user
    Then the reservation list size should be 2
    And the reservation numbers should be sequential
    And the table should have 2 reservations

  Scenario: Ensure reservation is linked to the correct user and table
    Given a user with username "user3" and role "client"
    And a restaurant named "restaurant3" managed by "manager3"
    And a table with 2 seats in "restaurant3"
    And a reservation for "2024-12-31T19:00:00"
    When the reservation is added to the user
    Then the reservation's user should be "user3"
    And the reservation should be in the correct table
