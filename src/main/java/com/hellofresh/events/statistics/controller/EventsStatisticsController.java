package com.hellofresh.events.statistics.controller;

import com.hellofresh.events.statistics.constants.Constants;
import com.hellofresh.events.statistics.dto.EventsInfoDto;
import com.hellofresh.events.statistics.exceptions.ValidationException;
import com.hellofresh.events.statistics.handlers.ErrorMessage;
import com.hellofresh.events.statistics.service.EventsStatisticsService;
import com.hellofresh.events.statistics.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping()
@Slf4j
public class EventsStatisticsController {

    @Autowired
    private EventsStatisticsService eventsStatisticsService;

    @Autowired
    @Qualifier("eventsPostExecutorService")
    private ExecutorService eventsPostExecutorService;

    @Autowired
    @Qualifier("eventsStatisticsThreadExecutorService")
    private ExecutorService eventsStatisticsThreadExecutorService;

    /**
     *
     * Process an event.
     * The API is non-blocking The processes of the request is delegated to internal thread mechanism
     * using ExecutorService. This POST API has its own dedicated threading mechanism.
     * @param event
     * @throws ValidationException
     *
     */

    @RequestMapping(path = "/event", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<?>> postTransactions(@Valid @RequestBody String event ) throws ValidationException {

        ErrorMessage errorMessage= new ErrorMessage();
        errorMessage=ValidationUtils.validateEvent(event,errorMessage);

        if(errorMessage.getErrorNotifications().size()>0){
            throw new ValidationException(String.join(".", errorMessage.getErrorNotifications()));
        }

        EventsInfoDto eventsInfoDto=eventsStatisticsService.createDtoFromEvent(event);
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(() -> eventsStatisticsService.processEvents(eventsInfoDto), eventsPostExecutorService)
                .whenComplete((p, throwable) ->
                        {
                            log.debug("Current Thread Name :{}", Thread.currentThread().getName());
                            deferredResult.setResult(ResponseEntity.status(202).build());
                        }


                );

        return deferredResult;
    }

    /**
     * Returns the statistics of all the events that occured in the last 60 seconds.
     * The API is non-blocking The processes of the request is delegated to internal thread mechanism
     * using ExecutorService. This GET API has its own dedicated threading mechanism.
     * @return
     */
    @RequestMapping(path = "/stats", method = RequestMethod.GET)
    public @ResponseBody DeferredResult<ResponseEntity<?>> getStatistics() {
        ZonedDateTime startTime = ZonedDateTime.now();


        log.debug("Servlet Thread Started for statistics API Started -{}",startTime);


        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();

        CompletableFuture
                .supplyAsync(() -> eventsStatisticsService.getEventStatistics(ZonedDateTime.now(ZoneId.of("UTC"))), eventsStatisticsThreadExecutorService)
                .whenComplete((result, throwable) ->
                        {
                            log.info("Current Thread Name :{}", Thread.currentThread().getName());
                            if(!result.equals(Constants.EMPTY_STRING)){
                                deferredResult.setResult(ResponseEntity.status(HttpStatus.OK).body(result));
                            }
                            else{
                                deferredResult.setResult(ResponseEntity.status(HttpStatus.NO_CONTENT).body("No events details available"));
                            }

                        }


                );

        return deferredResult;

    }
}

