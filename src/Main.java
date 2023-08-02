import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    static Set<String> ids = new HashSet<>();
    static List<String> idsDuplicados = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String sourceFile = "C:\\Users\\romul\\Downloads\\comprovantes_duplicados_demais_bancos";
        pegarCpfs(sourceFile);
    }

    private static void contarValor(String sourceFile) {
        File sourceFolder = new File(sourceFile);
        File[] files = sourceFolder.listFiles();
        BigDecimal total = BigDecimal.ZERO;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String realValor;
                    BigDecimal valorReal;
                    String[] valor = file.getName().split("- R\\$");
                    realValor = valor[1].split("_")[0].trim();
                    realValor = realValor.replace(',', '.').replace("$", "").trim();
                    try {
                        valorReal = new BigDecimal(realValor);
                        total = total.add(valorReal);
                    } catch (NumberFormatException e) {
                        System.out.println("Erro ao converter valor: " + realValor);
                    }
                }
            }
            System.out.println(total);
        }
    }

    private static void pegarCpfs(String sourceFile) throws IOException {
        File sourceFolder = new File(sourceFile);
        File[] files = sourceFolder.listFiles();
        List<String> cpfs = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String[] nome = file.getName().split("-");
                    String cpf = nome[2].trim();
                    cpf = cpf.substring(cpf.length() - 11);
                    cpfs.add(cpf);
                }
            }
        }
        String dados = cpfs.stream().map(c -> "'" + c + "',\n").collect(Collectors.joining());
        Files.write(new File("C:\\Users\\romul\\Downloads\\comprovantes_duplicados_demais_bancos\\cpfs.txt").toPath(), dados.getBytes());
    }

    private static int countFilesWithUnderscore(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        int count = 0;

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().contains("_")) {
                    count++;
                }
            }
        }

        return count;
    }

    private static void moveFilesWithUnderscore(String sourceFolderPath, String destinationFolderPath) {
        File sourceFolder = new File(sourceFolderPath);
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().contains("104 -") && file.getName().contains("_")) {
                    Path sourcePath = file.toPath();
                    Path destinationPath = Paths.get(destinationFolderPath, file.getName());

                    try {
                        Files.move(sourcePath, destinationPath);
                        System.out.println("Arquivo " + file.getName() + " movido com sucesso.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void pegarBonusDuplicidade() throws IOException {
        List<String> linhas = Files.readAllLines(new File("C:\\Users\\romul\\Downloads\\RETORNO_000224_01082023_6307.RET.txt").toPath(), StandardCharsets.UTF_8);
        StringBuilder novoArquivo = new StringBuilder();
        novoArquivo.append("Nome, Banco, Agencia, Conta, Valor, CPF, Protocolo\n");
        for (String linha : linhas) {
            if (linha.charAt(13) == 'A') {
                novoArquivo.append("\n");
                novoArquivo.append(linha, 43, 73).append(", ");
                novoArquivo.append(linha, 20, 23).append(", ");
                novoArquivo.append(linha, 23, 29).append(", ");
                novoArquivo.append(linha, 29, 42).append(", ");
                String valorPago = linha.substring(119, 134);
                BigDecimal valorPagoBigDecimal = new BigDecimal(valorPago.substring(0, valorPago.length() - 2) + "." + valorPago.substring(valorPago.length() - 2));
                novoArquivo.append(valorPagoBigDecimal).append(", ");

            } else if (linha.charAt(13) == 'B') {

                novoArquivo.append(linha, 18, 32).append(", ");
            } else if (linha.charAt(13) == 'Z') {
                novoArquivo.append(linha, 78, 103).append("\n");
            }
        }
        novoArquivo = new StringBuilder(novoArquivo.toString().replaceAll("(?m)^[ \t]*\r?\n", ""));
        Files.write(new File("C:\\Users\\romul\\Downloads\\duplicidades_bonus.txt").toPath(), novoArquivo.toString().getBytes());
    }

    private static void validarLinhas() throws IOException {
        List<String> linhas = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\bonus\\remessa-cancelamento353-corrigido.txt").toPath(), StandardCharsets.UTF_8);
        for (String linha : linhas) {
            if (linha.length() != 240) {
                System.out.println("Linha com tamanho diferente de 240: " + linha);
            }
        }
    }

    private static void testeBD() {
        BigDecimal valorCredito = new BigDecimal("10000.00");
        BigDecimal valorParcela = new BigDecimal("1346.86");
        double ap1 = 2500.00;
        BigDecimal ap1bd = new BigDecimal(ap1);
        double ap2 = 2500.00;
        BigDecimal ap2bd = new BigDecimal(ap2);
        double ap3 = 2500.00;
        BigDecimal ap3bd = new BigDecimal(ap3);
        double ap4 = 2500.00;
        BigDecimal ap4bd = new BigDecimal(ap4);
        double p1 = (ap1 / valorCredito.doubleValue()) * 100;
        double p2 = (ap2 / valorCredito.doubleValue()) * 100;
        double p3 = (ap3 / valorCredito.doubleValue()) * 100;
        double p4 = (ap4 / valorCredito.doubleValue()) * 100;
        BigDecimal p1bd = ap1bd.divide(valorCredito, 2, RoundingMode.HALF_DOWN).multiply(new BigDecimal(100));
        BigDecimal p2bd = ap2bd.divide(valorCredito, 2, RoundingMode.DOWN).multiply(new BigDecimal(100));
        BigDecimal p3bd = ap3bd.divide(valorCredito, 2, RoundingMode.DOWN).multiply(new BigDecimal(100));
        BigDecimal p4bd = ap4bd.divide(valorCredito, 2, RoundingMode.DOWN).multiply(new BigDecimal(100));
        double vp1 = (p1 * valorParcela.doubleValue()) / 100;
        double vp2 = (p2 * valorParcela.doubleValue()) / 100;
        double vp3 = (p3 * valorParcela.doubleValue()) / 100;
        double vp4 = (p4 * valorParcela.doubleValue()) / 100;
        BigDecimal vp1bd = p1bd.multiply(valorParcela).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal vp2bd = p2bd.multiply(valorParcela).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal vp3bd = p3bd.multiply(valorParcela).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal vp4bd = p4bd.multiply(valorParcela).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        System.out.println("Valor Credito: " + valorCredito);
        System.out.println("Valor Parcela: " + valorParcela);
        System.out.println("---Double---");
        System.out.println("AP1: " + ap1);
        System.out.println("AP2: " + ap2);
        System.out.println("AP3: " + ap3);
        System.out.println("AP4: " + ap4);
        System.out.println("P1: " + p1);
        System.out.println(new BigDecimal(p1).setScale(2, RoundingMode.HALF_DOWN).doubleValue());
        System.out.println("P2: " + p2);
        System.out.println("P3: " + p3);
        System.out.println("P4: " + p4);
        System.out.println("VP1: " + vp1);
        System.out.println((new BigDecimal(p1).setScale(2, RoundingMode.HALF_DOWN).doubleValue() / 100) * valorParcela.doubleValue());
        System.out.println("VP2: " + vp2);
        System.out.println("VP3: " + vp3);
        System.out.println("VP4: " + vp4);
        System.out.println("---BigDecimal---");
        System.out.println("AP1: " + ap1bd);
        System.out.println("AP2: " + ap2bd);
        System.out.println("AP3: " + ap3bd);
        System.out.println("AP4: " + ap4bd);
        System.out.println("P1: " + p1bd);
        System.out.println("P2: " + p2bd);
        System.out.println("P3: " + p3bd);
        System.out.println("P4: " + p4bd);
        System.out.println("VP1: " + vp1bd);
        System.out.println("VP2: " + vp2bd);
        System.out.println("VP3: " + vp3bd);
        System.out.println("VP4: " + vp4bd);
    }

    private static void bonus() throws IOException {
        List<String> participante = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\bonus_edinheiro.csv").toPath(), StandardCharsets.UTF_8);
        List<String> bonusAvulso = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\bonus_avulso.csv").toPath(), StandardCharsets.UTF_8);
        StringBuilder dados = new StringBuilder();
        for (String linha : participante) {
            Long idParticipante = Long.valueOf(linha.split(",")[0]);
            for (String b : bonusAvulso) {
                Long idPBonus = Long.valueOf(b.split(",")[2]);
                if (idPBonus.equals(idParticipante)) {
                    b = b.replace("\r", "");
                    dados.append(b).append(",").append(linha.split(",")[1]).append("\r");
                }
            }
        }
        Files.write(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\bonus_edinheiro_4.csv").toPath(), dados.toString().getBytes());
    }

    public static void criarCSVFromRemessa() throws IOException {
        StringBuilder dados = new StringBuilder("NN, CPF\r");
        List<String> linhas = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\cobranca\\remessa-cobranca000242.txt").toPath(), StandardCharsets.UTF_8);
        for (String linha : linhas) {
            if (linha.charAt(13) == 'P') {
                String NN = linha.substring(51, 57);
                dados.append(Long.valueOf(NN)).append(",");
            } else if (linha.charAt(13) == 'Q') {
                dados.append(linha, 22, 33).append(",").append(linha, 33, 73).append("\r");
            }
        }
        Files.write(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\validacoes\\cobranca000242AR.csv").toPath(), dados.toString().getBytes());
    }

    public static void criarCSVFromRemessaP() throws IOException {
        StringBuilder dados = new StringBuilder("VALOR, CPF\r");
        List<String> linhas = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\bonus\\remessa-bonus000240.txt").toPath(), StandardCharsets.UTF_8);
        for (String linha : linhas) {
            if (linha.charAt(13) == 'A') {
                dados.append(linha, 119, 134).append(",");
            } else if (linha.charAt(13) == 'B') {
                dados.append(linha, 21, 32).append("\r");
            }
        }
        Files.write(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\validacoes\\bonus000240AR.csv").toPath(), dados.toString().getBytes());
    }

    public static void validarassociacaoNomeParcela() throws IOException {
        List<String> linhasDB = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\validacoes\\cobranca000242DB.csv").toPath(), StandardCharsets.UTF_8);
        List<String> linhasRemessa = Files.readAllLines(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\validacoes\\cobranca000242AR.csv").toPath(), StandardCharsets.UTF_8);
        StringBuilder dados = new StringBuilder();
        int contador = 0;
        for (String linhaD : linhasDB) {
            for (String linhaR : linhasRemessa) {
                if (linhaD.split(",")[0].equals(linhaR.split(",")[0]) &&
                        !linhaD.split(",")[1].equals(linhaR.split(",")[1])) {
                    contador++;
                    dados.append(linhaD).append(",").append(linhaR.split(",")[1]).append(",").append(linhaR.split(",")[2]).append("\r");
                }
            }
        }
        System.out.println("Total: " + contador);
//        Files.write(new File("C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\CNAB\\validacoes\\remessa000226ARcorreta_avulsa.csv").toPath(), dados.toString().getBytes());
    }

    public static String removeCaracteresEspeciais(String valor) {
        valor = valor.replace("-", "");
        valor = valor.replace(".", "");
        valor = valor.replace("/", "");
        valor = valor.replace("\\", "");
        valor = valor.replace("|", "");
        valor = valor.replace(">", "");
        valor = valor.replace("<", "");
        valor = valor.replace("(", "");
        valor = valor.replace(")", "");
        valor = valor.replace("=", "");
        valor = valor.replace(" ", "");
        valor = valor.replace(":", "");
        valor = valor.replace(";", "");
        valor = valor.replace(",", "");
        valor = valor.replace("?", "");
        valor = valor.replace("!", "");
        valor = valor.replace("@", "");
        valor = valor.replace("#", "");
        valor = valor.replace("$", "");
        valor = valor.replace("%", "");
        valor = valor.replace("¨", "");
        valor = valor.replace("&", "");
        valor = valor.replace("*", "");
        valor = valor.replace("_", "");
        valor = valor.replace("+", "");
        valor = valor.replace("´", "");
        valor = valor.replace("`", "");
        valor = valor.replace("ª", "");
        valor = valor.replace("º", "");
        valor = valor.replace("§", "");
        valor = valor.replace("¬", "");
        valor = valor.replace("¢", "");
        valor = valor.replace("£", "");
        valor = valor.replace("³", "");
        valor = valor.replace("²", "");
        valor = valor.replace("¹", "");
        valor = valor.replace("°", "");
        valor = valor.replace("¨", "");
        return valor;
    }

    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        } else if ("".equals(text.trim())) {
            return "";
        } else {
            String validName = new String(text.getBytes(), StandardCharsets.UTF_8);
            validName = Normalizer.normalize(validName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            validName = validName.replaceAll("[^a-zA-Z0-9.-]", " ").replaceAll("[ ]+", " ");
            return !"".equals(validName.trim()) ? (validName.charAt(0) == ' ' ? validName.substring(1) : validName) : "";
        }
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

    public static void lerArquivo(String pathEntrada, String pathSaida, Function<String, String> tratarLinha) throws IOException {
        FileInputStream stream = new FileInputStream(pathEntrada);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        String linha = br.readLine();
        StringBuilder result = new StringBuilder();
        while (linha != null) {
            result.append(tratarLinha.apply(linha)).append("\r");
            linha = br.readLine();
        }
        System.out.println(ids.size());
        System.out.println(idsDuplicados.size());
        File file = new File(pathSaida);
        Files.write(file.toPath(), result.toString().getBytes());
    }

    public static void tratarUpdateParticipante() throws IOException {
        String entrada = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\cpf_participacao.csv";
        String saida = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\segunda\\cpf_participacao.csv";
        lerArquivo(entrada, saida, Main::_tratarUpdateParticipantes);
    }

    public static String _tratarUpdateParticipantes(String linha) {
        String novaLinha = "UPDATE mpodigital.MCRED_PARTICIPANTE_CONTRATO SET PC_VALOR_PARTICIPACAO = :participacao WHERE CPF_PARTICIPANTE = ':cpfParticipante' AND PC_VALOR_PARTICIPACAO IS NULL;";
        String[] split = linha.split(",");
        novaLinha = novaLinha.replace(":participacao", split[1]);
        novaLinha = novaLinha.replace(":cpfParticipante", split[0]);
        return novaLinha;
    }

    public static void tratarClientesSemCartira() throws IOException {
        String entrada = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\clientes_sem_carteira.csv";
        String saida = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\clientes_sem_carteira.sql";
        lerArquivo(entrada, saida, Main::_tratarClientesSemCarteira);
    }

    public static String _tratarClientesSemCarteira(String linha) {
        String novaLinha = "UPDATE mpodigital.cliente SET id_carteira = :idCarteira WHERE id_cliente = :idCliente AND id_carteira IS NULL;";
        String[] split = linha.split(",");
        novaLinha = novaLinha.replace(":idCliente", split[0]);
        novaLinha = novaLinha.replace(":idCarteira", split[1]);
        if (!ids.add(split[0])) {
            idsDuplicados.add(split[0]);
            System.out.println("Duplicado: " + split[0]);
        }
        return novaLinha;
    }

    public static void tratarContratosSemAgente() throws IOException {
        String entrada = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\contrato_cpf_agente.csv";
        String saida = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\contrato_cpf_agente.sql";
        lerArquivo(entrada, saida, Main::_tratarContratosSemAgente);
    }

    public static String _tratarContratosSemAgente(String linha) {
        String novaLinha = "UPDATE mpodigital.MCRED_CONTRATO SET CPF_AGENTE_CREDITO = ':cpfAgente' WHERE NUMERO_CONTRATO = :idContrato AND CPF_AGENTE_CREDITO IS NULL;";
        String[] split = linha.split(",");
        novaLinha = novaLinha.replace(":idContrato", split[0]);
        novaLinha = novaLinha.replace(":cpfAgente", split[1]);
        if (!ids.add(split[0])) {
            idsDuplicados.add(split[0]);
            System.out.println("Duplicado: " + split[0]);
        }
        return novaLinha;
    }

    private static void tratarDataPagamento() throws IOException {
        String entrada = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\nosso_numero.csv";
        String saida = "C:\\Users\\romul\\OneDrive\\Documentos\\WPE\\docs\\adece\\importacao_edinheiro\\nosso_numero_data_formatada.csv";
        lerArquivo(entrada, saida, Main::_tratarDataPagamento);
    }

    public static String _tratarDataPagamento(String linha) {
        String[] split = linha.split(",");
        String date = split[1];
        DateTimeFormatter formatterOrigem = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(date, formatterOrigem);
        Instant instant = data.atStartOfDay(ZoneOffset.UTC).toInstant();
        String tsPagamento = String.valueOf(Timestamp.from(instant).getTime());
        return linha.concat(",").concat(tsPagamento.substring(0, Math.min(tsPagamento.length(), 10)));
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

    public static String _tratarDadosBancarios(String linha) {
        linha = replaceTable(linha, "conta");
        linha = getSubQueryCliente(linha);
        return linha;
    }

    private static String getSubQueryCliente(String linha) {
        int inicioCpf = linha.indexOf("('") + 1;
        String cpf = linha.substring(inicioCpf, inicioCpf + 13);
        return linha.replaceAll(cpf, "(SELECT id_cliente FROM mpodigital.cliente WHERE cd_cpf_cnpj = " + cpf + ")");
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
        return linha.replaceAll("MY_TABLE", "mpodigital." + table).concat("\r");
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

    public static String removerMascaras(String campo) {
        return campo != null ? campo.replaceAll("[^a-zA-Z0-9]", "") : null;
    }

    public static BigDecimal valorParaBigDecimal(String valor) {
        String casasDecimais = valor.substring(valor.length() - 2);
        String valorSemCasasDecimais = valor.substring(0, valor.length() - 2);
        valor = valorSemCasasDecimais + "." + casasDecimais;
        return new BigDecimal(valor);
    }

    public static String tratarValor(BigDecimal valor, int tamanho) {
        valor = (BigDecimal) tratarNull(valor, BigDecimal.ZERO.toString());
        String valorStr = valor.toString().replace(".", "").replace(",", "");
        return tratarCampoNumerico(valorStr, tamanho);
    }

    public static Object tratarNull(Object valor, String original) {
        valor = valor != null ? valor : original;
        if (valor instanceof String) {
            valor = removerAcentos((String) valor);
        }
        return valor;
    }

    public static String removerAcentos(String str) {
        return str != null && !str.isEmpty() ? Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") : str;
    }

    static class Pagamento {
        BigDecimal valor;
        String cpf;
    }
}