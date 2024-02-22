package jabbyml.essentials.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class RequestTest {

    private Request unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = new Request(true);
    }

    @Test
    void construct_whenWithTrue_thenStoreRawResponseShouldBeTrue() {

        assertTrue(unitUnderTest.isStoreRawResponse());
    }

    @Test
    void construct_whenWithFalse_thenStoreRawResponseShouldBeFalse() {
        Request request = new Request(false);

        assertFalse(request.isStoreRawResponse());
    }

    @Test
    void getQueryParameters_whenConstructed_thenReturnEmpty() {

        assertEquals(Map.of(), unitUnderTest.getQueryParameters());
    }

    @Test
    void getRestParameters_whenConstructed_thenReturnEmpty() {

        assertEquals(Map.of(), unitUnderTest.getRestParameters());
    }

}