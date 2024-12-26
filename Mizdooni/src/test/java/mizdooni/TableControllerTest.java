package mizdooni;

import mizdooni.controllers.ControllerUtils;
import mizdooni.exceptions.InvalidManagerRestaurant;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private TableService tableService;

    private int restaurantId;

    private static final String PARAMS_MISSING = "parameters missing";
    private static final String PARAMS_BAD_TYPE = "bad parameter type";

    @BeforeEach
    void setup() {

        restaurantId = 1;
        Mockito.when(restaurantService.getRestaurant(restaurantId)).thenReturn(new Restaurant(
                "Test Restaurant",
                new User("test", "test", "test@test.com", new Address("a", "b", "c"), User.Role.manager),
                "Test Type",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                "A test restaurant",
                new Address("a", "b", "c"),
                "imageLink"
        ));
    }

    @Test
    void getTables_shouldReturnTables() throws Exception {
        List<Table> mockTables = List.of(
                new Table(1, restaurantId, 4),
                new Table(2, restaurantId, 6)
        );

        Mockito.when(tableService.getTables(restaurantId)).thenReturn(mockTables);

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data[0].tableNumber").value(1))
                .andExpect(jsonPath("$.data[0].seatsNumber").value(4))
                .andExpect(jsonPath("$.data[1].tableNumber").value(2))
                .andExpect(jsonPath("$.data[1].seatsNumber").value(6));
    }

    @Test
    void getTables_invalidRestaurantId_shouldReturnError() throws Exception {
        int invalidRestaurantId = 999;

        Mockito.when(tableService.getTables(invalidRestaurantId))
                .thenThrow(new RestaurantNotFound());

        mockMvc.perform(get("/tables/{restaurantId}", invalidRestaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    void addTable_shouldAddTable() throws Exception {
        String requestJson = """
            {
                "seatsNumber": "4"
            }
        """;

        Mockito.doNothing().when(tableService).addTable(Mockito.eq(restaurantId), Mockito.anyInt());

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    void addTable_missingSeatNumber_shouldReturnError() throws Exception {
        String requestJson = "{}";

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(PARAMS_MISSING));
    }
    @Test
    void addTable_invalidSeatNumberType_shouldReturnError() throws Exception {
        String requestJson = """
            {
                "seatsNumber": "invalid"
            }
        """;

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(PARAMS_BAD_TYPE));
    }

    @Test
    void addTable_invalidManager_shouldReturnError() throws Exception {
        String requestJson = """
            {
                "seatsNumber": "4"
            }
        """;

        Mockito.doThrow(new InvalidManagerRestaurant()).when(tableService).addTable(Mockito.eq(restaurantId), Mockito.anyInt());

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("The manager is not valid for this restaurant."));
    }

    @Test
    void getTables_emptyTables_shouldReturnEmptyList() throws Exception {
        Mockito.when(tableService.getTables(restaurantId)).thenReturn(List.of());

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addTable_noSuchRestaurant_shouldReturnError() throws Exception {
        int invalidRestaurantId = 999;
        String requestJson = """
            {
                "seatsNumber": "4"
            }
        """;

        Mockito.doThrow(new RestaurantNotFound()).when(tableService).addTable(Mockito.eq(invalidRestaurantId), Mockito.anyInt());

        mockMvc.perform(post("/tables/{restaurantId}", invalidRestaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }

    @Test
    void addTable_negativeSeatNumber_shouldReturnError() throws Exception {
        String requestJson = """
            {
                "seatsNumber": "-4"
            }
        """;

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(PARAMS_BAD_TYPE)); // Assuming validation checks handle this
    }
}
