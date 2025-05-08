import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchInsert {

    private static final int BATCH_SIZE = 500;

    public static void main() throws IOException {
        // Lê o arquivo e divide os inserts em lotes
        List<List<String>> batches = readBatches(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\oscip\\propostas_renovacao_update.sql"));

        // Cria um ExecutorService com um número fixo de threads
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Para cada lote, submete um Runnable ao ExecutorService
        for (List<String> batch : batches) {
            executor.submit(() -> {
                try (Connection conn = getConnection();
                     Statement stmt = conn.createStatement()) {

                    for (String insert : batch) {
                        stmt.addBatch(insert);
                    }

                    stmt.executeBatch();
                    System.out.println("Batch executado com sucesso");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        // Chama shutdown para garantir que todas as threads terminem
        executor.shutdown();
    }

    private static List<List<String>> readBatches(File file) throws IOException {
        List<List<String>> batches = new ArrayList<>();
        List<String> propostas = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            List<String> batch = new ArrayList<>();
        for (String proposta : propostas) {
            if (batch.size() == BATCH_SIZE) {
                batches.add(batch);
                batch = new ArrayList<>();
            }
            batch.add(proposta.replace(";", ""));
        }
        if (!batch.isEmpty()) {
            batches.add(batch);
        }
        return batches;
    }

    private static Connection getConnection() throws SQLException {
        return null;
    }
}
