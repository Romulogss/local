import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CNABUtils {
    /**
     * Método que pega o dado bancário e verifica se ele possui o dígito e retorno o dado sem o dígito.
     *
     * @param contaCorrente dado bancário
     * @param tamanho       tamanho do campo no arquivo CNAB.
     * @return o dado bancário sem o dígito
     */
    public static String getDadoBancario(String contaCorrente, int tamanho) {
        if (contaCorrente == null || contaCorrente.isEmpty()) {
            return leftPadWithZeros(null, tamanho);
        } else {
            contaCorrente = contaCorrente.replace(" ", "");
            contaCorrente = contaCorrente.replace(".", "");
            contaCorrente = contaCorrente.replaceAll("[^\\d-]", "");
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

    /**
     * Método que pega o dado bancário e verifica se ele possui dígito e retorno só o dígito, se possuir.
     *
     * @param contaCorrente dado bancário
     * @return dígito do dado bancário.
     */
    public static String getDigitoDadoBancario(String contaCorrente) {
        String digito = " ";
        contaCorrente = removerMascaras(contaCorrente);
        if (isNotBlank(contaCorrente)) {
            contaCorrente = contaCorrente.replace(" ", "");
            contaCorrente = contaCorrente.replace(".", "");
            contaCorrente = contaCorrente.replaceAll("[^\\d-]", "");
        } else {
            return " ";
        }
        if (contaCorrente.equals("0001") || Long.parseLong(contaCorrente) == 1) {
            digito = "9";
        } else if (contaCorrente.length() > 4) {
            digito = contaCorrente.substring(contaCorrente.length() - 1);
        }
        if (isBlank(digito)) {
            digito = " ";
        }
        return digito;
    }

    /**
     * Método que pega o CPF/CNPJ conforme o tipo de inscrição da linha
     *
     * @param tipoInscricao   string que representa se o número de inscrição é PF ou PJ.
     * @param numeroInscricao os 14 campos do CPF/CNPJ
     * @return o CPF ou CNPJ.
     */
    public static String getNumeroInscricaoByTipo(String tipoInscricao, String numeroInscricao) {
        if (tipoInscricao == null || numeroInscricao == null) {
            return null;
        }
        if (tipoInscricao.equals("1")) {
            return numeroInscricao.substring(numeroInscricao.length() - 11);
        } else {
            return numeroInscricao.substring(numeroInscricao.length() - 14);
        }
    }

    /**
     * Método que recebe um valor o transforma num campo númerico CNAB, conforme tamanho fornecido
     *
     * @param valor   valor do campo
     * @param tamanho tamanho do campo
     * @return o campo formatado.
     */
    public static String tratarCampoNumerico(String valor, int tamanho) {
        if (!isNumeric(valor)) {
            valor = "";
        }
        String result = leftPadWithZeros(valor.replaceAll(" ", ""), tamanho);
        return result.substring(0, tamanho);
    }

    /**
     * Método que recebe um valor o transforma num campo alfanúmerico CNAB, conforme tamanho fornecido
     *
     * @param valor   valor do campo
     * @param tamanho tamanho do campo
     * @return o campo formatado.
     */
    public static String tratarCampoAlfNumerico(String valor, int tamanho) {
        valor = removerAcentos(valor);
        valor = removeCaracteresEspeciais(valor);
        String result = rightPadWithSpaces(valor, tamanho);
        return removerAcentos(result).substring(0, tamanho).toUpperCase();
    }

    /**
     * Método que recebe um BigDecimal e o transforma num campo do formato CNAB.
     *
     * @param valor   objeto com valor BigDecimal
     * @param tamanho tamanho do campo
     * @return string que representa o bigdeciaml em padrão CNAB.
     */
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

    /**
     * Método que recebe uma string que representa um valor número, em formato CNAB e transforma em BigDecimal.
     *
     * @param valor campo número CNAB.
     * @return valor em BigDecimal
     */
    public static BigDecimal valorParaBigDecimal(String valor) {
        String casasDecimais = valor.substring(valor.length() - 2);
        String valorSemCasasDecimais = valor.substring(0, valor.length() - 2);
        valor = valorSemCasasDecimais + "." + casasDecimais;
        return new BigDecimal(valor);
    }

    public static String bigDecimalParaMoeda(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return "R$ " + nf.format(Double.valueOf(valor.toString()));
    }

    /**
     * Método que recebe uma string que representa um valor de data, em formato CNAB e transforma em LocalDate.
     *
     * @param valor campo data CNAB.
     * @return valor em LocalDate
     */
    public static LocalDate valorParaDate(String valor) {
        String dia = valor.substring(0, 2);
        String mes = valor.substring(2, 4);
        String ano = valor.substring(4, 8);
        return LocalDate.of(Integer.parseInt(ano), Integer.parseInt(mes), Integer.parseInt(dia));
    }

    /**
     * Método que recebe uma linha de registro de um lote CNAB e retorna o número do lote
     *
     * @param linha registro num lote CNAB
     * @return número do lote.
     */
    public static Long getNumeroLote(String linha) {
        return Long.parseLong(linha.substring(3, 7));
    }

    /**
     * Método que recebe uma linha de registro de um lote CNAB e retorna o número sequencial do registro.
     *
     * @param linha linha de registro de um lote CNAB
     * @return número sequencial do registro
     */
    public static Long getNumeroDeRegistro(String linha) {
        return Long.parseLong(linha.substring(8, 13));
    }

    public static String getMotivoOcorrencia(String linha) {
        return linha.substring(213, 223);
    }

    /**
     * Método que recebe uma linha de registro de um lote CNAB e um valor de BigDecimal do campo de início informado.
     *
     * @param linha linha de registro de um lote CNAB
     * @return valor de BigDecimal do campo
     */
    public static BigDecimal valorParaBigDecimalDaLinha(String linha, int inicio) {
        String valor = valorParaStringDaLinha(linha, inicio, 15);
        return valorParaBigDecimal(valor);
    }

    /**
     * Método que recebe uma linha de registro de um lote CNAB e um valor de LocalDate do campo de início informado.
     *
     * @param linha linha de registro de um lote CNAB
     * @return valor de LocalDate do campo
     */
    public static LocalDate valorParaDateDaLinha(String linha, int inicio) {
        String valor = valorParaStringDaLinha(linha, inicio, 8);
        valor = valor.equals("00000000") || valor.trim().isEmpty() ? "01012000" : valor;
        return valorParaDate(valor);
    }

    /**
     * Método que recebe uma linha de registro de um lote CNAB e retorna os dados dentro do campo localizado pelo início
     * e tamanho.
     *
     * @param linha   linha de registro de um lote CNAB
     * @param inicio  início do campo
     * @param tamanho tamanho do campo
     * @return o campo
     */
    public static String valorParaStringDaLinha(String linha, int inicio, int tamanho) {
        return linha.substring(inicio, inicio + tamanho);
    }

    /**
     * Método que recebe o campo Motivos Ocorrencia de um registro de um lote CNAB e retorna cada ocorrencia separada,
     * numa lista de string.
     *
     * @param motivos campo de motivo de ocorrencias
     * @return lista com cada código de motivo de ocorrencia separadamente.
     */
    public static List<String> getMotivosOcorrencia(String motivos) {
        List<String> resultado = new ArrayList<>();
        for (int i = 0; i < motivos.length(); i += 2) {
            String motivo = motivos.substring(i, i + 2);
            if (isNotBlank(motivo)) {
                resultado.add(motivo);
            }
        }
        return resultado;
    }

    /**
     * Método que remove caracteres especiais e ainda mantém o espaço
     *
     * @param valor string com caracteres especiais.
     * @return string sem os caracteres especiais.
     */
    public static String removeCaracteresEspeciais(String valor) {
        if (isBlank(valor)) {
            return "";
        }
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

    public static String leftPadWithZeros(String input, int expectedSize) {
        if (input == null) {
            return leftPadWithZeros("", expectedSize);
        } else {
            StringBuilder sb = new StringBuilder(expectedSize);

            for(int i = expectedSize - input.length(); i > 0; --i) {
                sb.append("0");
            }

            sb.append(input);
            return sb.toString();
        }
    }

    public static String removerMascaras(String campo) {
        return campo != null ? campo.replaceAll("[^a-zA-Z0-9]", "") : null;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
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

    public static String removerAcentos(String str) {
        return str != null && !str.isEmpty() ? Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") : str;
    }

    public static String rightPadWithSpaces(String input, int expectedSize) {
        if (input == null) {
            return rightPadWithSpaces("", expectedSize);
        } else {
            StringBuilder sb = new StringBuilder(expectedSize);
            sb.append(input);

            for(int i = expectedSize - input.length(); i > 0; --i) {
                sb.append(" ");
            }

            return sb.toString();
        }
    }
}
