package edu.itstep.library.job;

import edu.itstep.library.service.CounterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CounterSaverJob {
    private CounterService counterService;

    public CounterSaverJob(CounterService counterService) {
        this.counterService = counterService;
    }

    @Scheduled(fixedRate = 5000)
    public void run() {
        System.out.println("Saving counter");
        counterService.save();
    }
}
