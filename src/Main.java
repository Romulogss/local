import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(getDadoBancario("13.938-6", 12));
    }

    public static String getDadoBancario(String contaCorrente, int tamanho) {
        if (contaCorrente == null) {
            return leftPadWithZeros(null, tamanho);
        } else {
            contaCorrente = contaCorrente.replace(" ", "");
            contaCorrente = contaCorrente.replace(".", "");
        }
        String resultado;
        if (contaCorrente.contains("-")) {
            resultado = contaCorrente.split("-")[0];
        } else if (contaCorrente.length() >= 5) {
            resultado = contaCorrente.substring(0, contaCorrente.length() - 1);
        } else {
            resultado = contaCorrente;
        }
        return tratarCampoNumerico(resultado, tamanho);
    }

    public static String tratarCampoNumerico(String valor, int tamanho) {
        if (!isNumeric(valor)) {
            valor = "";
        }
        String result = leftPadWithZeros(valor.replaceAll(" ", ""), tamanho);
        return result.substring(0, tamanho);
    }

    public static String leftPadWithZeros(String input, int expectedSize) {
        if (input == null) {
            return leftPadWithZeros("", expectedSize);
        } else {
            StringBuilder sb = new StringBuilder(expectedSize);

            for (int i = expectedSize - input.length(); i > 0; --i) {
                sb.append("0");
            }

            sb.append(input);
            return sb.toString();
        }
    }

    public static boolean isNumeric(CharSequence cs) {
        if (cs != null && cs.length() != 0) {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static class VotoPorValor {
        private BigDecimal valor;
        private Long qtdVoto = 0L;

        public VotoPorValor(BigDecimal valor, Long qtdVoto) {
            this.valor = valor;
            this.qtdVoto = qtdVoto;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }

        public Long getQtdVoto() {
            return qtdVoto;
        }

        public void setQtdVoto(Long qtdVoto) {
            this.qtdVoto = qtdVoto;
        }
    }

    public static void tratarUpdateParticipante() throws IOException {
        FileInputStream stream = new FileInputStream("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\cpf_participacao.csv");
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String linha = br.readLine();
        StringBuilder result = new StringBuilder();
        while (linha != null) {
            String novaLinha = "UPDATE mpodigital.MCRED_PARTICIPANTE_CONTRATO SET PC_VALOR_PARTICIPACAO = :participacao WHERE CPF_PARTICIPANTE = ':cpfParticipante' AND PC_VALOR_PARTICIPACAO IS NULL;";
            String[] split = linha.split(",");
            novaLinha = novaLinha.replace(":participacao", split[1]);
            novaLinha = novaLinha.replace(":cpfParticipante", split[0]);
            result.append(novaLinha).append("\r");
            linha = br.readLine();
        }
        File file = new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\cpf_participacao.csv");
        Files.write(file.toPath(), result.toString().getBytes());
    }

    private static void tratarDataPagamento() throws IOException {
        FileInputStream stream = new FileInputStream("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\nosso_numero.csv");
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String linha = br.readLine();
        StringBuilder result = new StringBuilder();
        while (linha != null) {
            String[] split = linha.split(",");
            String date = split[1];
            DateTimeFormatter formatterOrigem = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(date, formatterOrigem);
            Instant instant = data.atStartOfDay(ZoneOffset.UTC).toInstant();
            String tsPagamento = String.valueOf(Timestamp.from(instant).getTime());
            linha = linha.concat(",").concat(tsPagamento.substring(0, Math.min(tsPagamento.length(), 10)));
            result.append(linha).append("\r");
            linha = br.readLine();
        }
        File file = new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\nosso_numero_data_formatada.csv");
        Files.write(file.toPath(), result.toString().getBytes());
    }

    public static void associarIdContrato() throws IOException {
        FileInputStream stream = new FileInputStream("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\PARCELAS_PAGAS_PRIMEIRO_IMPORT.csv");
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String linha = br.readLine();
        StringBuilder result = new StringBuilder();
        long idParcela = 78810L;
        while (linha != null) {
            linha = linha.replace("ID_PARCELA", Long.toString(idParcela));
            linha = linha.replace("NOSSO_NUMERO", Long.toString(idParcela));
            String cpf = linha.split(",")[2];
            FileInputStream stream2 = new FileInputStream("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\participantes_primeiro_import.csv");
            InputStreamReader reader2 = new InputStreamReader(stream2);
            BufferedReader br2 = new BufferedReader(reader2);
            String linha2 = br2.readLine();
            while (linha2 != null) {
                if (linha2.contains(cpf)) {
                    String idContrato = linha2.split(",")[0];
                    linha = linha.replace(cpf, idContrato);
                }
                linha2 = br2.readLine();
            }
            result.append(linha).append("\r");
            linha = br.readLine();
            idParcela++;
        }
        File file = new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\PARCELAS_PAGAS_PRIMEIRO_IMPORT_2_tratado.csv");
        Files.write(file.toPath(), result.toString().getBytes());
    }

    private static String getSubQueryCliente(String linha) {
        int inicioCpf = linha.indexOf("('") + 1;
        String cpf = linha.substring(inicioCpf, inicioCpf + 13);
        return linha.replaceAll(cpf, "(SELECT id_cliente FROM dbProdMPO.mpodigital.cliente WHERE cd_cpf_cnpj = " + cpf + ")");
    }

    private static String getSubQueryClienteAgente(String linha) {
        int inicioCpf = linha.indexOf("('") + 1;
        String cpf = linha.substring(inicioCpf, inicioCpf + 13);
        String cpfAgente = linha.substring(inicioCpf + 15, inicioCpf + 28);
        linha = linha.replaceAll(cpf, "(SELECT id_cliente FROM dbDevMPO.mpodigital.cliente WHERE cd_cpf_cnpj = " + cpf + ")");
        linha = linha.replaceAll(cpfAgente, "(SELECT cd_usu FROM dbDevMPO.mpodigital.assessor WHERE tx_cpf = " + cpfAgente + ")");
        return linha;
    }

    private static String replaceTable(String linha, String table) {
        return linha.replaceAll("\"MY_TABLE\"", "dbProdMPO.mpodigital." + table).concat("\r");
    }

    private static String getQueryAgenteCliente(String linha) {
        String query = "UPDATE dbDevMPO.mpodigital.cliente " + "SET id_carteira = (SELECT id_carteira FROM dbDevMPO.mpodigital.carteira c " + "INNER JOIN dbDevMPO.mpodigital.assessor a on a.id_assessor = c.id_assessor " + "WHERE a.tx_cpf = ':cpfAgente') " + "WHERE cd_cpf_cnpj = ':cpfCliente'";
        String[] split = linha.split(",");
        query = query.replace(":cpfAgente", split[1]);
        query = query.replace(":cpfCliente", split[0]);
        return query;
    }

    private static StringBuilder getStringBuilder(StringBuilder result) throws IOException {
        File file = new File("C:\\Users\\romul\\Documents\\WPE\\docs\\adece\\importacao_edinheiro\\PARCELAS_PAGAS_PRIMEIRO_IMPORT_2_tratado.sql");
        Files.write(file.toPath(), result.toString().getBytes());
        result = new StringBuilder();
        return result;
    }
}