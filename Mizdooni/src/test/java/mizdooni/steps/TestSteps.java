package mizdooni.steps;

import io.cucumber.java.en.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class TestSteps {
    private User user;
    private Restaurant restaurant;
    private Table table;
    private Reservation reservation;
    private List<Reservation> reservations;
    private Review review;
    private Rating averageRating;

    @Given("a restaurant named {string} managed by {string}")
    public void aRestaurantNamedManagedBy(String restaurantName, String managerUsername) {
        User manager = new User(managerUsername, "password", "manager@example.com", null, User.Role.manager);
        restaurant = new Restaurant(restaurantName, manager, "type1", null, null, null, null, null);
    }

    @Given("a user with username {string} and role {string}")
    public void aUserWithUsernameAndRole(String username, String role) {
        User.Role userRole = User.Role.valueOf(role);
        user = new User(username, "password", "client@example.com", null, userRole);
    }

    @And("a table with {int} seats in {string}")
    public void aTableWithSeatsInRestaurant(int seats, String restaurantName) {
        assertNotNull(restaurant);
        assertEquals(restaurantName, restaurant.getName());
        table = new Table(0, restaurant.getId(), seats);
        restaurant.addTable(table);
    }

    @And("a reservation for {string}")
    public void aReservationFor(String dateTime) {
        LocalDateTime date = LocalDateTime.parse(dateTime);
        reservation = new Reservation(user, restaurant, table, date);
    }

    @And("add reservations:")
    public void addReservations(List<String> dateTimes) {
        reservations = dateTimes.stream()
                .map(dateTime -> new Reservation(user, restaurant, table, LocalDateTime.parse(dateTime)))
                .toList();
    }

    @When("the reservation is added to the user")
    public void theReservationIsAddedToTheUser() {
        user.addReservation(reservation);
        table.addReservation(reservation);
    }

    @When("the reservations are added to the user")
    public void theReservationsAreAddedToTheUser() {
        for (Reservation res : reservations) {
            user.addReservation(res);
            table.addReservation(res);
        }
    }

    @Then("the reservation list size should be {int}")
    public void theReservationListSizeShouldBe(int size) {
        assertEquals(size, user.getReservations().size());
    }

    @Then("the reservation number should be {int}")
    public void theReservationNumberShouldBe(int number) {
        assertEquals(number, reservation.getReservationNumber());
    }

    @Then("the reservation numbers should be sequential")
    public void theReservationNumbersShouldBeSequential() {
        int counter = 0;
        for (Reservation res : user.getReservations()) {
            assertEquals(counter++, res.getReservationNumber());
        }
    }

    @Then("the table should have {int} reservations")
    public void theTableShouldHaveReservations(int count) {
        assertEquals(count, table.getReservations().size());
    }

    @Then("the reservation's user should be {string}")
    public void theReservationUserShouldBe(String username) {
        assertEquals(username, reservation.getUser().getUsername());
    }

    @Then("the reservation should be in the correct table")
    public void theReservationShouldBeInTheCorrectTable() {
        assertTrue(table.getReservations().contains(reservation));
    }

    @Given("another review with food rating {double}, service rating {double}, ambiance rating {double}, and overall rating {double} by {string}")
    public void anotherReviewWithRatingsByUser(double food, double service, double ambiance, double overall, String username) {
        User user = new User(username, "password", "user@example.com", null, User.Role.client);
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        Review review = new Review(user, rating, "Another review", LocalDateTime.now());
        restaurant.addReview(review);
    }

    @When("the average rating is calculated")
    public void theAverageRatingIsCalculated() {
        averageRating = restaurant.getAverageRating();
    }

    @Then("the average food rating should be {double}")
    public void theAverageFoodRatingShouldBe(double expectedFoodRating) {
        assertEquals(expectedFoodRating, averageRating.food, 0);
    }

    @Then("the average service rating should be {double}")
    public void theAverageServiceRatingShouldBe(double expectedServiceRating) {
        assertEquals(expectedServiceRating, averageRating.service, 0);
    }

    @Then("the average ambiance rating should be {double}")
    public void theAverageAmbianceRatingShouldBe(double expectedAmbianceRating) {
        assertEquals(expectedAmbianceRating, averageRating.ambiance, 0);
    }

    @Then("the average overall rating should be {double}")
    public void theAverageOverallRatingShouldBe(double expectedOverallRating) {
        assertEquals(expectedOverallRating, averageRating.overall, 0);
    }

    @Given("a review with food rating {double}, service rating {double}, ambiance rating {double}, and overall rating {double} by {string}")
    public void aReviewWithRatingsByUser(double food, double service, double ambiance, double overall, String username) {
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        review = new Review(user, rating, "Great experience!", LocalDateTime.now());
    }

    @When("the review is added to the restaurant")
    public void theReviewIsAddedToTheRestaurant() {
        restaurant.addReview(review);
    }

    @When("a new review with food rating {double}, service rating {double}, ambiance rating {double}, and overall rating {double} by {string}")
    public void aNewReviewWithRatingsByUser(double food, double service, double ambiance, double overall, String username) {
        Rating newRating = new Rating();
        newRating.food = food;
        newRating.service = service;
        newRating.ambiance = ambiance;
        newRating.overall = overall;
        review = new Review(user, newRating, "Updated review!", LocalDateTime.now());
    }

    @Then("the restaurant should have {int} review")
    public void theRestaurantShouldHaveReview(int reviewCount) {
        assertEquals(reviewCount, restaurant.getReviews().size());
    }

    @Then("the updated review should have an overall rating of {double}")
    public void theUpdatedReviewShouldHaveAnOverallRatingOf(double overallRating) {
        Review latestReview = restaurant.getReviews().get(0);
        assertEquals(overallRating, latestReview.getRating().overall, 0.01);
    }

    @Then("the review should be written by {string}")
    public void theReviewShouldBeWrittenBy(String username) {
        assertEquals(username, review.getUser().getUsername());
    }
}