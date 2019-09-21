package handler;

import handler.resource.StaticResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticResourceTest {

    @Test
    void setLocationTest() {
        // given
        StaticResource resource = StaticResource.of("css");
        String location = "/test";

        // when
        resource.changeLocation(location);

        // then
        assertThat(resource.getLocation()).isEqualTo(location);
    }

    @Test
    void equalsTest() {
        // given
        StaticResource resource1 = StaticResource.of("css");
        StaticResource resource2 = StaticResource.of("css");

        // when & then
        assertThat(resource1).isEqualTo(resource2);
    }
}
