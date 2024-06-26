package utez.edu.mx.IntegradoraPodec.Model.Order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import utez.edu.mx.IntegradoraPodec.Model.Customers.CustomersBean;
import utez.edu.mx.IntegradoraPodec.Model.Employees.EmployeesBean;
import utez.edu.mx.IntegradoraPodec.Model.Order_Item.OrderItemBean;
import utez.edu.mx.IntegradoraPodec.Model.Price_Kg.PriceKgBean;
import utez.edu.mx.IntegradoraPodec.Model.Rols.RolsBean;
import utez.edu.mx.IntegradoraPodec.Model.Status.StatusBean;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private LocalDate dateRequest;
    @Column()
    private LocalDate dateSending;
    @Column()
    private String description;
    @Column()
    private LocalDate dateDelivered;
    @Column(nullable = false)
    private String latitude;
    @Column(nullable = false)
    private String longitude;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employees_fk", nullable = true)
    private EmployeesBean employeesBean;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_fk", nullable = true)
    private CustomersBean customersBean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_fk")
    private StatusBean statusBean;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_fk")
    private PriceKgBean priceKgBean;

    @OneToMany(mappedBy = "orderBean",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<OrderItemBean> orderItemBeans;


    public OrderBean(LocalDate dateRequest, LocalDate datesending, String description, LocalDate dateDelivered) {
        this.dateRequest = dateRequest;
        this.dateSending = datesending;
        this.description = description;
        this.dateDelivered = dateDelivered;
    }

    public OrderBean(Long id, LocalDate dateRequest, LocalDate datesending, String description, LocalDate dateDelivered) {
        this.id = id;
        this.dateRequest = dateRequest;
        this.dateSending = datesending;
        this.description = description;
        this.dateDelivered = dateDelivered;
    }
}
