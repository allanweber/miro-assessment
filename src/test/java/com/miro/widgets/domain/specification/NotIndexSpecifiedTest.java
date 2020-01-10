package com.miro.widgets.domain.specification;

import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotIndexSpecifiedTest {

    @Test
    public void Given_NullWidget_Then_ReturnFalse(){
        boolean result = NotIndexSpecified.satisfiedBy().test(null);
        assertFalse(result);
    }

    @Test
    public void Given_NullWidgetZIndex_Then_ReturnTrue(){
        boolean result = NotIndexSpecified.satisfiedBy().test(Widget.builder().build());
        assertTrue(result);
    }

    @Test
    public void Given_WidgetZIndexValue_Then_ReturnFalse(){
        boolean result = NotIndexSpecified.satisfiedBy().test(Widget.builder().zindex(0).build());
        assertFalse(result);
    }
}