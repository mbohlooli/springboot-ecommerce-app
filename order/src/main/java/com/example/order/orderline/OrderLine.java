package com.example.order.orderline;

import com.example.order.order.Order;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OrderLine {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer productId;
    private double quantity;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
}
