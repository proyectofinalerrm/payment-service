package pe.com.bank.payment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.bank.payment.client.AccountRestClient;
import pe.com.bank.payment.client.CreditRestClient;
import pe.com.bank.payment.client.TransactionRestClient;
import pe.com.bank.payment.entity.PaymentEntity;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class PaymentService {


    CreditRestClient creditRestClient;
    AccountRestClient accountRestClient;
    TransactionRestClient transactionRestClient;

    public Mono<PaymentEntity> createPayment(PaymentEntity paymentEntity) {
        return Mono.empty();
    }

}