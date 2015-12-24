package net.jmob.guice.conf.core.samples.complex.service;

import org.hibernate.validator.constraints.Length;

import java.time.Duration;
import java.util.Optional;

public interface SubType {

    Optional<Integer> getIntValue();

    @Length(max = 10)
    String getValue();

    Duration getDuration();
}
