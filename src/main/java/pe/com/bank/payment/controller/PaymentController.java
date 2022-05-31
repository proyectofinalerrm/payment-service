package pe.com.bank.payment.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.com.bank.payment.client.AccountRestClient;
import pe.com.bank.payment.client.CreditRestClient;
import pe.com.bank.payment.client.TransactionRestClient;
import pe.com.bank.payment.entity.*;
import pe.com.bank.payment.service.PaymentService;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/v1")
public class PaymentController {

    PaymentService paymentService;
    CreditRestClient creditRestClient;
    AccountRestClient accountRestClient;
    TransactionRestClient transactionRestClient;

    @PostMapping("/payment")
    public Mono<PaymentEntity> createPayment(@RequestBody PaymentEntity paymentEntity) {
        return creditRestClient.retrieveCreditA(paymentEntity.getCreditId())
                .flatMap(crc -> {
                    var r = creditRestClient.updateCreditA(new CreditDTO(crc.getCreditId(), crc.getAmountUsed() - paymentEntity.getAmount(), crc.getLimitCredit(), crc.getCreditAvailable() + paymentEntity.getAmount(), crc.getNumberCredit(), crc.getType(), crc.getProductId()), paymentEntity.getCreditId());
                    return r.flatMap(re -> {
                        var r2 = accountRestClient.retrieveAccountA(paymentEntity.getAccountId());
                        return r2.flatMap(sd -> {
                            var r3 = accountRestClient.updateAccountA(new AccountDTO(sd.getId(), sd.getAccountNumber(), sd.getAmount() - paymentEntity.getAmount(), sd.getDateOpen(), sd.getAmounttype(), sd.getProductId(), sd.getCustomerId()), paymentEntity.getAccountId());
                            return r3.flatMap(sf -> {
                                var sdfsd = transactionRestClient.createTransactionA(new TransactionDTO(paymentEntity.getAccountId(), paymentEntity.getAmount(), paymentEntity.getDate(), paymentEntity.getType(), paymentEntity.getCreditId()));
                                return sdfsd.flatMap(dsf -> {
                                    return Mono.empty();
                                });
                            });
                        });
                    });
                });
    }

    @PostMapping("/payment/charge")
    public Mono<PaymentChargeDTO> createPaymentCharge(@RequestBody PaymentChargeDTO paymentChargeDTO) {
        return creditRestClient.retrieveCreditA(paymentChargeDTO.getCreditId())
                .flatMap(crc -> {
                    return (crc.getLimitCredit() >= paymentChargeDTO.getAmount()) ?
                            creditRestClient.updateCreditA(new CreditDTO(crc.getCreditId(), crc.getAmountUsed() + paymentChargeDTO.getAmount(), crc.getLimitCredit(), crc.getCreditAvailable(), crc.getNumberCredit(), crc.getType(), crc.getProductId()), paymentChargeDTO.getCreditId())
                                    .flatMap(urc -> {
                                        var traResCli = transactionRestClient.createTransactionA(new TransactionDTO("", paymentChargeDTO.getAmount(), paymentChargeDTO.getDate(), paymentChargeDTO.getType(), paymentChargeDTO.getCreditId()));
                                        return traResCli.flatMap(dsf -> {
                                            return Mono.empty();
                                        });
                                    })
                            : Mono.empty();
                });
    }
}