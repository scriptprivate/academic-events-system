import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function; 

public class EventService {
    
    public static class Event {
        public final int eventId;
        public final String eventName;
        public final String description;
        public final LocalDate startDate;
        public final LocalDate endDate;
        public final LocalDate registrationDeadline;
        public final int maxParticipants;
        public final BigDecimal registrationFee;
        public final int categoryId;
        public final int locationId;
        public final String status;
        
        public Event(int eventId, String eventName, String description, LocalDate startDate, 
                    LocalDate endDate, LocalDate registrationDeadline, int maxParticipants, 
                    BigDecimal registrationFee, int categoryId, int locationId, String status) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.registrationDeadline = registrationDeadline;
            this.maxParticipants = maxParticipants;
            this.registrationFee = registrationFee;
            this.categoryId = categoryId;
            this.locationId = locationId;
            this.status = status;
        }
        
        @Override
        public String toString() {
            return String.format("Event{id=%d, name='%s', startDate=%s, endDate=%s, fee=%.2f, status='%s'}", 
                eventId, eventName, startDate, endDate, registrationFee, status);
        }
    }
    
    public static Function<ResultSet, Event> mapToEvent() {
        return rs -> {
            try {
                return new Event(
                    rs.getInt("event_id"),
                    rs.getString("event_name"),
                    rs.getString("description"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDate("registration_deadline") != null ? 
                        rs.getDate("registration_deadline").toLocalDate() : null,
                    rs.getInt("max_participants"),
                    rs.getBigDecimal("registration_fee"),
                    rs.getInt("category_id"),
                    rs.getInt("location_id"),
                    rs.getString("status")
                );
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping ResultSet to Event", e);
            }
        };
    }
    
    public static List<Event> getAllEvents() {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "SELECT * FROM events ORDER BY start_date";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                List<Event> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(mapToEvent().apply(rs));
                }
                return events;
            }
        }).orElse(Collections.emptyList());
    }
    
    public static List<Event> getEventsByCategory(int categoryId) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "SELECT * FROM events WHERE category_id = ? ORDER BY start_date";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, categoryId);
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Event> events = new ArrayList<>();
                    while (rs.next()) {
                        events.add(mapToEvent().apply(rs));
                    }
                    return events;
                }
            }
        }).orElse(Collections.emptyList());
    }
    
    public static List<Event> getUpcomingEvents() {
        return getAllEvents().stream()
            .filter(event -> event.startDate.isAfter(LocalDate.now()))
            .filter(event -> "ACTIVE".equals(event.status))
            .collect(Collectors.toList());
    }
    
    public static List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return getAllEvents().stream()
            .filter(event -> !event.startDate.isBefore(startDate) && !event.endDate.isAfter(endDate))
            .collect(Collectors.toList());
    }
    
    public static Optional<Integer> createEvent(String eventName, String description, LocalDate startDate, 
                                               LocalDate endDate, LocalDate registrationDeadline, 
                                               int maxParticipants, BigDecimal registrationFee, 
                                               int categoryId, int locationId) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "INSERT INTO events (event_name, description, start_date, end_date, " +
                        "registration_deadline, max_participants, registration_fee, category_id, location_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING event_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, eventName);
                stmt.setString(2, description);
                // FIX 2: Use java.sql.Date explicitly to resolve ambiguity
                stmt.setDate(3, java.sql.Date.valueOf(startDate));
                stmt.setDate(4, java.sql.Date.valueOf(endDate));
                stmt.setDate(5, registrationDeadline != null ? java.sql.Date.valueOf(registrationDeadline) : null);
                stmt.setInt(6, maxParticipants);
                stmt.setBigDecimal(7, registrationFee);
                stmt.setInt(8, categoryId);
                stmt.setInt(9, locationId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return null;
        });
    }
    
    public static boolean updateEventStatus(int eventId, String status) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "UPDATE events SET status = ? WHERE event_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, eventId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
    
    public static boolean deleteEvent(int eventId) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "DELETE FROM events WHERE event_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
}
