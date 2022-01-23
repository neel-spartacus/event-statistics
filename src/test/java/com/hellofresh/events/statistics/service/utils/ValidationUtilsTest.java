package com.hellofresh.events.statistics.service.utils;


import com.hellofresh.events.statistics.exceptions.ValidationException;
import com.hellofresh.events.statistics.handlers.ErrorMessage;
import com.hellofresh.events.statistics.utils.ValidationUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldThrowInvalidExceptionForInvalidEvent() {
        String event = "12123,0.12354,875415,841113";
        exceptionRule.expect(ValidationException.class);
        exceptionRule.expectMessage("Invalid event with incorrect parameters");
        ValidationUtils.validateEvent(event, new ErrorMessage());
    }

    @Test
    public void shouldThrowNumberFormatForInvalidEventTimeStamp() {
        String event = "test,0.12354,875415";
        exceptionRule.expect(NumberFormatException.class);
        exceptionRule.expectMessage("For input string: \"test\"");
        ValidationUtils.validateEvent(event, new ErrorMessage());
    }

    @Test
    public void shouldHaveValidXInEvent() {
        String event = "1607341341814,-1,1282509067";
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage = ValidationUtils.validateEvent(event, errorMessage);
        Assert.assertEquals("The value of x: -1.0 should be between 0 and 1", errorMessage.getErrorNotifications().get(0));

    }

    @Test
    public void shouldHaveValidYInEvent() {
        String event = "1607341271814,0.0586780608,111212767";
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage = ValidationUtils.validateEvent(event, errorMessage);
        Assert.assertEquals("The value of y: 111212767 should be between 1073741823 and 2147483647", errorMessage.getErrorNotifications().get(0));

    }


}
