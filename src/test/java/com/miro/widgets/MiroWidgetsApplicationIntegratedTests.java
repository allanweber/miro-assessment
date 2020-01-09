package com.miro.widgets;

import com.miro.widgets.infrastructure.configuration.swagger.SwaggerFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class MiroWidgetsApplicationIntegratedTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(new SwaggerFilter())
                .build();
    }

    @Test
    public void When_SwaggerURiIsRetrieved_Then_SwaggerResourceFound() throws Exception {
        MockHttpServletResponse actualResponseContent = mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertTrue(actualResponseContent.getContentAsString().contains("Swagger UI"));
    }

    @Test
    public void When_RootPathIsAccessed_Then_RedirectToSwaggerResourceFound() throws Exception {
        MockHttpServletResponse actualResponseContent = mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse();
    }

}
