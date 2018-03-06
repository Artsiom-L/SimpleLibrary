package edu.itstep.library.service.impl;

import edu.itstep.library.service.CounterService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterServiceImpl implements CounterService {
    private JdbcTemplate jdbcTemplate;
    private AtomicLong indexPageCounter;
    private AtomicBoolean valueChanged = new AtomicBoolean(false);

    public CounterServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostConstruct
    public void setUp() {
        //1 check if counter exists
        Long value = jdbcTemplate.query("SELECT value FROM counters WHERE name = 'index'", resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return null;
        });
        if (value == null) {
            //not exists
            jdbcTemplate.execute("INSERT INTO counters(name) VALUES('index')");
            value = 0L;
        }
        indexPageCounter = new AtomicLong(value);
    }

    @Override
    public long incrementAndGet() {
        valueChanged.set(true);
        return indexPageCounter.incrementAndGet();
    }

    @Override
    public void save() {
        if (valueChanged.get()) {
            System.out.println("Saving to DB");
            jdbcTemplate.update("UPDATE counters SET value = ? WHERE name = 'index'", indexPageCounter.get());
            valueChanged.set(false);
        }
    }
}
