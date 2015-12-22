package net.jmob.guice.conf.core.samples.complex.service;

import org.hibernate.validator.constraints.Length;

public interface SubType {

    int getIntValue();

    @Length(max = 10)
    String getValue();
}
