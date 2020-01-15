package com.miro.widgets.api;

import com.miro.widgets.domain.constants.ConstantsUtils;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "Api Limit")
@RequestMapping("/limit")
public interface LimitControllerApi {

    @ApiOperation(notes = "Update request limits for one API", value = "Update Limit")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Limit updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @PutMapping("/{limitKey}/{max}/{seconds:[0-9]+}")
    ResponseEntity<?> updateLimit(@ApiParam(name = "limitKey", value = "RequestLimit enum format", required = true) @PathVariable RequestLimit limitKey,
                                  @ApiParam(name = "max", value = "Number of max requests", required = true) @PathVariable Integer max,
                                  @ApiParam(name = "seconds", value = "Duration in seconds", required = true) @PathVariable Long seconds);
}
