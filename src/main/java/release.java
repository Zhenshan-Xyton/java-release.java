// B2 STRESS FIXTURE (java): RELEASE
// EFFECT: authority:HELD, validity:VALID -> authority:RELINQUISHED, validity:INVALID
// OBSERVABLE AT: R1 (syntax only -- no execution, no compilation, no dataflow required to see it)
// GROUNDED IN: codeql_census/repo/java/ql/lib/ext/java.io.model.yml lines 137 and 149
//   (java.io.Closeable#close and java.io.InputStream#close summary rows -- the deallocator
//   CodeQL threads flow through), ext/java.sql.model.yml line 39 (java.sql.Statement#close
//   summary row), ext/java.util.concurrent.locks.model.yml (java.util.concurrent.locks.Lock
//   #lock / #unlock summary rows -- both directions of the same allocator/deallocator pair),
//   ext/java.util.concurrent.model.yml line ~40 (java.util.concurrent.ExecutorService#shutdown
//   summary row), and ext/java.nio.channels.model.yml (the FileChannel family whose #close is
//   inherited from java.io.Closeable and modeled by the same close() row above).
// REALIZATIONS (noun x verb-spelling, each CLOSED + OPEN):
//   1. noun=file output stream, verb=.close()
//   2. noun=JDBC prepared statement, verb=.close()
//   3. noun=reentrant lock, verb=.unlock()
//   4. noun=executor service, verb=.shutdown()
//   5. noun=TCP socket, verb=.close()
//   6. noun=file channel, verb=.close()

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class release {

    // --- realization 1: noun=file output stream, verb=.close() ---------------------
    // CLOSED
    static void writeAuditRecord(String path, byte[] record) throws IOException {
        FileOutputStream out = new FileOutputStream(path, true);
        out.write(record);
        out.flush();
        out.close();
    }

    // OPEN
    static void writeAuditRecordLeaky(String path, byte[] record) throws IOException {
        FileOutputStream out = new FileOutputStream(path, true);
        out.write(record);
        out.flush();
        // out is never closed on this path -- the file descriptor stays held.
    }

    // --- realization 2: noun=JDBC prepared statement, verb=.close() ----------------
    // CLOSED
    static void updateUserEmail(Connection conn, long userId, String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET email = ? WHERE id = ?");
        ps.setString(1, email);
        ps.setLong(2, userId);
        ps.executeUpdate();
        ps.close();
    }

    // OPEN
    static void updateUserEmailLeaky(Connection conn, long userId, String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET email = ? WHERE id = ?");
        ps.setString(1, email);
        ps.setLong(2, userId);
        ps.executeUpdate();
        // ps.close() is simply absent -- the statement handle is never released.
    }

    // --- realization 3: noun=reentrant lock, verb=.unlock() ------------------------
    private static final ReentrantLock cacheLock = new ReentrantLock();

    // CLOSED
    static void updateCacheEntry(String key, String value, java.util.Map<String, String> cache) {
        cacheLock.lock();
        cache.put(key, value);
        cacheLock.unlock();
    }

    // OPEN
    static void updateCacheEntryLeaky(String key, String value, java.util.Map<String, String> cache) {
        cacheLock.lock();
        cache.put(key, value);
        // cacheLock.unlock() is absent -- every later caller of updateCacheEntry deadlocks.
    }

    // --- realization 4: noun=executor service, verb=.shutdown() --------------------
    // CLOSED
    static void runOneOffTask(Runnable task) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.submit(task);
        pool.shutdown();
    }

    // OPEN
    static void runOneOffTaskLeaky(Runnable task) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.submit(task);
        // pool.shutdown() is never called -- the worker thread is held open forever.
    }

    // --- realization 5: noun=TCP socket, verb=.close() -----------------------------
    // CLOSED
    static void sendHeartbeat(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        socket.getOutputStream().write("HEARTBEAT\n".getBytes());
        socket.close();
    }

    // OPEN
    static void sendHeartbeatLeaky(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        socket.getOutputStream().write("HEARTBEAT\n".getBytes());
        // socket.close() is absent -- the connection and its file descriptor stay held.
    }

    // --- realization 6: noun=file channel, verb=.close() ----------------------------
    // CLOSED
    static void appendToLog(String path, ByteBuffer data) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE,
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        channel.write(data);
        channel.close();
    }

    // OPEN
    static void appendToLogLeaky(String path, ByteBuffer data) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE,
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        channel.write(data);
        // channel.close() is simply absent -- the underlying file descriptor is never released.
    }
}
