import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chat Application - Single File Implementation
 * Features: Message payload, sent/received/read flags, recipient phone number
 */

// ==================== MESSAGE CLASS ====================
class Message {
    private String payload;           // The actual message text
    private boolean isSent;           // Flag: message has been sent
    private boolean isReceived;       // Flag: message has been received
    private boolean isRead;           // Flag: message has been read
    private String recipientPhone;    // Phone number of recipient
    private String senderPhone;       // Phone number of sender
    private LocalDateTime timestamp;  // When message was created
    
    // Constructor
    public Message(String payload, String senderPhone, String recipientPhone) {
        this.payload = payload;
        this.senderPhone = senderPhone;
        this.recipientPhone = recipientPhone;
        this.timestamp = LocalDateTime.now();
        this.isSent = false;
        this.isReceived = false;
        this.isRead = false;
    }
    
    // Getters
    public String getPayload() { return payload; }
    public boolean isSent() { return isSent; }
    public boolean isReceived() { return isReceived; }
    public boolean isRead() { return isRead; }
    public String getRecipientPhone() { return recipientPhone; }
    public String getSenderPhone() { return senderPhone; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    // Status setters (simulating message delivery progression)
    public void markAsSent() { this.isSent = true; }
    public void markAsReceived() { this.isReceived = true; }
    public void markAsRead() { this.isRead = true; }
    
    // Get status icon similar to WhatsApp
    public String getStatusIcon() {
        if (isRead) return "✓✓ READ";           // Blue double check
        if (isReceived) return "✓✓ RECEIVED";   // Grey double check
        if (isSent) return "✓ SENT";            // Single check
        return "⏳ PENDING";                     // Not sent yet
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%s] From: %s | To: %s | %s | Status: %s", 
            timestamp.format(formatter), senderPhone, recipientPhone, payload, getStatusIcon());
    }
}

// ==================== USER CLASS ====================
class User {
    private String name;
    private String phoneNumber;
    private List<Message> sentMessages;
    private List<Message> receivedMessages;
    
    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }
    
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getReceivedMessages() { return receivedMessages; }
    
    // Send a message to another user
    public Message sendMessage(String payload, User recipient) {
        Message message = new Message(payload, this.phoneNumber, recipient.getPhoneNumber());
        message.markAsSent();  // Mark as sent immediately
        sentMessages.add(message);
        recipient.receiveMessage(message);
        System.out.println("📤 Message sent to " + recipient.getName());
        return message;
    }
    
    // Receive a message from another user
    public void receiveMessage(Message message) {
        receivedMessages.add(message);
        message.markAsReceived();  // Mark as received
    }
    
    // Read a specific message
    public void readMessage(int index) {
        if (index >= 0 && index < receivedMessages.size()) {
            Message message = receivedMessages.get(index);
            message.markAsRead();
            System.out.println("📖 Message read: " + message.getPayload());
        }
    }
    
    // Display chat history with a specific user
    public void displayChatWith(User otherUser) {
        System.out.println("\n========== Chat with " + otherUser.getName() + " ==========");
        
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);
        
        // Filter messages between these two users
        allMessages.stream()
            .filter(m -> m.getSenderPhone().equals(otherUser.getPhoneNumber()) || 
                        m.getRecipientPhone().equals(otherUser.getPhoneNumber()))
            .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
            .forEach(m -> {
                String direction = m.getSenderPhone().equals(this.phoneNumber) ? "You →" : "← " + otherUser.getName();
                System.out.println(direction + " " + m.getPayload() + " [" + m.getStatusIcon() + "]");
            });
    }
}

// ==================== CHAT APP MAIN CLASS ====================
public class ChatApp {
    private static List<User> users = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         📱 JAVA CHAT APP             ║");
        System.out.println("║   Message Status: Sent/Received/Read ║");
        System.out.println("╚══════════════════════════════════════╝\n");
        
        // Create sample users
        User alice = new User("Alice", "+27 71 123 4567");
        User bob = new User("Bob", "+27 82 987 6543");
        User charlie = new User("Charlie", "+27 63 456 7890");
        
        users.add(alice);
        users.add(bob);
        users.add(charlie);
        
