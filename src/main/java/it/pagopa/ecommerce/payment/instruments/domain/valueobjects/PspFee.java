package it.pagopa.ecommerce.payment.instruments.domain.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.lang.NonNull;

import lombok.EqualsAndHashCode;

@ValueObjects
@EqualsAndHashCode
public class PspFee implements Serializable {

    private final Double fee;

    public PspFee(@NonNull Double fee) {

        this.fee = Objects.requireNonNull(fee);
    }

    public @NonNull Double value() {
        return fee;
    }
}
