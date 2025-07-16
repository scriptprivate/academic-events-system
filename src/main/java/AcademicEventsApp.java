import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AcademicEventsApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static void main(String[] args) {
        System.out.println("=== Academic Events Management System ===");
        System.out.println("Functional and Declarative");
        System.out.println();
        
        if (!testDatabaseConnection()) {
            System.err.println("Unable to connect to database. Please check your configuration.");
            return;
        }
        
        runApplicationLoop();
    }
    
    public static boolean testDatabaseConnection() {
        return DatabaseConnection.createConnection()
            .map(conn -> {
                try {
                    conn.close();
                    System.out.println("[+] Database connection successful!");
                    return true;
                } catch (Exception e) {
                    System.err.println("[!] Database connection test failed: " + e.getMessage());
                    return false;
                }
            })
            .orElse(false);
    }
    
    public static void runApplicationLoop() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1 -> handleEventManagement();
                case 2 -> handleParticipantManagement();
                case 3 -> handleRegistrationManagement();
                case 4 -> handleReports();
                case 5 -> {
                    System.out.println("Thank you for using Academic Events Management System!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    public static void displayMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Event Management");
        System.out.println("2. Participant Management");
        System.out.println("3. Registration Management");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.println();
    }
    
    public static void handleEventManagement() {
        System.out.println("\n=== Event Management ===");
        System.out.println("1. View All Events");
        System.out.println("2. View Upcoming Events");
        System.out.println("3. Create New Event");
        System.out.println("4. Update Event Status");
        System.out.println("5. Delete Event");
        System.out.println("6. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> viewAllEvents();
            case 2 -> viewUpcomingEvents();
            case 3 -> createNewEvent();
            case 4 -> updateEventStatus();
            case 5 -> deleteEvent();
            case 6 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }
    
    public static void handleParticipantManagement() {
        System.out.println("\n=== Participant Management ===");
        System.out.println("1. View All Participants");
        System.out.println("2. Search Participant by Email");
        System.out.println("3. View Participants by Type");
        System.out.println("4. Create New Participant");
        System.out.println("5. Update Participant");
        System.out.println("6. Delete Participant");
        System.out.println("7. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> viewAllParticipants();
            case 2 -> searchParticipantByEmail();
            case 3 -> viewParticipantsByType();
            case 4 -> createNewParticipant();
            case 5 -> updateParticipant();
            case 6 -> deleteParticipant();
            case 7 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }
    
    public static void handleRegistrationManagement() {
        System.out.println("\n=== Registration Management ===");
        System.out.println("1. View All Registrations");
        System.out.println("2. View Registrations by Event");
        System.out.println("3. View Registrations by Participant");
        System.out.println("4. Create New Registration");
        System.out.println("5. Update Registration Status");
        System.out.println("6. Update Payment Status");
        System.out.println("7. Cancel Registration");
        System.out.println("8. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> viewAllRegistrations();
            case 2 -> viewRegistrationsByEvent();
            case 3 -> viewRegistrationsByParticipant();
            case 4 -> createNewRegistration();
            case 5 -> updateRegistrationStatus();
            case 6 -> updatePaymentStatus();
            case 7 -> cancelRegistration();
            case 8 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }
    
    public static void handleReports() {
        System.out.println("\n=== Reports ===");
        System.out.println("1. Event Summary Report");
        System.out.println("2. Participant Summary Report");
        System.out.println("3. Registration Summary Report");
        System.out.println("4. Revenue Report");
        System.out.println("5. Back to Main Menu");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1 -> generateEventSummaryReport();
            case 2 -> generateParticipantSummaryReport();
            case 3 -> generateRegistrationSummaryReport();
            case 4 -> generateRevenueReport();
            case 5 -> { /* Return to main menu */ }
            default -> System.out.println("Invalid choice.");
        }
    }
    
    public static void viewAllEvents() {
        System.out.println("\n=== All Events ===");
        List<EventService.Event> events = EventService.getAllEvents();
        
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            events.forEach(System.out::println);
        }
    }
    
    public static void viewUpcomingEvents() {
        System.out.println("\n=== Upcoming Events ===");
        List<EventService.Event> events = EventService.getUpcomingEvents();
        
        if (events.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            events.forEach(System.out::println);
        }
    }
    
    public static void createNewEvent() {
        System.out.println("\n=== Create New Event ===");
        
        String eventName = getStringInput("Event Name: ");
        String description = getStringInput("Description: ");
        LocalDate startDate = getDateInput("Start Date (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput("End Date (YYYY-MM-DD): ");
        LocalDate registrationDeadline = getOptionalDateInput("Registration Deadline (YYYY-MM-DD, optional): ");
        int maxParticipants = getIntInput("Maximum Participants: ");
        BigDecimal registrationFee = getBigDecimalInput("Registration Fee: ");
        int categoryId = getIntInput("Category ID: ");
        int locationId = getIntInput("Location ID: ");
        
        Optional<Integer> eventId = EventService.createEvent(
            eventName, description, startDate, endDate, registrationDeadline,
            maxParticipants, registrationFee, categoryId, locationId
        );
        
        if (eventId.isPresent()) {
            System.out.println("Event created successfully with ID: " + eventId.get());
        } else {
            System.out.println("Failed to create event.");
        }
    }
    
    public static void updateEventStatus() {
        System.out.println("\n=== Update Event Status ===");
        
        int eventId = getIntInput("Event ID: ");
        String status = getStringInput("New Status (ACTIVE/CANCELLED/COMPLETED): ");
        
        if (EventService.updateEventStatus(eventId, status)) {
            System.out.println("Event status updated successfully.");
        } else {
            System.out.println("Failed to update event status.");
        }
    }
    
    public static void deleteEvent() {
        System.out.println("\n=== Delete Event ===");
        
        int eventId = getIntInput("Event ID to delete: ");
        String confirm = getStringInput("Are you sure? (yes/no): ");
        
        if ("yes".equalsIgnoreCase(confirm)) {
            if (EventService.deleteEvent(eventId)) {
                System.out.println("Event deleted successfully.");
            } else {
                System.out.println("Failed to delete event.");
            }
        } else {
            System.out.println("Event deletion cancelled.");
        }
    }
    
    public static void viewAllParticipants() {
        System.out.println("\n=== All Participants ===");
        List<ParticipantService.Participant> participants = ParticipantService.getAllParticipants();
        
        if (participants.isEmpty()) {
            System.out.println("No participants found.");
        } else {
            participants.forEach(System.out::println);
        }
    }
    
    public static void searchParticipantByEmail() {
        System.out.println("\n=== Search Participant by Email ===");
        
        String email = getStringInput("Email: ");
        Optional<ParticipantService.Participant> participant = ParticipantService.getParticipantByEmail(email);
        
        if (participant.isPresent()) {
            System.out.println("Found: " + participant.get());
        } else {
            System.out.println("Participant not found.");
        }
    }
    
    public static void viewParticipantsByType() {
        System.out.println("\n=== Participants by Type ===");
        
        String type = getStringInput("Participant Type (STUDENT/PROFESSOR/RESEARCHER/OTHER): ");
        List<ParticipantService.Participant> participants = ParticipantService.getParticipantsByType(type);
        
        if (participants.isEmpty()) {
            System.out.println("No participants found for type: " + type);
        } else {
            participants.forEach(System.out::println);
        }
    }
    
    public static void createNewParticipant() {
        System.out.println("\n=== Create New Participant ===");
        
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        String email = getStringInput("Email: ");
        String phone = getStringInput("Phone: ");
        String institution = getStringInput("Institution: ");
        String participantType = getStringInput("Participant Type (STUDENT/PROFESSOR/RESEARCHER/OTHER): ");
        
        Optional<Integer> participantId = ParticipantService.createParticipant(
            firstName, lastName, email, phone, institution, participantType
        );
        
        if (participantId.isPresent()) {
            System.out.println("Participant created successfully with ID: " + participantId.get());
        } else {
            System.out.println("Failed to create participant.");
        }
    }
    
    public static void updateParticipant() {
        System.out.println("\n=== Update Participant ===");
        
        int participantId = getIntInput("Participant ID: ");
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        String email = getStringInput("Email: ");
        String phone = getStringInput("Phone: ");
        String institution = getStringInput("Institution: ");
        String participantType = getStringInput("Participant Type: ");
        
        if (ParticipantService.updateParticipant(participantId, firstName, lastName, email, phone, institution, participantType)) {
            System.out.println("Participant updated successfully.");
        } else {
            System.out.println("Failed to update participant.");
        }
    }
    
    public static void deleteParticipant() {
        System.out.println("\n=== Delete Participant ===");
        
        int participantId = getIntInput("Participant ID to delete: ");
        String confirm = getStringInput("Are you sure? (yes/no): ");
        
        if ("yes".equalsIgnoreCase(confirm)) {
            if (ParticipantService.deleteParticipant(participantId)) {
                System.out.println("Participant deleted successfully.");
            } else {
                System.out.println("Failed to delete participant.");
            }
        } else {
            System.out.println("Participant deletion cancelled.");
        }
    }
    
    public static void viewAllRegistrations() {
        System.out.println("\n=== All Registrations ===");
        List<RegistrationService.Registration> registrations = RegistrationService.getAllRegistrations();
        
        if (registrations.isEmpty()) {
            System.out.println("No registrations found.");
        } else {
            registrations.forEach(System.out::println);
        }
    }
    
    public static void viewRegistrationsByEvent() {
        System.out.println("\n=== Registrations by Event ===");
        
        int eventId = getIntInput("Event ID: ");
        List<RegistrationService.Registration> registrations = RegistrationService.getRegistrationsByEvent(eventId);
        
        if (registrations.isEmpty()) {
            System.out.println("No registrations found for event ID: " + eventId);
        } else {
            registrations.forEach(System.out::println);
            System.out.println("Total registrations: " + registrations.size());
        }
    }
    
    public static void viewRegistrationsByParticipant() {
        System.out.println("\n=== Registrations by Participant ===");
        
        int participantId = getIntInput("Participant ID: ");
        List<RegistrationService.Registration> registrations = RegistrationService.getRegistrationsByParticipant(participantId);
        
        if (registrations.isEmpty()) {
            System.out.println("No registrations found for participant ID: " + participantId);
        } else {
            registrations.forEach(System.out::println);
            System.out.println("Total registrations: " + registrations.size());
        }
    }
    
    public static void createNewRegistration() {
        System.out.println("\n=== Create New Registration ===");
        
        int eventId = getIntInput("Event ID: ");
        int participantId = getIntInput("Participant ID: ");
        String notes = getStringInput("Notes (optional): ");
        
        long currentRegistrations = RegistrationService.countRegistrationsForEvent(eventId);
        System.out.println("Current registrations for this event: " + currentRegistrations);
        
        Optional<Integer> registrationId = RegistrationService.createRegistration(eventId, participantId, notes);
        
        if (registrationId.isPresent()) {
            System.out.println("Registration created successfully with ID: " + registrationId.get());
        } else {
            System.out.println("Failed to create registration. Check if participant is already registered for this event.");
        }
    }
    
    public static void updateRegistrationStatus() {
        System.out.println("\n=== Update Registration Status ===");
        
        int registrationId = getIntInput("Registration ID: ");
        String status = getStringInput("New Status (PENDING/CONFIRMED/CANCELLED): ");
        
        if (RegistrationService.updateRegistrationStatus(registrationId, status)) {
            System.out.println("Registration status updated successfully.");
        } else {
            System.out.println("Failed to update registration status.");
        }
    }
    
    public static void updatePaymentStatus() {
        System.out.println("\n=== Update Payment Status ===");
        
        int registrationId = getIntInput("Registration ID: ");
        String paymentStatus = getStringInput("New Payment Status (PENDING/PAID/REFUNDED): ");
        
        if (RegistrationService.updatePaymentStatus(registrationId, paymentStatus)) {
            System.out.println("Payment status updated successfully.");
        } else {
            System.out.println("Failed to update payment status.");
        }
    }
    
    public static void cancelRegistration() {
        System.out.println("\n=== Cancel Registration ===");
        
        int registrationId = getIntInput("Registration ID: ");
        String confirm = getStringInput("Are you sure? (yes/no): ");
        
        if ("yes".equalsIgnoreCase(confirm)) {
            if (RegistrationService.cancelRegistration(registrationId)) {
                System.out.println("Registration cancelled successfully.");
            } else {
                System.out.println("Failed to cancel registration.");
            }
        } else {
            System.out.println("Registration cancellation cancelled.");
        }
    }
    
    public static void generateEventSummaryReport() {
        System.out.println("\n=== Event Summary Report ===");
        
        List<EventService.Event> allEvents = EventService.getAllEvents();
        List<EventService.Event> upcomingEvents = EventService.getUpcomingEvents();
        
        System.out.println("Total Events: " + allEvents.size());
        System.out.println("Upcoming Events: " + upcomingEvents.size());
        
        allEvents.stream()
            .collect(java.util.stream.Collectors.groupingBy(event -> event.status))
            .forEach((status, events) -> 
                System.out.println("Events with status '" + status + "': " + events.size())
            );
        
        if (!upcomingEvents.isEmpty()) {
            System.out.println("\nUpcoming Events:");
            upcomingEvents.forEach(event -> 
                System.out.println("- " + event.eventName + " (" + event.startDate + ")")
            );
        }
    }
    
    public static void generateParticipantSummaryReport() {
        System.out.println("\n=== Participant Summary Report ===");
        
        List<ParticipantService.Participant> allParticipants = ParticipantService.getAllParticipants();
        System.out.println("Total Participants: " + allParticipants.size());
        
        allParticipants.stream()
            .collect(java.util.stream.Collectors.groupingBy(participant -> participant.participantType))
            .forEach((type, participants) -> 
                System.out.println("Participants of type '" + type + "': " + participants.size())
            );
        
        allParticipants.stream()
            .filter(participant -> participant.institution != null && !participant.institution.isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(participant -> participant.institution))
            .entrySet().stream()
            .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
            .limit(5)
            .forEach(entry -> 
                System.out.println("Institution '" + entry.getKey() + "': " + entry.getValue().size() + " participants")
            );
    }
    
    public static void generateRegistrationSummaryReport() {
        System.out.println("\n=== Registration Summary Report ===");
        
        List<RegistrationService.Registration> allRegistrations = RegistrationService.getAllRegistrations();
        List<RegistrationService.Registration> confirmedRegistrations = RegistrationService.getConfirmedRegistrations();
        
        System.out.println("Total Registrations: " + allRegistrations.size());
        System.out.println("Confirmed Registrations: " + confirmedRegistrations.size());
        
        allRegistrations.stream()
            .collect(java.util.stream.Collectors.groupingBy(registration -> registration.status))
            .forEach((status, registrations) -> 
                System.out.println("Registrations with status '" + status + "': " + registrations.size())
            );
        
        allRegistrations.stream()
            .collect(java.util.stream.Collectors.groupingBy(registration -> registration.paymentStatus))
            .forEach((paymentStatus, registrations) -> 
                System.out.println("Registrations with payment status '" + paymentStatus + "': " + registrations.size())
            );
    }
    
    public static void generateRevenueReport() {
        System.out.println("\n=== Revenue Report ===");
        
        List<EventService.Event> allEvents = EventService.getAllEvents();
        List<RegistrationService.Registration> allRegistrations = RegistrationService.getAllRegistrations();
        
        BigDecimal totalPotentialRevenue = allEvents.stream()
            .map(event -> {
                long registrations = RegistrationService.countRegistrationsForEvent(event.eventId);
                return event.registrationFee.multiply(BigDecimal.valueOf(registrations));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Total Potential Revenue: $" + totalPotentialRevenue);
        
        BigDecimal paidRevenue = allRegistrations.stream()
            .filter(registration -> "PAID".equals(registration.paymentStatus))
            .map(registration -> {
                return allEvents.stream()
                    .filter(event -> event.eventId == registration.eventId)
                    .findFirst()
                    .map(event -> event.registrationFee)
                    .orElse(BigDecimal.ZERO);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("Revenue from Paid Registrations: $" + paidRevenue);
        
        System.out.println("\nRevenue by Event:");
        allEvents.forEach(event -> {
            long paidRegistrations = allRegistrations.stream()
                .filter(registration -> registration.eventId == event.eventId)
                .filter(registration -> "PAID".equals(registration.paymentStatus))
                .count();
            
            BigDecimal eventRevenue = event.registrationFee.multiply(BigDecimal.valueOf(paidRegistrations));
            System.out.println("- " + event.eventName + ": $" + eventRevenue);
        });
    }
    
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        return scanner.nextLine().trim();
    }
    
    public static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                System.out.flush();
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
    
    public static BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                System.out.flush();
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal number. Please try again.");
            }
        }
    }
    
    public static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                System.out.flush();
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
    }
    
    public static LocalDate getOptionalDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                System.out.flush();
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format or leave empty.");
            }
        }
    }
}
