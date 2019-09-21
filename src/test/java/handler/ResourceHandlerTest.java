package handler;

import handler.resource.ResourceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceHandlerTest {
    private ResourceHandler resourceHandler;

    @BeforeEach
    void setUp() {
        resourceHandler = ResourceHandler.getInstance();
    }

    @Test
    void setPrefix() {
        final String location = "/test";
        resourceHandler.setLocation(location);

        assertThat(resourceHandler.getLocation()).isEqualTo(location);
    }
}