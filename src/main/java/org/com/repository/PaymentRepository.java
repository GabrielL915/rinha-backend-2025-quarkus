package org.com.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.domain.model.Payment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository {

    @Inject
    DataSource dataSource;


    public void insertPayment(Payment payment) {
        String sql = """
                INSERT INTO payments(id, correlation_id, amount, request_at, is_default)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setObject(1, payment.getId());
            preparedStatement.setObject(2, payment.getCorrelationId());
            preparedStatement.setBigDecimal(3, payment.getAmount());
            preparedStatement.setTimestamp(4, Timestamp.from(payment.getRequestAt()));
            preparedStatement.setBoolean(5, payment.isDefault());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error in save Payment");
        }
    }


    public List<Payment> findByPeriod(Instant from, Instant to) {
        String sql = """
                SELECT id, correlation_id, amount, requested_at, is_default
                    FROM payments
                   WHERE request_at >= ? AND request_at <= ?
                   ORDER BY requested_at
                """;

        List<Payment> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.from(from));
            preparedStatement.setTimestamp(2, Timestamp.from(to));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setId(resultSet.getObject("id", UUID.class));
                    payment.setCorrelationId(resultSet.getObject("correlation_id", UUID.class));
                    payment.setAmount(resultSet.getBigDecimal("amount"));
                    payment.setRequestAt(resultSet.getTimestamp("request_at").toInstant());
                    payment.setDefault(resultSet.getBoolean("is_default"));

                    list.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching payments");
        }
        return list;
    }
}
