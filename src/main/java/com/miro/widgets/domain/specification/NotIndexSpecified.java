package com.miro.widgets.domain.specification;

import com.miro.widgets.domain.entity.Widget;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotIndexSpecified {

    public static Predicate<Widget> satisfiedBy(){
        return widget -> Objects.nonNull(widget) && Objects.isNull(widget.getZindex());
    }
}
