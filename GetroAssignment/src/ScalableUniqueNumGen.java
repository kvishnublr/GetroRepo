import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * Creates a unique tracking number per instnace and supports horizontal scaling.
 */

public class ScalableUniqueNumGen {

    private static final String NODE_ID = createUniqueNodeId();
    private static final long MAX_SEQUENCE = 1000000L;

    private static final AtomicLong counter = new AtomicLong(0);

    /**
     * Creates a unique identifier for this node using the hostname and a portion of a UUID.
     * @return A unique node identifier.
     */
    private static String createUniqueNodeId() {
        try {
            // This gets initiated when class is loaded for first time
            String host = InetAddress.getLocalHost().getHostName();
            String uuidPart = UUID.randomUUID().toString().substring(0, 8); // Extract first 8 characters of UUID
            return host + "-" + uuidPart;
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to retrieve hostname for node ID generation", e);
        }
    }

    /**
     * Produces a unique tracking number.
     * @return A unique tracking number.
     */
    public static String createTrackingNumber() {
        long currentTime = System.currentTimeMillis();
        long sequence = counter.getAndIncrement();

        if (sequence >= MAX_SEQUENCE) {
            synchronized (counter) {
                if (counter.get() >= MAX_SEQUENCE) {
                    counter.set(0); // Reset the sequence when limit is reached
                    sequence = counter.getAndIncrement();
                }
            }
        }

        return NODE_ID + "-" + currentTime + "-" + String.format("%06d", sequence);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(createTrackingNumber());
        }
    }
}
