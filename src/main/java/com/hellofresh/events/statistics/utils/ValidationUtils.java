package com.hellofresh.events.statistics.utils;

import com.hellofresh.events.statistics.exceptions.ValidationException;
import com.hellofresh.events.statistics.handlers.ErrorMessage;

import java.time.DateTimeException;
import java.time.Instant;

import static com.hellofresh.events.statistics.constants.Constants.MAX_VALUE_X;
import static com.hellofresh.events.statistics.constants.Constants.MIN_VALUE_X;

public class ValidationUtils {

    public static ErrorMessage validateEvent(String event, ErrorMessage errorMessage) throws ValidationException, DateTimeException {

        String[] eventParameters = event.split(",");
        if (eventParameters.length != 3) {
            throw new ValidationException("Invalid event with incorrect parameters");
        }
        String firstEventParam = eventParameters[0];
        String secondEventParam = eventParameters[1];
        String thirdEventParam = eventParameters[2];

        if (firstEventParam == null || firstEventParam.equals("") || secondEventParam == null || secondEventParam.equals("")
                || thirdEventParam == null || thirdEventParam.equals("")) {
            errorMessage.addErrorNotifications("Invalid parameters in the event");
        }
        Instant.ofEpochMilli(Long.parseLong(firstEventParam));

        Double valueX = Double.parseDouble(secondEventParam);

        if (valueX < 0 || valueX > 1) {
            errorMessage.addErrorNotifications("The value of x: " + valueX + " should be between 0 and 1");
        }
        Integer valueY = Integer.parseInt(thirdEventParam);

        if (valueY < MIN_VALUE_X || valueY > MAX_VALUE_X) {
            //if(valueY> MAX_VALUE_X){
            errorMessage.addErrorNotifications("The value of y: " + valueY + " should be between 1073741823 and 2147483647");
        }

        return errorMessage;
    }


}
