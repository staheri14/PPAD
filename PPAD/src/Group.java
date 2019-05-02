import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stahe on 12/27/2017.
 */
public class Group {
    BigInteger gID;
    AtomicInteger size=new AtomicInteger(0);
    public ConcurrentHashMap<AtomicInteger,Profile> members=new ConcurrentHashMap<>(PSP.groupSize);
}
