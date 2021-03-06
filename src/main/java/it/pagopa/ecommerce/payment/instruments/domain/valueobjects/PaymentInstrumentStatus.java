package it.pagopa.ecommerce.payment.instruments.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.lang.NonNull;

import it.pagopa.ecommerce.payment.instruments.utils.PaymentInstrumentStatusEnum;
import lombok.EqualsAndHashCode;

@ValueObjects
@EqualsAndHashCode
public class PaymentInstrumentStatus implements Serializable {

    private final PaymentInstrumentStatusEnum status;

    public PaymentInstrumentStatus(@NonNull PaymentInstrumentStatusEnum status) {

        this.status = Objects.requireNonNull(status);
    }

    public @NonNull PaymentInstrumentStatusEnum value() {

        return status;
    }
}
