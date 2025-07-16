import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParticipantService {
    
    public static class Participant {
        public final int participantId;
        public final String firstName;
        public final String lastName;
        public final String email;
        public final String phone;
        public final String institution;
        public final String participantType;
        
        public Participant(int participantId, String firstName, String lastName, String email, 
                          String phone, String institution, String participantType) {
            this.participantId = participantId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.institution = institution;
            this.participantType = participantType;
        }
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
        
        @Override
        public String toString() {
            return String.format("Participant{id=%d, name='%s', email='%s', type='%s', institution='%s'}", 
                participantId, getFullName(), email, participantType, institution);
        }
    }
    
    public static Function<ResultSet, Participant> mapToParticipant() {
        return rs -> {
            try {
                return new Participant(
                    rs.getInt("participant_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("institution"),
                    rs.getString("participant_type")
                );
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping ResultSet to Participant", e);
            }
        };
    }
    
    public static List<Participant> getAllParticipants() {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "SELECT * FROM participants ORDER BY last_name, first_name";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                List<Participant> participants = new ArrayList<>();
                while (rs.next()) {
                    participants.add(mapToParticipant().apply(rs));
                }
                return participants;
            }
        }).orElse(Collections.emptyList());
    }
    
    public static Optional<Participant> getParticipantByEmail(String email) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "SELECT * FROM participants WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapToParticipant().apply(rs);
                    }
                }
            }
            return null;
        });
    }
    
    public static List<Participant> getParticipantsByType(String participantType) {
        return getAllParticipants().stream()
            .filter(participant -> participantType.equals(participant.participantType))
            .collect(Collectors.toList());
    }
    
    public static List<Participant> getParticipantsByInstitution(String institution) {
        return getAllParticipants().stream()
            .filter(participant -> participant.institution != null && 
                   participant.institution.toLowerCase().contains(institution.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public static Optional<Integer> createParticipant(String firstName, String lastName, String email, 
                                                     String phone, String institution, String participantType) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "INSERT INTO participants (first_name, last_name, email, phone, institution, participant_type) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING participant_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, institution);
                stmt.setString(6, participantType);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return null;
        });
    }
    
    public static boolean updateParticipant(int participantId, String firstName, String lastName, 
                                           String email, String phone, String institution, String participantType) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "UPDATE participants SET first_name = ?, last_name = ?, email = ?, " +
                        "phone = ?, institution = ?, participant_type = ? WHERE participant_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, institution);
                stmt.setString(6, participantType);
                stmt.setInt(7, participantId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
    
    public static boolean deleteParticipant(int participantId) {
        return DatabaseConnection.executeWithConnection(conn -> {
            String sql = "DELETE FROM participants WHERE participant_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, participantId);
                return stmt.executeUpdate() > 0;
            }
        }).orElse(false);
    }
}
