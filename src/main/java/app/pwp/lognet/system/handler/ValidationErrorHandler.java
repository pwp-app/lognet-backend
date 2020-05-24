package app.pwp.lognet.system.handler;

import app.pwp.lognet.utils.common.R;
import com.alibaba.fastjson.JSON;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class ValidationErrorHandler {
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public R validationErrorHandler(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        HashMap<String, String> message = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            message.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return R.badRequest(JSON.toJSONString(message));
    }
}