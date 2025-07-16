import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class RegistrationService {
    
    public static class Registration {
        public final int registrationId;
        public final int eventId;
        public final int participantId;
        public final LocalDateTime registrationDate;
        public final String status;
        public final String paymentStatus;
        public final String notes;
        
        public Registration(int registrationId, int eventId, int participantId, 
                          LocalDateTime registrationDate, String status, String paymentStatus, String notes) {
            this.registrationId = registrationId;
            this.eventId = eventId;
            this.participantId = participantId;
            this.registrationDate = registrationDate;
            this.status = status;
            this.paymentStatus = paymentStatus;
            this.notes = notes;
        }
        
        @Override
        public String toString() {
            return String.format("Registration{id=%d, eventId=%d, participantId=%d, status='%s', payment='%s'}", 
                registrationId, eventId, participantId, status, paymentStatus);
        }
    }
    
    public static Function<ResultSet, Registration> mapToRegistration() {
        return rs -> {
            try {
                return new Registration(
                    rs.getInt("registration_id"),
                    rs.getInt("event_id"),
                    rs.getInt("participant_id"),
                    rs.getTimestamp("registration_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getString("payment_status"),
                    rs.getString("notes")
                );
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping ResultSet to Registration", e);
            }
        };
    }
    
    public static List<Registration> getAllRegistrations() {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "SELECT * FROM registrations ORDER BY registration_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                List<Registration> registrations = new ArrayList<>();
                while (rs.next()) {
                    registrations.add(mapToRegistration().apply(rs));
                }
                return registrations;
            }
        }).orElse(Collections.emptyList());
    }
    
    public static List<Registration> getRegistrationsByEvent(int eventId) {
        return getAllRegistrations().stream()
            .filter(registration -> registration.eventId == eventId)
            .collect(Collectors.toList());
    }
    
    public static List<Registration> getRegistrationsByParticipant(int participantId) {
        return getAllRegistrations().stream()
            .filter(registration -> registration.participantId == participantId)
            .collect(Collectors.toList());
    }
    
    public static List<Registration> getConfirmedRegistrations() {
        return getAllRegistrations().stream()
            .filter(registration -> "CONFIRMED".equals(registration.status))
            .collect(Collectors.toList());
    }
    
    public static long countRegistrationsForEvent(int eventId) {
        return getRegistrationsByEvent(eventId).stream()
            .filter(registration -> "CONFIRMED".equals(registration.status))
            .count();
    }
    
    public static Optional<Integer> createRegistration(int eventId, int participantId, String notes) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "INSERT INTO registrations (event_id, participant_id, notes) " +
                        "VALUES (?, ?, ?) RETURNING registration_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                stmt.setInt(2, participantId);
                stmt.setString(3, notes);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return null;
        });
    }
    
    public static boolean updateRegistrationStatus(int registrationId, String status) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "UPDATE registrations SET status = ? WHERE registration_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, registrationId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
    
    public static boolean updatePaymentStatus(int registrationId, String paymentStatus) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "UPDATE registrations SET payment_status = ? WHERE registration_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, paymentStatus);
                stmt.setInt(2, registrationId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
    
    public static boolean cancelRegistration(int registrationId) {
        return updateRegistrationStatus(registrationId, "CANCELLED");
    }
    
    public static boolean deleteRegistration(int registrationId) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "DELETE FROM registrations WHERE registration_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, registrationId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
}
