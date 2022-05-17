package it.pagopa.ecommerce.payment.instruments.application;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.pagopa.ecommerce.payment.instruments.domain.aggregates.PaymentInstrument;
import it.pagopa.ecommerce.payment.instruments.domain.aggregates.PaymentInstrumentFactory;
import it.pagopa.ecommerce.payment.instruments.domain.valueobjects.PaymentInstrumentDescription;
import it.pagopa.ecommerce.payment.instruments.domain.valueobjects.PaymentInstrumentEnabled;
import it.pagopa.ecommerce.payment.instruments.domain.valueobjects.PaymentInstrumentID;
import it.pagopa.ecommerce.payment.instruments.domain.valueobjects.PaymentInstrumentName;
import it.pagopa.ecommerce.payment.instruments.infrastructure.PaymentInstrumentDocument;
import it.pagopa.ecommerce.payment.instruments.infrastructure.PaymentInstrumentRepository;
import it.pagopa.ecommerce.payment.instruments.utils.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@ApplicationService
@Slf4j
public class PaymentInstrumentService {

    @Autowired
    private PaymentInstrumentRepository paymentInstrumentRepository;

    @Autowired
    private PaymentInstrumentFactory paymentInstrumentFactory;

    public Mono<PaymentInstrument> createPaymentInstrument(String paymentInstrumentName,
            String paymentInstrumentDescription) {

        log.debug("[Payment instrument Aggregate] Create new Aggregate");
        Mono<PaymentInstrument> paymentInstrument = paymentInstrumentFactory.newPaymentInstrument(
                new PaymentInstrumentID(UUID.randomUUID()),
                new PaymentInstrumentName(paymentInstrumentName),
                new PaymentInstrumentDescription(paymentInstrumentDescription),
                new PaymentInstrumentEnabled(Boolean.TRUE));

        // TODO create converter aggregate - document
        log.debug("[Payment instrument Aggregate] Store Aggregate");
        return paymentInstrument.flatMap(
                p -> paymentInstrumentRepository
                        .save(new PaymentInstrumentDocument(
                                p.getPaymentInstrumentID().value().toString(),
                                p.getPaymentInstrumentName().value(),
                                p.getPaymentInstrumentDescription().value(),
                                p.getPsp(),
                                p.getPaymentInstrumentEnabled().value())))
                .map(document -> new PaymentInstrument(
                        new PaymentInstrumentID(
                                UUID.fromString(document.getPaymentInstrumentID())),
                        new PaymentInstrumentName(document.getPaymentInstrumentName()),
                        new PaymentInstrumentDescription(
                                document.getPaymentInstrumentDescription()),
                        new PaymentInstrumentEnabled(document.getPaymentInstrumentEnabled())));
    }

    public Flux<PaymentInstrument> retrivePaymentInstruments() {

        // TODO create converter aggregate - document
        log.debug("[Payment instrument Aggregate] Retrive Aggregate");
        return paymentInstrumentRepository
                .findAll()
                .map(document -> new PaymentInstrument(
                        new PaymentInstrumentID(
                                UUID.fromString(document.getPaymentInstrumentID())),
                        new PaymentInstrumentName(document.getPaymentInstrumentName()),
                        new PaymentInstrumentDescription(
                                document.getPaymentInstrumentDescription()),
                        new PaymentInstrumentEnabled(document.getPaymentInstrumentEnabled())));
    }

    public Mono<PaymentInstrument> patchPaymentInstrument(String id,
            Boolean enable) {

        log.debug("[Payment instrument Aggregate] Patch Aggregate");

        return paymentInstrumentRepository
                .findById(id)
                .map(document -> {
                    PaymentInstrument paymentInstrument = new PaymentInstrument(
                            new PaymentInstrumentID(
                                    UUID.fromString(document
                                            .getPaymentInstrumentID())),
                            new PaymentInstrumentName(document.getPaymentInstrumentName()),
                            new PaymentInstrumentDescription(
                                    document.getPaymentInstrumentDescription()),
                            new PaymentInstrumentEnabled(
                                    document.getPaymentInstrumentEnabled()));
                    paymentInstrument.enablePaymentInstrument(new PaymentInstrumentEnabled(enable));
                    return paymentInstrument;
                });
    }

    public Mono<PaymentInstrument> retrivePaymentInstrumentById(String id) {

        log.debug("[Payment instrument Aggregate] Retrive Aggregate");

        return paymentInstrumentRepository
                .findById(id)
                .map(document -> new PaymentInstrument(
                        new PaymentInstrumentID(
                                UUID.fromString(document.getPaymentInstrumentID())),
                        new PaymentInstrumentName(document.getPaymentInstrumentName()),
                        new PaymentInstrumentDescription(
                                document.getPaymentInstrumentDescription()),
                        new PaymentInstrumentEnabled(document.getPaymentInstrumentEnabled())));
    }
}