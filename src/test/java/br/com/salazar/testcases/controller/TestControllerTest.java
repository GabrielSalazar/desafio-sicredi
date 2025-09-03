package br.com.salazar.testcases.controller;
import br.com.salazar.Application;
import br.com.salazar.controller.TestController;
import br.com.salazar.model.dto.TestResponseDto;
import br.com.salazar.service.TestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@ContextConfiguration(classes = Application.class)
public class TestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private TestService testService;

    @Test
    @DisplayName("GET /test deve retornar 200 e o payload esperado")
    public void shouldReturnOkWithExpectedBody() throws Exception {
        when(testService.getTestStatus())
                .thenReturn(new TestResponseDto("ok", "GET"));

        mvc.perform(get("/test").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.method").value("GET"));
    }
}