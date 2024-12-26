Feature: Add a review for restaurant

  Scenario: Successfully add a review to a restaurant
    Given a restaurant named "restaurant1" managed by "manager1"
    And a user with username "user1" and role "client"
    And a review with food rating 4.5, service rating 4.0, ambiance rating 4.8, and overall rating 4.5 by "user1"
    When the review is added to the restaurant
    Then the restaurant should have 1 review
    And the review should be written by "user1"

  Scenario: Replace an existing review from the same user
    Given a restaurant named "restaurant2" managed by "manager2"
    And a user with username "user1" and role "client"
    And a review with food rating 4.5, service rating 4.0, ambiance rating 4.8, and overall rating 4.5 by "user1"
    And the review is added to the restaurant
    And a user with username "user2" and role "client"
    And a review with food rating 3.5, service rating 3.8, ambiance rating 3.9, and overall rating 3.7 by "user2"
    When the review is added to the restaurant
    Then the restaurant should have 2 review