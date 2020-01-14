package com.miro.widgets.infrastructure.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestSpecification {

    public static Predicate<HttpServletRequest> widgetRequest() {
        return request -> request.getRequestURI().contains("widget");
    }

    public static Predicate<HttpServletRequest> dataManipulationMethod() {
        return request ->
                request.getMethod().equalsIgnoreCase("DELETE")
                        || request.getMethod().equalsIgnoreCase("PUT")
                        || request.getMethod().equalsIgnoreCase("POST");
    }

    public static Predicate<HttpServletRequest> httpGetMethod() {
        return request ->  request.getMethod().equalsIgnoreCase("GET");
    }

    @SuppressWarnings("unchecked")
    public static Predicate<HttpServletRequest> httpGetByIdMethod() {
        return request ->  ((Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).containsKey("widgetId");
    }
}
