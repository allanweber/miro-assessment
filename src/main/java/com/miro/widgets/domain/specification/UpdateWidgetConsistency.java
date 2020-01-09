package com.miro.widgets.domain.specification;

import com.miro.widgets.domain.entity.Widget;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.BiPredicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateWidgetConsistency {

    public static BiPredicate<Widget, Widget> satisfiedBy() {
        return (alreadySaved, newRecord) -> Objects.nonNull(alreadySaved.getZindex()) && Objects.isNull(newRecord.getZindex());
    }
}
