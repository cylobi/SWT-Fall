package mizdooni;
import com.fasterxml.jackson.databind.ObjectMapper;
import mizdooni.exceptions.InvalidWorkingTime;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.response.PagedList;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    void getRestaurant_shouldReturnRestaurant() throws Exception {
        Restaurant mockRestaurant = new Restaurant("Test Restaurant", null, "Type", null, null, "Description", null, "image");
        Mockito.when(restaurantService.getRestaurant(anyInt())).thenReturn(mockRestaurant);

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Restaurant"));
    }

    @Test
    void getRestaurant_invalidId_shouldReturnError() throws Exception {
        Mockito.when(restaurantService.getRestaurant(anyInt()))
                .thenThrow(new ResponseException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        mockMvc.perform(get("/restaurants/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Restaurant not found"));
    }

    @Test
    void getRestaurants_shouldReturnPagedRestaurants() throws Exception {
        List<Restaurant> mockRestaurants = List.of(
                new Restaurant("Restaurant 1", null, "Type", null, null, "Description", null, "image"),
                new Restaurant("Restaurant 2", null, "Type", null, null, "Description", null, "image")
        );

        PagedList<Restaurant> mockPagedList = new PagedList<>(mockRestaurants, 1, 2);

        Mockito.when(restaurantService.getRestaurants(Mockito.anyInt(), Mockito.any())).thenReturn(mockPagedList);

        mockMvc.perform(get("/restaurants?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageList").isArray())
                .andExpect(jsonPath("$.data.pageList.length()").value(2))
                .andExpect(jsonPath("$.data.pageList[0].name").value("Restaurant 1"));
    }

    @Test
    void validateRestaurantName_shouldReturnAvailability() throws Exception {
        Mockito.when(restaurantService.restaurantExists(anyString())).thenReturn(false);

        mockMvc.perform(get("/validate/restaurant-name?data=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    void validateRestaurantName_conflict_shouldReturnError() throws Exception {
        Mockito.when(restaurantService.restaurantExists(anyString())).thenReturn(true);

        mockMvc.perform(get("/validate/restaurant-name?data=TakenName"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    void addRestaurant_shouldAddAndReturnId() throws Exception {
        Mockito.when(restaurantService.addRestaurant(anyString(), anyString(), Mockito.any(), Mockito.any(), anyString(), Mockito.any(), anyString()))
                .thenReturn(1);

        String restaurantJson = """
                {
                    "name": "New Restaurant",
                    "type": "Type",
                    "startTime": "10:00",
                    "endTime": "22:00",
                    "description": "Great food",
                    "address": {
                        "country": "Country",
                        "city": "City",
                        "street": "Street"
                    },
                    "image": "imageLink"
                }
                """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(restaurantJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void addRestaurant_invalidParams_shouldReturnError() throws Exception {
        String invalidRestaurantJson = """
                {
                    "name": "New Restaurant",
                    "type": "Type"
                    // Missing required fields
                }
                """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRestaurantJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRestaurantTypes_shouldReturnTypes() throws Exception {
        Mockito.when(restaurantService.getRestaurantTypes()).thenReturn(Set.of("Italian", "Chinese"));

        mockMvc.perform(get("/restaurants/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getRestaurantLocations_shouldReturnLocations() throws Exception {
        Map<String, Set<String>> locations = Map.of("Country1", Set.of("City1", "City2"));
        Mockito.when(restaurantService.getRestaurantLocations()).thenReturn(locations);

        mockMvc.perform(get("/restaurants/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.Country1").isArray());
    }

    // Edge Cases
    @Test
    void getManagerRestaurants_invalidManagerId_shouldReturnError() throws Exception {
        Mockito.when(restaurantService.getManagerRestaurants(Mockito.anyInt()))
                .thenThrow(new ResponseException(HttpStatus.NOT_FOUND, "Manager not found"));

        mockMvc.perform(get("/restaurants/manager/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Manager not found"));
    }

    @Test
    void getManagerRestaurants_noRestaurants_shouldReturnEmptyList() throws Exception {
        Mockito.when(restaurantService.getManagerRestaurants(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/restaurants/manager/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void getRestaurants_missingPageParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRestaurants_invalidFilter_shouldReturnError() throws Exception {
        Mockito.when(restaurantService.getRestaurants(anyInt(), Mockito.any()))
                .thenThrow(new ResponseException(HttpStatus.BAD_REQUEST, "Invalid filter"));

        mockMvc.perform(get("/restaurants?page=1&filter=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid filter"));
    }
    @Test
    void addRestaurant_overlappingTimeRange_shouldReturnError() throws Exception {
        Mockito.when(restaurantService.addRestaurant(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenThrow(new InvalidWorkingTime());

        String restaurantJson = """
            {
                "name": "Conflicting Restaurant",
                "type": "Type",
                "startTime": "10:00",
                "endTime": "08:00",
                "description": "Great food",
                "address": {
                    "country": "Country",
                    "city": "City",
                    "street": "Street"
                },
                "image": "imageLink"
            }
        """;

        mockMvc.perform(post("/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(restaurantJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid working time."));
    }
}
