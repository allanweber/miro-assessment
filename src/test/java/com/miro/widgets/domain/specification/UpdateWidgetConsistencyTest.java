package com.miro.widgets.domain.specification;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateWidgetConsistencyTest {

    @Test
    public void Given_NullWidgets_ThenReturnFalse(){
        boolean result = UpdateWidgetConsistency.satisfiedBy().test(null, null);
        assertFalse(result);
    }

    @Test
    public void Given_NullWidgetAlreadySaved_ThenReturnFalse(){

        Widget newRecord = Widget.builder().coordinate(Coordinate.builder().build()).zindex(1).build();

        boolean result = UpdateWidgetConsistency.satisfiedBy().test(null, newRecord);
        assertFalse(result);
    }

    @Test
    public void Given_NullWidgetNewRecord_ThenReturnFalse(){

        Widget alreadySaved = Widget.builder().coordinate(Coordinate.builder().build()).zindex(1).build();

        boolean result = UpdateWidgetConsistency.satisfiedBy().test(alreadySaved, null);
        assertFalse(result);
    }

    @Test
    public void Given_NullIndexSavedAndNewRecordWithValue_ThenReturnFalse(){

        Widget alreadySaved = Widget.builder().coordinate(Coordinate.builder().build()).build();

        Widget newRecord = Widget.builder().coordinate(Coordinate.builder().build()).zindex(1).build();

        boolean result = UpdateWidgetConsistency.satisfiedBy().test(alreadySaved, newRecord);
        assertFalse(result);
    }

    @Test
    public void Given_ExistentZIndexAndNullNewValue_ThenReturnTrue(){

        Widget alreadySaved = Widget.builder().coordinate(Coordinate.builder().build()).zindex(1).build();

        Widget newRecord = Widget.builder().coordinate(Coordinate.builder().build()).build();

        boolean result = UpdateWidgetConsistency.satisfiedBy().test(alreadySaved, newRecord);
        assertTrue(result);
    }
}