        boolean running = true;
        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send Message");
            System.out.println("2. View Inbox");
            System.out.println("3. Read Message");
            System.out.println("4. View Chat History");
            System.out.println("5. View All Messages (Debug)");
            System.out.println("6. Exit");
            System.out.print("\nChoose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            switch (choice) {
                case 1: sendMessageMenu(); break;
                case 2: viewInboxMenu(); break;
                case 3: readMessageMenu(); break;
                case 4: viewChatHistoryMenu(); break;
                case 5: viewAllMessagesDebug(); break;
                case 6: 
                    running = false;
                    System.out.println("Goodbye! 👋");
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
    
    private static void sendMessageMenu() {
        System.out.println("\n--- Send Message ---");
        System.out.println("Available users:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " (" + users.get(i).getPhoneNumber() + ")");
        }
        
        System.out.print("\nSelect sender (number): ");
        int senderIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        System.out.print("Select recipient (number): ");
        int recipientIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (senderIdx == recipientIdx) {
            System.out.println("❌ Cannot send message to yourself!");
            return;
        }
        
        System.out.print("Enter message: ");
        String payload = scanner.nextLine();
        
        User sender = users.get(senderIdx);
        User recipient = users.get(recipientIdx);
        
        Message msg = sender.sendMessage(payload, recipient);
        
        // Simulate delivery progression
        System.out.println("\n📊 Message Status Progression:");
        System.out.println("   Initial: " + msg.getStatusIcon());
        
        // Already marked as sent and received in sendMessage()
        System.out.println("   After delivery: " + msg.getStatusIcon());
    }
    
    private static void viewInboxMenu() {
        System.out.println("\n--- View Inbox ---");
        System.out.println("Select user:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }
        
        System.out.print("\nChoice: ");
        int userIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        User user = users.get(userIdx);
        List<Message> inbox = user.getReceivedMessages();
        
        if (inbox.isEmpty()) {
            System.out.println("📭 Inbox is empty!");
            return;
        }
        
        System.out.println("\n📬 Inbox for " + user.getName() + ":");
        for (int i = 0; i < inbox.size(); i++) {
            Message m = inbox.get(i);
            String readStatus = m.isRead() ? "📖 READ" : "🔔 UNREAD";
            System.out.println((i + 1) + ". From: " + m.getSenderPhone() + " | " + m.getPayload() + " | " + readStatus);
        }
    }
    
    private static void readMessageMenu() {
        System.out.println("\n--- Read Message ---");
        System.out.println("Select user:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }
        
        System.out.print("\nChoice: ");
        int userIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        User user = users.get(userIdx);
        List<Message> inbox = user.getReceivedMessages();
        
        if (inbox.isEmpty()) {
            System.out.println("📭 No messages to read!");
            return;
        }
        
        System.out.println("\nSelect message to read:");
        for (int i = 0; i < inbox.size(); i++) {
            Message m = inbox.get(i);
            System.out.println((i + 1) + ". From: " + m.getSenderPhone() + " | Status: " + m.getStatusIcon());
        }
        
        System.out.print("\nChoice: ");
        int msgIdx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (msgIdx >= 0 && msgIdx < inbox.size()) {
            Message m = inbox.get(msgIdx);
            System.out.println("\n📄 Message Content: " + m.getPayload());
            user.readMessage(msgIdx);
            System.out.println("✅ Status updated to: " + m.getStatusIcon());
        }
    }
    
    private static void viewChatHistoryMenu() {
        System.out.println("\n--- View Chat History ---");
        System.out.println("Select user:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }
        
        System.out.print("\nYour choice: ");
        int user1Idx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        System.out.println("Select contact to view chat with:");
        for (int i = 0; i < users.size(); i++) {
            if (i != user1Idx) {
                System.out.println((i + 1) + ". " + users.get(i).getName());
            }
        }
        
        System.out.print("\nChoice: ");
        int user2Idx = scanner.nextInt() - 1;
        scanner.nextLine();
        
        users.get(user1Idx).displayChatWith(users.get(user2Idx));
    }
    
    private static void viewAllMessagesDebug() {
        System.out.println("\n--- All Messages (Debug View) ---");
        for (User user : users) {
            System.out.println("\n>> " + user.getName() + "'s Sent Messages:");
            for (Message m : user.getSentMessages()) {
                System.out.println("   " + m);
            }
        }
    }
}
