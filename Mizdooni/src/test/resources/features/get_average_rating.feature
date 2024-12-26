Feature: Calculate the average rating for restaurant

  Scenario: Calculate average rating with multiple reviews
    Given a restaurant named "restaurant1" managed by "manager1"
    And a user with username "user1" and role "client"
    And a review with food rating 4.5, service rating 4.0, ambiance rating 4.8, and overall rating 4.5 by "user1"
    And the review is added to the restaurant
    And a user with username "user2" and role "client"
    And a review with food rating 3.5, service rating 3.0, ambiance rating 3.8, and overall rating 3.4 by "user2"
    And the review is added to the restaurant
    When the average rating is calculated
    Then the average food rating should be 4.0
    And the average service rating should be 3.5
    And the average ambiance rating should be 4.3
    And the average overall rating should be 3.95