package com.sia.salesapp.application.extendedServices;

import com.sia.salesapp.domain.entity.Order;
import com.sia.salesapp.domain.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderComputationService {

    public void computeTotals(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {

                BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(lineTotal);

                if (item.getVatRate() != null) {
                    BigDecimal vatAmount = lineTotal.multiply(item.getVatRate())
                            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                    taxTotal = taxTotal.add(vatAmount);
                }
            }
        }

        order.setSubtotal(subtotal);
        order.setTaxTotal(taxTotal);

        BigDecimal shippingFee = order.getShippingFee() == null ? BigDecimal.ZERO : order.getShippingFee();
        order.setShippingFee(shippingFee);

        order.setGrandTotal(subtotal.add(taxTotal).add(shippingFee));
    }
}