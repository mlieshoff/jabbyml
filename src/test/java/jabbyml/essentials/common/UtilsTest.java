package jabbyml.essentials.common;

import static jabbyml.essentials.common.Utils.isBlank;
import static jabbyml.essentials.common.Utils.isNotBlank;
import static jabbyml.essentials.common.Utils.isNotEmpty;
import static jabbyml.essentials.common.Utils.require;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Map;

class UtilsTest {

  @Test
  void isNotEmptyMap_withNonEmptyMap() {
    Map<String, String> map = Map.of("key", "value");

    assertTrue(isNotEmpty(map));
  }

  @Test
  void isNotEmptyMap_withEmptyMap() {

    assertFalse(isNotEmpty(Map.of()));
  }

  @Test
  void isNotEmptyMap_withNullMap() {
    Map<String, String> map = null;

    assertFalse(isNotEmpty(map));
  }

  @Test
  void isNotEmptyArray_withNonEmptyArray() {
    Object[] array = new Object[] {"element"};

    assertTrue(isNotEmpty(array));
  }

  @Test
  void isNotEmptyArray_withEmptyArray() {
    Object[] array = new Object[] {};

    assertFalse(isNotEmpty(array));
  }

  @Test
  void isNotEmptyArray_withNullArray() {
    Object[] array = null;

    assertFalse(isNotEmpty(array));
  }

  @Test
  void isBlank_withBlankString() {

    assertTrue(isBlank(""));
  }

  @Test
  void isBlank_withNullString() {

    assertTrue(isBlank(null));
  }

  @Test
  void isBlank_withNonBlankString() {

    assertFalse(isBlank("not blank"));
  }

  @Test
  void isNotBlank_withNonBlankString() {

    assertTrue(isNotBlank("not blank"));
  }

  @Test
  void isNotBlank_withBlankString() {

    assertFalse(isNotBlank(""));
  }

  @Test
  void isNotBlank_withNullString() {

    assertFalse(isNotBlank(null));
  }

  @Test
  void require_withNonBlankString() {

    assertDoesNotThrow(() -> require("KEY", "not blank"));
  }

  @Test
  void require_withBlankString() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> require("KEY", ""));

    assertTrue(exception.getMessage().contains("KEY must be set! But was: "));
  }

  @Test
  void require_withNullString() {
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> require("KEY", (String) null));

    assertTrue(exception.getMessage().contains("KEY must be set! But was: null"));
  }

  @Test
  void require_withNonNullObject() {

    assertDoesNotThrow(() -> require("KEY", new Object()));
  }

  @Test
  void require_withNullObject() {
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> require("KEY", (Object) null));

    assertTrue(exception.getMessage().contains("KEY must be set!"));
  }
}
