package dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class FailureResponseTemplate {
    public String errorMessage;
    
    @JsonInclude(Include.NON_NULL)
    public String systemError;
}
